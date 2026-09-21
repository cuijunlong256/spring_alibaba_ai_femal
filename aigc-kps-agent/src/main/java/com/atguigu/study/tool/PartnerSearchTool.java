package com.atguigu.study.tool;

import com.atguigu.study.domain.MatchmakerRecommendation;
import com.atguigu.study.domain.Partner;
import com.atguigu.study.mapper.MatchmakerRecommendationMapper;
import com.atguigu.study.pgmapper.PartnerMapper;
import com.atguigu.study.service.ai.RecommendationContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PartnerSearchTool {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private PartnerMapper partnerMapper;

    @Autowired
    private MatchmakerRecommendationMapper recommendationMapper;

    @Tool(description = "根据用户的喜好描述，从知识库中搜索匹配的明星对象。query 是用户的喜好描述，gender 是目标性别(female/male)")
    public String searchPartners(String query, String gender) {
        return searchPartners(query, gender, null);
    }

    public String searchPartners(String query, String gender, Long sessionId) {
        // 1. 提取过滤条件
        String zodiac = extractZodiac(query);
        int[] ageRange = extractAgeRange(query);
        String nationality = extractNationality(query);
        List<String> keywords = extractKeywords(query);
        String refType = detectReferenceType(query); // "filter" 筛选型 / "exclude" 排除型 / null 无指代

        // 获取已推荐的明星ID（用于排除）
        List<String> recommendedIds;
        if (sessionId != null) {
            List<MatchmakerRecommendation> recs = recommendationMapper.selectBySessionId(sessionId);
            recommendedIds = recs != null
                    ? recs.stream().map(MatchmakerRecommendation::getPartnerId).toList()
                    : new ArrayList<>();
        } else {
            recommendedIds = new ArrayList<>();
        }

        List<Partner> partners;

        // 2. 排除型指代："除了她们"、"还有其他人吗" → 排除已推荐，重新检索
        if ("exclude".equals(refType)) {
            if (zodiac != null || ageRange != null || nationality != null) {
                // 有精确条件 → 全表过滤 + 排除已推荐
                List<Partner> all = partnerMapper.selectByGender(gender);
                partners = all.stream()
                        .filter(p -> {
                            if (recommendedIds.contains(p.getId())) return false;
                            if (zodiac != null && !zodiac.equals(p.getZodiac())) return false;
                            if (nationality != null && !nationality.equals(p.getNationality())) return false;
                            if (ageRange != null && (p.getAge() == null || p.getAge() < ageRange[0] || p.getAge() > ageRange[1])) return false;
                            return true;
                        })
                        .collect(Collectors.toList());
            } else {
                // 无精确条件 → 向量检索 + 排除已推荐
                List<Document> docs = vectorStore.similaritySearch(
                        SearchRequest.builder().query(query).topK(20).build()
                );
                List<String> ids = docs.stream()
                        .filter(d -> gender.equals(d.getMetadata().get("gender")))
                        .map(d -> d.getMetadata().get("partnerId").toString())
                        .filter(id -> !recommendedIds.contains(id))
                        .toList();
                partners = ids.isEmpty() ? new ArrayList<>() : partnerMapper.selectByIds(ids);
            }

            // 关键词过滤
            if (!keywords.isEmpty() && !partners.isEmpty()) {
                List<Partner> filtered = partners.stream()
                        .filter(p -> matchKeywords(p, keywords))
                        .collect(Collectors.toList());
                if (!filtered.isEmpty()) partners = filtered;
            }
        }
        // 3. 筛选型指代："她们谁喜欢读书" → 从已推荐中筛选
        else if ("filter".equals(refType) && !recommendedIds.isEmpty()) {
            partners = partnerMapper.selectByIds(recommendedIds);
            if (!keywords.isEmpty()) {
                List<Partner> filtered = partners.stream()
                        .filter(p -> matchKeywords(p, keywords))
                        .collect(Collectors.toList());
                if (!filtered.isEmpty()) partners = filtered;
            }
        }
        // 4. 有精确条件（星座/年龄/国籍）→ 全表过滤
        else if (zodiac != null || ageRange != null || nationality != null) {
            List<Partner> all = partnerMapper.selectByGender(gender);
            partners = all.stream()
                    .filter(p -> {
                        if (zodiac != null && !zodiac.equals(p.getZodiac())) return false;
                        if (nationality != null && !nationality.equals(p.getNationality())) return false;
                        if (ageRange != null && (p.getAge() == null || p.getAge() < ageRange[0] || p.getAge() > ageRange[1])) return false;
                        return true;
                    })
                    .collect(Collectors.toList());

            if (!keywords.isEmpty() && !partners.isEmpty()) {
                List<Partner> filtered = partners.stream()
                        .filter(p -> matchKeywords(p, keywords))
                        .collect(Collectors.toList());
                if (!filtered.isEmpty()) partners = filtered;
            }
        }
        // 5. 无精确条件 → 语义向量检索
        else {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder().query(query).topK(15).build()
            );
            List<String> ids = docs.stream()
                    .filter(d -> gender.equals(d.getMetadata().get("gender")))
                    .map(d -> d.getMetadata().get("partnerId").toString())
                    .toList();
            if (ids.isEmpty()) return "暂时没有找到特别匹配的对象，可以换个描述试试~";
            partners = partnerMapper.selectByIds(ids);

            if (!keywords.isEmpty()) {
                List<Partner> filtered = partners.stream()
                        .filter(p -> matchKeywords(p, keywords))
                        .collect(Collectors.toList());
                if (!filtered.isEmpty()) partners = filtered;
            }
        }

        if (partners.isEmpty()) {
            if ("exclude".equals(refType)) return "已经把符合条件的都推荐过啦，要不要换个条件试试？";
            if ("filter".equals(refType)) return "刚才推荐的人里没有符合条件的，要不要我重新推荐一些？";
            return "暂时没有找到符合条件的对象，可以放宽条件试试~";
        }

        // 6. 存到上下文（排除型和正常推荐才存，筛选型不重复存）
        if (!"filter".equals(refType)) {
            List<String[]> recommended = new ArrayList<>();
            for (Partner p : partners) {
                recommended.add(new String[]{p.getId(), p.getName()});
            }
            RecommendationContext.setCurrentRecommendations(recommended);
        }

        // 7. 组装返回文本
        return partners.stream()
                .limit(5)
                .map(p -> String.format(
                        "【%s】%d岁，%s，%s，%s人。性格：%s。标签：%s。兴趣：%s。外貌风格：%s。理想型：%s。",
                        p.getName(),
                        p.getAge() != null ? p.getAge() : 0,
                        p.getZodiac() != null ? p.getZodiac() : "",
                        p.getCategory() != null ? p.getCategory() : "",
                        p.getNationality() != null ? p.getNationality() : "",
                        p.getPersonality() != null ? String.join("、", p.getPersonality()) : "",
                        p.getTags() != null ? String.join("、", p.getTags()) : "",
                        p.getInterests() != null ? String.join("、", p.getInterests()) : "",
                        p.getAppearance() != null && p.getAppearance().get("style") != null ? p.getAppearance().get("style") : "",
                        p.getIdealPartner() != null ? p.getIdealPartner() : ""
                ))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 检测指代词类型
     * @return "filter" 筛选型 / "exclude" 排除型 / null 无指代
     */
    private String detectReferenceType(String query) {
        if (query == null || query.isEmpty()) return null;

        // 排除型：除了、还有其他、还有别的、不要这些、换一批
        String[] excludeWords = {"除了", "还有其他", "还有别的", "还有谁", "还有吗",
                "换一批", "换一个", "换个", "不要这些", "不要她们", "不要他们", "其他人呢", "别人呢"};
        for (String w : excludeWords) {
            if (query.contains(w)) return "exclude";
        }

        // 筛选型：她们谁、这两个里面、她们中、谁喜欢、谁是
        String[] filterWords = {"她们谁", "他们谁", "这两个谁", "这两个里面", "她们中",
                "他们中", "谁喜欢", "谁是", "哪个喜欢", "哪个是"};
        for (String w : filterWords) {
            if (query.contains(w)) return "filter";
        }

        // 单纯指代但没有明确筛选/排除意图 → 视为排除（推荐新的）
        String[] simpleRef = {"她们", "他们", "这两个", "那两个", "刚才", "之前", "上面推荐"};
        for (String w : simpleRef) {
            if (query.contains(w)) return "exclude";
        }

        return null;
    }

    private boolean matchKeywords(Partner p, List<String> keywords) {
        List<String> allTexts = new ArrayList<>();
        if (p.getTags() != null) allTexts.addAll(Arrays.asList(p.getTags()));
        if (p.getPersonality() != null) allTexts.addAll(Arrays.asList(p.getPersonality()));
        if (p.getInterests() != null) allTexts.addAll(Arrays.asList(p.getInterests()));
        if (p.getIdealPartner() != null) allTexts.add(p.getIdealPartner());

        for (String kw : keywords) {
            for (String text : allTexts) {
                if (text != null && text.contains(kw)) return true;
            }
        }
        return false;
    }

    private String extractZodiac(String query) {
        if (query == null || query.isEmpty()) return null;
        String[] zodiacs = {"白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座",
                "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};
        for (String z : zodiacs) {
            if (query.contains(z)) return z;
        }
        return null;
    }

    /**
     * 提取国籍
     */
    private String extractNationality(String query) {
        if (query == null || query.isEmpty()) return null;
        if (query.contains("日本")) return "日本";
        if (query.contains("韩国") || query.contains("韩国人")) return "韩国";
        if (query.contains("中国") || query.contains("国内") || query.contains("内地")) return "中国";
        if (query.contains("美国")) return "美国";
        if (query.contains("英国")) return "英国";
        return null;
    }

    private int[] extractAgeRange(String query) {
        if (query == null || query.isEmpty()) return null;

        // 匹配范围：30到40、30-40、30~40、30至40
        Pattern rangePattern = Pattern.compile("(\\d{1,2})\\s*(?:到|至|-|~|—)\\s*(\\d{1,2})");
        Matcher rangeMatcher = rangePattern.matcher(query);
        if (rangeMatcher.find()) {
            int min = Integer.parseInt(rangeMatcher.group(1));
            int max = Integer.parseInt(rangeMatcher.group(2));
            return new int[]{Math.min(min, max), Math.max(min, max)};
        }

        // 匹配"35左右"、"30岁左右"、"30上下" → ±5岁
        Pattern aroundPattern = Pattern.compile("(\\d{1,2})\\s*(?:岁)?\\s*(?:左右|上下|前后|差不多)");
        Matcher aroundMatcher = aroundPattern.matcher(query);
        if (aroundMatcher.find()) {
            int age = Integer.parseInt(aroundMatcher.group(1));
            return new int[]{age - 5, age + 5};
        }

        // 匹配"30岁以上"
        Pattern minPattern = Pattern.compile("(?:大于|超过|以上|不小于|高于)\\s*(\\d{1,2})");
        Matcher minMatcher = minPattern.matcher(query);
        if (minMatcher.find()) {
            return new int[]{Integer.parseInt(minMatcher.group(1)), 120};
        }

        // 匹配"30岁以下"
        Pattern maxPattern = Pattern.compile("(?:小于|低于|以下|不大于|不超过)\\s*(\\d{1,2})");
        Matcher maxMatcher = maxPattern.matcher(query);
        if (maxMatcher.find()) {
            return new int[]{0, Integer.parseInt(maxMatcher.group(1))};
        }

        return null;
    }

    private List<String> extractKeywords(String query) {
        List<String> keywords = new ArrayList<>();
        if (query == null || query.isEmpty()) return keywords;

        String[] candidates = {
                "知性", "文艺", "性感", "可爱", "甜美", "高冷", "御姐", "萝莉",
                "活泼", "开朗", "温柔", "善良", "独立", "自信", "优雅", "气质",
                "幽默", "直爽", "低调", "成熟", "稳重", "清纯", "仙气", "古典",
                "时尚", "潮流", "妩媚", "清新", "自然", "阳光", "运动", "健身",
                "音乐", "唱歌", "跳舞", "演戏", "摄影", "绘画", "阅读", "旅行",
                "美食", "烹饪", "购物", "游戏", "养猫", "养狗", "宠物", "读书"
        };
        for (String c : candidates) {
            if (query.contains(c)) keywords.add(c);
        }
        return keywords;
    }
}