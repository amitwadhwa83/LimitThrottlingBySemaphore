package com.messagebird.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.messagebird.domain.Message;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.service.MessageService;

/**
 * All operations related to messaging will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(final MessageService messageService) {
	this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String sendMessage(@Valid @RequestBody Message input)
	    throws UnauthorizedException, GeneralException, NotFoundException, InterruptedException {
	return messageService.sendMessage(input);
    }

    @PostMapping("/multiparts")
    @ResponseStatus(HttpStatus.CREATED)
    public String sendMultiPartsMessage(@Valid @RequestBody Message input)
	    throws UnauthorizedException, GeneralException, NotFoundException, InterruptedException {
	return messageService.sendMultiPartsMessage(input);
    }
}