package org.springai.chatdemo.config;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Bean
	public MessageChatMemoryAdvisor redisMessageChatMemoryAdvisor() {

		return new MessageChatMemoryAdvisor(new RedisChatMemory(
				"127.0.0.1",
				6379,
				"123456"
		));
	}

}
