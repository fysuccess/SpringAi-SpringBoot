package org.springai.flightbooking.client;

import org.springai.flightbooking.services.CustomerSupportAssistant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;


@RequestMapping("/api/assistant")
@RestController
@CrossOrigin
public class AssistantController {

	private final CustomerSupportAssistant agent;

	public AssistantController(CustomerSupportAssistant agent) {
		this.agent = agent;
	}

	@RequestMapping(path="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> chat(String chatId, String message) throws InterruptedException {
		Flux<String> messages = agent.chat(chatId, message).concatWith(Flux.just("[complete]"));

		System.out.println("答复:" + formatStr(chatId, messages));

		return messages;
	}

	public String formatStr (String chatId, Flux<String> messages) throws InterruptedException {
		List<String> str = new ArrayList<>();
		CountDownLatch latch = new CountDownLatch(1);
		messages.collectList()
				.subscribe(
						list -> {
							System.out.println("Collected list: " + list);
							str.addAll(list);
							latch.countDown();
						},
						error -> {
							System.err.println("Error: " + error);
							latch.countDown();
						}
				);
		latch.await();
        return String.join("", str);
	}

}
