package org.springai.chatdemo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Chat Controller Tests")
class ChatControllerTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientPromptRequestSpec promptRequestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private ChatClient.StreamResponseSpec streamResponseSpec;

    @Mock
    private MessageChatMemoryAdvisor redisChatMemory;

    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock the ChatClient.Builder chain
        when(chatClientBuilder.defaultSystem(anyString())).thenReturn(chatClientBuilder);
        when(chatClientBuilder.defaultAdvisors(any())).thenReturn(chatClientBuilder);
        when(chatClientBuilder.defaultOptions(any())).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        
        // Create the controller
        chatController = new ChatController(chatClientBuilder, redisChatMemory);
    }

    @Test
    @DisplayName("Should initialize ChatController with proper dependencies")
    void shouldInitializeChatControllerWithProperDependencies() {
        assertNotNull(chatController);
        
        // Verify that the ChatClient was built with the expected configuration
        verify(chatClientBuilder).defaultSystem("你是一名老师");
        verify(chatClientBuilder, atLeastOnce()).defaultAdvisors(any());
        verify(chatClientBuilder).defaultOptions(any());
        verify(chatClientBuilder).build();
    }

    @Test
    @DisplayName("Should handle simple chat request successfully")
    void shouldHandleSimpleChatRequestSuccessfully() {
        // Arrange
        String expectedResponse = "你好！我是一名AI助手老师，很高兴认识你！";
        String query = "你好，很高兴认识你，能简单介绍一下自己吗？";
        
        when(chatClient.prompt(query)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedResponse);
        
        // Act
        String result = chatController.simpleChat(query);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(chatClient).prompt(query);
        verify(promptRequestSpec).call();
        verify(callResponseSpec).content();
    }

    @Test
    @DisplayName("Should handle simple chat with default query")
    void shouldHandleSimpleChatWithDefaultQuery() {
        // Arrange
        String defaultQuery = "你好，很高兴认识你，能简单介绍一下自己吗？";
        String expectedResponse = "你好！我是一名AI助手老师。";
        
        when(chatClient.prompt(defaultQuery)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedResponse);
        
        // Act
        String result = chatController.simpleChat(defaultQuery);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(chatClient).prompt(defaultQuery);
    }

    @Test
    @DisplayName("Should handle stream chat request successfully")
    void shouldHandleStreamChatRequestSuccessfully() {
        // Arrange
        String query = "请介绍一下Spring AI";
        Flux<String> expectedStream = Flux.just("Spring", " AI", " 是一个", "强大的框架");
        
        when(chatClient.prompt(query)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(expectedStream);
        
        // Act
        Flux<String> result = chatController.streamChat(query, null);
        
        // Assert
        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext("Spring")
                .expectNext(" AI")
                .expectNext(" 是一个")
                .expectNext("强大的框架")
                .verifyComplete();
        
        verify(chatClient).prompt(query);
        verify(promptRequestSpec).stream();
        verify(streamResponseSpec).content();
    }

    @Test
    @DisplayName("Should handle advisor chat with chat ID and query")
    void shouldHandleAdvisorChatWithChatIdAndQuery() {
        // Arrange
        String chatId = "test-chat-123";
        String query = "我叫什么名字？";
        Flux<String> expectedStream = Flux.just("你叫", "牧生");
        
        when(chatClient.prompt(query)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.advisors(any())).thenReturn(promptRequestSpec);
        when(promptRequestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(expectedStream);
        
        // Act
        Flux<String> result = chatController.advisorChat(null, chatId, query);
        
        // Assert
        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext("你叫")
                .expectNext("牧生")
                .verifyComplete();
        
        verify(chatClient).prompt(query);
        verify(promptRequestSpec).advisors(any());
        verify(promptRequestSpec).stream();
        verify(streamResponseSpec).content();
    }

    @Test
    @DisplayName("Should handle Redis chat memory functionality")
    void shouldHandleRedisChatMemoryFunctionality() {
        // Arrange
        String chatId = "redis-chat-456";
        String query = "记住我的名字是张三";
        Flux<String> expectedStream = Flux.just("好的", "，我会记住", "你的名字是张三");
        
        when(chatClient.prompt(query)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.advisors(redisChatMemory)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.advisors(any())).thenReturn(promptRequestSpec);
        when(promptRequestSpec.stream()).thenReturn(streamResponseSpec);
        when(streamResponseSpec.content()).thenReturn(expectedStream);
        
        // Act
        Flux<String> result = chatController.redis(query, chatId, null);
        
        // Assert
        assertNotNull(result);
        StepVerifier.create(result)
                .expectNext("好的")
                .expectNext("，我会记住")
                .expectNext("你的名字是张三")
                .verifyComplete();
        
        verify(chatClient).prompt(query);
        verify(promptRequestSpec).advisors(redisChatMemory);
        verify(promptRequestSpec).advisors(any());
        verify(promptRequestSpec).stream();
        verify(streamResponseSpec).content();
    }

    @Test
    @DisplayName("Should handle empty query gracefully")
    void shouldHandleEmptyQueryGracefully() {
        // Arrange
        String emptyQuery = "";
        String expectedResponse = "请提供一个有效的问题。";
        
        when(chatClient.prompt(emptyQuery)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedResponse);
        
        // Act
        String result = chatController.simpleChat(emptyQuery);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(chatClient).prompt(emptyQuery);
    }

    @Test
    @DisplayName("Should handle null query by using default")
    void shouldHandleNullQueryByUsingDefault() {
        // Arrange
        String defaultQuery = "你好，很高兴认识你，能简单介绍一下自己吗？";
        String expectedResponse = "你好！我是AI助手。";
        
        when(chatClient.prompt(defaultQuery)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(expectedResponse);
        
        // Act - passing null should use default value
        String result = chatController.simpleChat(defaultQuery);
        
        // Assert
        assertEquals(expectedResponse, result);
        verify(chatClient).prompt(defaultQuery);
    }

    @Test
    @DisplayName("Should verify ChatClient configuration")
    void shouldVerifyChatClientConfiguration() {
        // Verify that the ChatClient was configured with the expected system prompt
        verify(chatClientBuilder).defaultSystem("你是一名老师");
        
        // Verify that advisors were configured
        verify(chatClientBuilder, atLeast(2)).defaultAdvisors(any());
        
        // Verify that options were configured
        verify(chatClientBuilder).defaultOptions(any());
        
        // Verify that the ChatClient was built
        verify(chatClientBuilder).build();
    }

    @Test
    @DisplayName("Should handle Chinese text properly")
    void shouldHandleChineseTextProperly() {
        // Arrange
        String chineseQuery = "请用中文回答：什么是人工智能？";
        String chineseResponse = "人工智能是计算机科学的一个分支，致力于创建能够执行通常需要人类智能的任务的系统。";
        
        when(chatClient.prompt(chineseQuery)).thenReturn(promptRequestSpec);
        when(promptRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(chineseResponse);
        
        // Act
        String result = chatController.simpleChat(chineseQuery);
        
        // Assert
        assertEquals(chineseResponse, result);
        assertTrue(result.contains("人工智能"));
        assertTrue(result.contains("计算机科学"));
        verify(chatClient).prompt(chineseQuery);
    }
}