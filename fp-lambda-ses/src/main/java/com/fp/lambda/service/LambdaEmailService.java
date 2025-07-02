package com.fp.lambda.service;

import com.fp.lambda.message.EmailMessage;
import com.fp.lambda.message.Message;

public interface LambdaEmailService {

    void processMessage(Message message);
}
