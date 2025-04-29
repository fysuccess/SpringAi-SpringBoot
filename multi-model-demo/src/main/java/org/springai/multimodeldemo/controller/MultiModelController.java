/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springai.multimodeldemo.controller;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.dashscope.common.Role;
import jakarta.annotation.Resource;
import org.springai.multimodeldemo.data.ModelEnum;
import org.springai.multimodeldemo.helper.FrameExtraHelper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/multi")
public class MultiModelController {

	private final ChatClient dashScopeChatClient;

	@Resource
	private ResourceLoader resourceLoader;

	private static final String DEFAULT_PROMPT = "这些是什么？";

	private static final String DEFAULT_VIDEO_PROMPT = "这是一组从视频中提取的图片帧，请描述此视频中的内容。";

	private static final String DEFAULT_MODEL = "qwen-vl-max-latest";


	public MultiModelController(ChatModel chatModel) {

		this.dashScopeChatClient = ChatClient.builder(chatModel).build();
	}

	/**
	 * 视觉理解
	 * @param prompt
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/image")
	public String image(
			@RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_PROMPT)
			String prompt
	) throws Exception {

		List<Media> mediaList = List.of(
				new Media(
						MimeTypeUtils.IMAGE_PNG,
						new URI("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg").toURL()
				)
		);

		UserMessage message = new UserMessage(prompt, mediaList);
		message.getMetadata().put(DashScopeChatModel.MESSAGE_FORMAT, MessageFormat.IMAGE);

		ChatResponse response = dashScopeChatClient.prompt(
				new Prompt(
						message,
						DashScopeChatOptions.builder()
								.withModel(DEFAULT_MODEL)
								.withMultiModel(true)
								.build()
				)
		).call().chatResponse();

		return response.getResult().getOutput().getText();
	}

	/**
	 * 视觉推理
	 * @param prompt
	 * @return
	 */
	@GetMapping("/video")
	public String video(
			@RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_VIDEO_PROMPT)
			String prompt
	) {

		List<Media> mediaList = FrameExtraHelper.createMediaList(10);

		UserMessage message = new UserMessage(prompt, mediaList);
		message.getMetadata().put(DashScopeChatModel.MESSAGE_FORMAT, MessageFormat.VIDEO);

		ChatResponse response = dashScopeChatClient.prompt(
				new Prompt(
						message,
						DashScopeChatOptions.builder()
								.withModel("qwen-vl-max-latest")
								.withMultiModel(true)
								.build()
				)
		).call().chatResponse();

		return response.getResult().getOutput().getText();
	}
	/**
	 * 视觉理解
	 * @param prompt
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/image/bin")
	public String imagesBinary(
			@RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_PROMPT)
			String prompt
	) {

		UserMessage message = new UserMessage(
				prompt,
				new Media(
						MimeTypeUtils.IMAGE_JPEG,
						resourceLoader.getResource("classpath:/multimodel/dog_and_girl.jpeg")
				));
		message.getMetadata().put(DashScopeChatModel.MESSAGE_FORMAT, MessageFormat.IMAGE);

		ChatResponse response = dashScopeChatClient.prompt(
				new Prompt(
						message,
						DashScopeChatOptions.builder()
								.withModel(ModelEnum.VISUAL_COMPREHENSION.getModel())
								.withMultiModel(true)
								.build()
				)
		).call().chatResponse();

		return response.getResult().getOutput().getText();
	}
	/**
	 * 视觉理解
	 * @param prompt
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/stream/image")
	public String streamImage(
			@RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_PROMPT)
			String prompt
	) {

		UserMessage message = new UserMessage(
				prompt,
				new Media(
						MimeTypeUtils.IMAGE_JPEG,
						resourceLoader.getResource("classpath:/multimodel/dog_and_girl.jpeg")
				));
		message.getMetadata().put(DashScopeChatModel.MESSAGE_FORMAT, MessageFormat.IMAGE);

		List<ChatResponse> response = dashScopeChatClient.prompt(
				new Prompt(
						message,
						DashScopeChatOptions.builder()
								.withModel(ModelEnum.VISUAL_COMPREHENSION.getModel())
								.withMultiModel(true)
								.build()
				)
		).stream().chatResponse().collectList().block();

		StringBuilder result = new StringBuilder();
		if (response != null) {
			for (ChatResponse chatResponse : response) {
				String outputContent = chatResponse.getResult().getOutput().getText();
				result.append(outputContent);
			}
		}

		return result.toString();
	}

	// 文字提取

	/**
	 * 文字提取
	 * @param prompt
	 * @return
	 */
	@GetMapping("/text")
	public Object text(
			@RequestParam(value = "prompt", required = false, defaultValue = DEFAULT_PROMPT)
			String prompt
	) {

		MultiModalConversation conv = new MultiModalConversation();

		Map<String, Object> map = new HashMap<>();
		map.put("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241108/ctdzex/biaozhun.jpg");
		//map.put("image", "classpath:/multimodel/20250428215856.png");
		map.put("max_pixels", "1003520");
		map.put("min_pixels", "3136");
		MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
				.content(Arrays.asList(
						map,
						//为保证识别效果，目前模型内部会统一使用"Read all the text in the image."进行识别，用户输入的文本不会生效。
						Collections.singletonMap("text", "Read all the text in the image."))).build();
		MultiModalConversationParam param = MultiModalConversationParam.builder()
				// 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
				.apiKey(System.getenv("DASHSCOPE_API_KEY"))
				.model(ModelEnum.EXTRACT_TEXT.getModel())
				.message(userMessage)
				.build();
		try{
			MultiModalConversationResult result = conv.call(param);
			System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
			return result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text");

		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
