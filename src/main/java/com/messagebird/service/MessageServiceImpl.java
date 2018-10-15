package com.messagebird.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.domain.Message;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.MessageResponse;
import com.messagebird.util.RateLimiter;

/**
 * Service to encapsulate the logic for sending message
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final RateLimiter limiter;
    private static final String ACCESS_KEY = "cxeYcOO7GQi9eVwsNcuzraqY7";
    private final MessageBirdClient client;
    private final int THROUGHPUT_MAX_PERMITS = 1;
    private final int THROUGHPUT_DELAY_IN_SECOND = 1;
    private final int MAX_CHAR = 160;

    public MessageServiceImpl() {
	this.client = new MessageBirdClient(new MessageBirdServiceImpl(ACCESS_KEY));
	limiter = RateLimiter.create(THROUGHPUT_MAX_PERMITS, THROUGHPUT_DELAY_IN_SECOND, TimeUnit.SECONDS);
    }

    @PreDestroy
    private void shutdown() {
	limiter.stop();
    }

    /**
     * Sends a message to client API
     */
    @Override
    public String sendMessage(Message inputMsg) throws InterruptedException, UnauthorizedException, GeneralException {
	limiter.acquire();
	MessageResponse response = client.sendMessage(inputMsg.getOriginator(), inputMsg.getMessage(),
		Arrays.asList(inputMsg.getRecipient()));
	LOG.info("Response:" + response.toString());
	return "Message successfully sent";
    }

    /**
     * Sends a concatenated message to client API for message having content/body
     * longer than specified size
     */
    @Override
    public String sendMultiPartsMessage(Message inputMsg)
	    throws InterruptedException, UnauthorizedException, GeneralException {

	for (String concatMsg : splitToChar(inputMsg.getMessage(), MAX_CHAR)) {
	    limiter.acquire();

	    // Using hashcode as UDH
	    com.messagebird.objects.Message message = com.messagebird.objects.Message.createBinarySMS(
		    inputMsg.getOriginator(), String.valueOf(inputMsg.hashCode()), concatMsg,
		    inputMsg.getRecipient().toString());
	    LOG.info("Response:" + client.sendMessage(message).toString());
	}
	return "Message successfully sent";
    }

    private static String[] splitToChar(String input, int size) {
	List<String> parts = new ArrayList<>();

	int length = input.length();
	for (int i = 0; i < length; i += size) {
	    parts.add(input.substring(i, Math.min(length, i + size)));
	}
	return parts.toArray(new String[0]);
    }
}