package org.springai.chatdemo.config;

import com.alibaba.cloud.ai.memory.redis.RedisChatMemory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Value("${spring.data.redis.host}")
	private String host;
	@Value("${spring.data.redis.port}")
	private int port;
	@Value("${spring.data.redis.password}")
	private String password;
	@Bean
	public MessageChatMemoryAdvisor redisMessageChatMemoryAdvisor() {

		return new MessageChatMemoryAdvisor(new RedisChatMemory(
				host,
				port,
				password
		));
	}

}
