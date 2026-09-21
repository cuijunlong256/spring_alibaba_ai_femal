package com.atguigu.study.skill;

import com.atguigu.study.dto.MatchRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 意图解析器：将用户自然语言输入解析为结构化的 MatchRequest
 * 纯规则实现，不依赖模型
 */
@Component
public class IntentParser {

    private static final List<String> ZODIACS = Arrays.asList(
            "白羊", "金牛", "双子", "巨蟹", "狮子", "处女",
            "天秤", "天蝎", "射手", "摩羯", "水瓶", "双鱼"
    );

    private static final List<String> NATIONALITIES = Arrays.asList(
            "中国", "日本", "韩国", "美国", "英国", "法国", "德国", "俄罗斯", "泰国"
    );

    public MatchRequest parse(String query, String gender, Long sessionId) {
        return MatchRequest.builder()
                .gender(gender)
                .ageRange(extractAgeRange(query))
                .zodiac(extractZodiac(query))
                .nationality(extractNationality(query))
                .fuzzyTags(extractFuzzyTags(query))
                .refType(detectReferenceType(query))
                .sessionId(sessionId)
                .rawQuery(query)
                .build();
    }

    /** 提取星座 */
    private String extractZodiac(String query) {
        for (String z : ZODIACS) {
            if (query.contains(z)) {
                return z + "座";
            }
        }
        return null;
    }

    /** 提取年龄范围 */
    private Integer[] extractAgeRange(String query) {
        // 匹配 "30-40岁"、"30到40岁"、"30~40岁"
        Pattern rangePattern = Pattern.compile("(\\d{1,3})\\s*[-~到至]\\s*(\\d{1,3})\\s*岁");
        Matcher m = rangePattern.matcher(query);
        if (m.find()) {
            return new Integer[]{Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))};
        }
        // 匹配 "30岁左右"、"30多岁"
        Pattern singlePattern = Pattern.compile("(\\d{1,3})\\s*(岁左右|多岁|岁上下)");
        Matcher m2 = singlePattern.matcher(query);
        if (m2.find()) {
            int age = Integer.parseInt(m2.group(1));
            return new Integer[]{age - 5, age + 5};
        }
        return null;
    }

    /** 提取国籍 */
    private String extractNationality(String query) {
        for (String n : NATIONALITIES) {
            if (query.contains(n)) {
                return n;
            }
        }
        return null;
    }

    /** 提取模糊标签（性格关键词） */
    private List<String> extractFuzzyTags(String query) {
        List<String> tags = new ArrayList<>();
        String[] tagWords = {
                "温柔", "知性", "文艺", "可爱", "活泼", "开朗", "内向", "外向",
                "高冷", "御姐", "萝莉", "性感", "清纯", "成熟", "天真", "独立",
                "贤惠", "顾家", "事业心", "浪漫", "幽默", "风趣", "阳光", "帅气"
        };
        for (String tag : tagWords) {
            if (query.contains(tag)) {
                tags.add(tag);
            }
        }
        return tags;
    }

    /** 检测指代类型 */
    private String detectReferenceType(String query) {
        String[] excludeWords = {"除了", "还有其他", "换一批", "别的", "另外的", "其他人"};
        String[] filterWords = {"她们谁", "他们谁", "这两个里面", "这几个里面", "当中谁", "里面谁"};

        for (String w : filterWords) {
            if (query.contains(w)) return "filter";
        }
        for (String w : excludeWords) {
            if (query.contains(w)) return "exclude";
        }
        return null;
    }
}