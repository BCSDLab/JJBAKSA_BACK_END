package com.jjbacsa.jjbacsabackend.util;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SesSender {
    private final AmazonSimpleEmailServiceAsync simpleEmailServiceAsync;

    public void sendMail(String to, String subject, String htmlBody) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(to) // 받는 사람
                )
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBody)) // HTML 양식의 본문
                        )
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(subject)) // 제목
                )
                .withSource("no-reply@jjbaksa.com");  // Verify된 Email

        simpleEmailServiceAsync.sendEmailAsync(request);
    }
}
