package com.atguigu.study.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.study.domain.Partner;
import com.atguigu.study.pgmapper.PartnerMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 明星数据导入工具
 * 读取两个 JSON 文件（男性知识库 + 女性知识库），
 * 将结构化数据存入 PostgreSQL partner 表，
 * 同时生成 embedding 存入 pgvector 向量表。
 *
 * 启动时自动执行，数据已存在则跳过。
 */
@Component  // 标记为Spring组件，自动注册到容器中
public class PartnerDataImporter implements CommandLineRunner {  // 实现CommandLineRunner接口，在Spring Boot应用启动后执行run方法

    @Autowired  // 自动注入PartnerMapper，用于数据库操作
    private PartnerMapper partnerMapper;

    @Autowired  // 自动注入VectorStore，用于向量存储
    private VectorStore vectorStore;

    @Value("${matchmaker.import.enabled:true}")  // 从配置文件中读取导入开关，默认为true
    private boolean importEnabled;

    @Override  // 重写CommandLineRunner的run方法，在应用启动时执行
    public void run(String... args) throws Exception {
        if (!importEnabled) {  // 检查是否启用导入功能
            System.out.println("[数据导入] 已跳过（matchmaker.import.enabled=false）");
            return;
        }

        // 如果已有数据，跳过导入
        Long count = partnerMapper.selectCount(null);  // 查询当前数据库中的数据量
        if (count != null && count > 0) {  // 如果数据量大于0，则跳过导入
            System.out.println("[数据导入] 数据已存在，跳过导入。当前数据量：" + count);
            return;
        }

        System.out.println("[数据导入] 开始导入明星数据...");

        // 导入女性知识库（女明星，gender=female）
        importFromJson("female_celebrities_kb.json", "female");

        // 导入男性知识库（男明星，gender=male）
        importFromJson("male_celebrities_kb.json", "male");

        System.out.println("[数据导入] 导入完成！");
    }

    /**
     * 从 JSON 文件导入数据
     *
     * @param fileName classpath 下的 JSON 文件名
     * @param gender   明星的性别（female/male）
     */
    private void importFromJson(String fileName, String gender) throws Exception {
        // 1. 读取 classpath 下的 JSON 文件
        ClassPathResource resource = new ClassPathResource(fileName);  // 从classpath中获取资源文件
        InputStream inputStream = resource.getInputStream();  // 获取输入流
        String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);  // 读取文件内容为字符串

        // 2. 解析 JSON，只取 profiles 数组
        JSONObject root = JSON.parseObject(json);  // 解析JSON对象
        JSONArray profiles = root.getJSONArray("profiles");  // 获取profiles数组

        if (profiles == null || profiles.isEmpty()) {  // 如果profiles为空或不存在，打印提示信息并返回
            System.out.println("[数据导入] " + fileName + " 中没有 profiles 数据");
            return;
        }

        System.out.println("[数据导入] 正在导入 " + fileName + "，共 " + profiles.size() + " 条");

        for (int i = 0; i < profiles.size(); i++) {  // 遍历profiles数组
            JSONObject profile = profiles.getJSONObject(i);  // 获取每个profile对象

            // 3. 转成 Partner 对象
            Partner partner = profile.toJavaObject(Partner.class);  // 将JSON对象转换为Partner对象
            // 根据文件名设置性别
            partner.setGender(gender);  // 设置性别属性

            // 4. 存入 PostgreSQL 结构化表
            partnerMapper.insert(partner);  // 插入数据库

            // 5. 拼接文本，生成 embedding 存入向量表
            String content = buildEmbeddingContent(partner);  // 构建用于生成向量的文本内容
            vectorStore.add(List.of(  // 将文档添加到向量存储
                    new Document(content, Map.of(  // 创建文档对象，包含内容和元数据
                            "partnerId", partner.getId(),  // 明星ID
                            "gender", gender,  // 性别
                            "name", partner.getName()  // 姓名
                    ))
            ));

            if ((i + 1) % 10 == 0) {  // 每处理10条数据打印一次进度
                System.out.println("[数据导入] 已导入 " + (i + 1) + "/" + profiles.size());
            }
        }

        System.out.println("[数据导入] " + fileName + " 导入完成");
    }

    /**
     * 拼接用于生成向量的文本内容
     * 把明星的关键信息拼成一段自然语言描述，便于语义检索
     */
    private String buildEmbeddingContent(Partner partner) {
        StringBuilder sb = new StringBuilder();  // 使用StringBuilder构建文本
        sb.append("姓名：").append(partner.getName()).append("。");  // 添加姓名信息
        sb.append("性别：").append("female".equals(partner.getGender()) ? "女" : "男").append("。");  // 添加性别信息
        sb.append("年龄：").append(partner.getAge()).append("岁。");  // 添加年龄信息
        sb.append("职业：").append(partner.getCategory()).append("。");  // 添加职业信息
        sb.append("国籍：").append(partner.getNationality()).append("。");  // 添加国籍信息
        sb.append("星座：").append(partner.getZodiac()).append("。");  // 添加星座信息
        sb.append("身高：").append(partner.getHeightCm()).append("cm。");  // 添加身高信息

        if (partner.getAppearance() != null) {  // 如果存在外貌信息
            sb.append("外貌：")  // 添加外貌信息
                    .append(partner.getAppearance().getOrDefault("face_shape", ""))  // 脸型
                    .append(partner.getAppearance().getOrDefault("skin_tone", ""))  // 肤色
                    .append(partner.getAppearance().getOrDefault("hair_style", ""))  // 发型
                    .append(partner.getAppearance().getOrDefault("body_type", ""))  // 体型
                    .append("。");
        }

        if (partner.getPersonality() != null) {  // 如果存在性格信息
            sb.append("性格：").append(String.join("、", partner.getPersonality())).append("。");  // 添加性格信息
        }
        if (partner.getInterests() != null) {  // 如果存在兴趣爱好信息
            sb.append("兴趣爱好：").append(String.join("、", partner.getInterests())).append("。");  // 添加兴趣爱好信息
        }
        if (partner.getTags() != null) {  // 如果存在标签信息
            sb.append("标签：").append(String.join("、", partner.getTags())).append("。");  // 添加标签信息
        }
        if (partner.getIdealPartner() != null) {  // 如果存在理想型信息
            sb.append("理想型：").append(partner.getIdealPartner()).append("。");  // 添加理想型信息
        }

        return sb.toString();  // 返回构建好的文本
    }
}