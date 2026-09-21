package com.atguigu.study.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LllmConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String baseUrl;

    @Bean("plusChatModel")
    public ChatModel plusChatModel() {
        OllamaApi api = OllamaApi.builder()
                .baseUrl(baseUrl)
                .build();
        return OllamaChatModel.builder()
                .ollamaApi(api)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("qwen2.5:7b")
                        .temperature(0.8)
                        .build())
                .build();
    }

    @Bean("flashChatModel")
    public ChatModel flashChatModel() {
        OllamaApi api = OllamaApi.builder()
                .baseUrl(baseUrl)
                .build();
        return OllamaChatModel.builder()
                .ollamaApi(api)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("qwen2.5:7b")
                        .temperature(0.3)
                        .build())
                .build();
    }

    @Bean("turboChatModel")
    public ChatModel turboChatModel() {
        OllamaApi api = OllamaApi.builder()
                .baseUrl(baseUrl)
                .build();
        return OllamaChatModel.builder()
                .ollamaApi(api)
                .defaultOptions(OllamaChatOptions.builder()
                        .model("deepseek-r1:7b")
                        .temperature(0.7)
                        .build())
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        OllamaApi api = OllamaApi.builder()
                .baseUrl(baseUrl)
                .build();
        return OllamaEmbeddingModel.builder()
                .ollamaApi(api)
                .defaultOptions(OllamaEmbeddingOptions.builder()
                        .model("nomic-embed-text")
                        .build())
                .build();
    }
}