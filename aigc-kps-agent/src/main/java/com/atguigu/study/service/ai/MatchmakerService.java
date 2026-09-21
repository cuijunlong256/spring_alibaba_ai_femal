package com.atguigu.study.service.ai;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.atguigu.study.domain.ConsultationSession;
import com.atguigu.study.dto.command.ConsultationSessionCreateDTO;
import com.atguigu.study.skill.CelebrityMatchTool;
import com.atguigu.study.tool.PartnerSearchTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 红娘核心服务（女性和男性共用）
 * 通过 type 参数区分：FEMALE_MATCHMAKER(推荐女性给男生) / MALE_MATCHMAKER(推荐男性给女生)
 */
@Slf4j
@Service
public class MatchmakerService {

    @Autowired
    private MatchmakerSessionService matchmakerSessionService;

    @Autowired
    private MatchmakerMessageService matchmakerMessageService;

    @Autowired
    private MatchmakerRecommendationService recommendationService;

    @Autowired
    @Qualifier("FemaleMatchmakerGraph")
    private CompiledGraph femaleMatchmakerGraph;

    @Autowired
    private CelebrityMatchTool celebrityMatchTool;


    @Autowired
    private PartnerSearchTool partnerSearchTool;

    // TODO: 后续接入男性红娘工作流后注入
     @Autowired
     @Qualifier("MaleMatchmakerGraph")
     private CompiledGraph maleMatchmakerGraph;

    /**
     * 创建会话
     * @param type FEMALE_MATCHMAKER / MALE_MATCHMAKER
     */
    public ConsultationSession startSession(Long userId, ConsultationSessionCreateDTO createDTO, String type) {
        ConsultationSession session = matchmakerSessionService.createSession(userId, createDTO, type);
        String greeting = "FEMALE_MATCHMAKER".equals(type)
                ? "嗨！我是你的专属红娘，想帮你找到心仪的女生～ 先说说你喜欢什么类型的吧？比如性格、外貌、气质都可以聊～"
                : "嗨！我是你的专属红娘，想帮你找到心仪的男生～ 先说说你喜欢什么类型的吧？比如性格、外貌、气质都可以聊～";
        matchmakerMessageService.saveAimessage(session.getId(), greeting, type.toLowerCase());
        return session;
    }

    /**
     * 流式对话
     */
    public Flux<NodeOutput> streamChat(Long sessionId, Long userId, String userMessage, String type) {
        log.info("Matchmaker.streamChat: sessionId={}, type={}, userMessage={}", sessionId, type, userMessage);

        //1.保存用户信息
        matchmakerMessageService.saveUserMessage(sessionId, userMessage);
        //2.获取历史
        String chatHistory  = matchmakerMessageService.formatHistoryAsText(sessionId);
        //3.查询用户已推荐过的明星ID（避免重复推荐）
        List<String> recommendedIds = recommendationService.getRecommendedPartnerIds(userId);
        String recommendedInfo = recommendedIds.isEmpty()
                ? "（暂无推荐记录）"
                : String.join(",", recommendedIds);

        // 4. 向量检索匹配的女生（不依赖模型工具调用，手动检索）
        String targetGender = "FEMALE_MATCHMAKER".equals(type) ? "female" : "male";
        String searchQuery = chatHistory + "\n用户最新消息：" + userMessage;
        String matchedPartners = "";
        try {
//            matchedPartners = partnerSearchTool.searchPartners(searchQuery, targetGender, sessionId);
            matchedPartners = celebrityMatchTool.celebrityMatch(searchQuery, targetGender, sessionId);
        } catch (Exception e) {
            log.warn("向量检索失败: {}", e.getMessage());
        }

        // 5. 拼接上下文
        String enrichedUserMessage = """
                【历史对话】
                %s

                【已推荐过的明星ID（不要再重复推荐这些人）】
                %s

                【匹配的女生资料】（如果用户有找对象意图就介绍，否则忽略）
                %s

                【用户最新消息】
                %s
                """.formatted(chatHistory, recommendedInfo, matchedPartners, userMessage);
        // 5. 调用对应性别的 Graph
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("userMessage", enrichedUserMessage);
        CompiledGraph graph = "FEMALE_MATCHMAKER".equals(type) ? femaleMatchmakerGraph  : maleMatchmakerGraph;
        return graph.stream(inputs).doOnError(e -> log.error("Matchmaker.streamChat error", e))
                .doOnComplete(() ->{
                    List<String[]> recommended = RecommendationContext.getCurrentRecommendations();
                    if (!recommended.isEmpty()){
                        recommendationService.saveRecommendations(sessionId,userId,recommended);
                        log.info("保存推荐记录 {} 条", recommended.size());
                    }
                    RecommendationContext.clear();
                    log.info("Matchmaker Graph 完成, sessionId={}", sessionId);
                });

    }
}