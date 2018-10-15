package com.messagebird.service;

import javax.validation.Valid;

import com.messagebird.domain.Message;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;

public interface MessageService {
    String sendMessage(@Valid Message input) throws InterruptedException, UnauthorizedException, GeneralException;

    String sendMultiPartsMessage(@Valid Message input) throws InterruptedException, UnauthorizedException, GeneralException;
}