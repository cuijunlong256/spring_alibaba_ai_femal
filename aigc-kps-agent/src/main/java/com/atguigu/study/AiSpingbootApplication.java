package com.atguigu.study;

import com.alibaba.cloud.ai.a2a.autoconfigure.server.A2aServerAgentCardAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {A2aServerAgentCardAutoConfiguration.class})
public class AiSpingbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiSpingbootApplication.class, args);
    }
}