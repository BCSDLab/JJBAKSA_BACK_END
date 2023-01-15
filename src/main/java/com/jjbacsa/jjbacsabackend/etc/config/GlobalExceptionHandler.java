package com.jjbacsa.jjbacsabackend.etc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.dto.slack.SlackAttachment;
import com.jjbacsa.jjbacsabackend.etc.dto.slack.SlackParameter;
import com.jjbacsa.jjbacsabackend.etc.dto.slack.SlackTarget;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.BaseException;
import com.jjbacsa.jjbacsabackend.etc.exception.CriticalException;
import com.jjbacsa.jjbacsabackend.util.ParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final SlackNotiSender slackNotiSender;

    @Value("${slack.url}")
    private String notifyErrorUrl;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<BaseException> defaultException(Throwable e, HandlerMethod handlerMethod) throws IOException {


        BaseException baseException;
        boolean slack = false;

        //시스템 정의 예외인 경우
        if (e instanceof BaseException) {

            ((BaseException) e).setErrorTrace(e.getStackTrace()[0].toString());
            baseException = (BaseException) e;

            if (e instanceof CriticalException)
                slack = true;
        }
        //Validation 예외인 경우
        else if (e instanceof BindException) {
            baseException = convertBindToBase((BindException) e);
        } else if (e instanceof ValidationException) {
            baseException = new BaseException(e.getClass().getSimpleName(), ErrorMessage.VALIDATION_FAIL_EXCEPTION);
            baseException.setErrorMessage(e.getMessage());
            baseException.setErrorTrace(e.getStackTrace()[0].toString());
        }
        //Method Security 예외인 경우
        else if (e instanceof AccessDeniedException) {
            baseException = convertDeniedToBase((AccessDeniedException) e);
        }
        //정의되지 않은 예외인 경우
        else {

            baseException = new BaseException(e.getClass().getSimpleName(), ErrorMessage.UNDEFINED_EXCEPTION);
            baseException.setErrorMessage(e.getMessage());
            baseException.setErrorTrace(e.getStackTrace()[0].toString());

            if (!(e instanceof ClientAbortException))
                slack = true;
        }


        if (slack) {
            sendSlackNoti(e, handlerMethod);
        }

        log.error(baseException.getErrorMessage(), e);

        return new ResponseEntity<>(baseException, baseException.getHttpStatus());
    }

    private BaseException convertBindToBase(BindException e) {

        BaseException baseException = new BaseException(e.getClass().getSimpleName(), ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        List<ObjectError> messageList = e.getAllErrors();
        StringBuilder message = new StringBuilder();
        for (ObjectError objectError : messageList) {

            String validationMessage = objectError.getDefaultMessage();
            message.append("[").append(validationMessage).append("]");
        }
        baseException.setErrorMessage(message.toString());
        baseException.setErrorTrace(e.getStackTrace()[0].toString());

        return baseException;
    }

    private BaseException convertDeniedToBase(AccessDeniedException e) {
        BaseException baseException = new BaseException(e.getClass().getSimpleName(), ErrorMessage.INVALID_AUTHORITY);
        baseException.setErrorTrace(e.getStackTrace()[0].toString());
        return baseException;
    }

    private <T extends Throwable> void sendSlackNoti(T e, HandlerMethod handlerMethod) throws IOException {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String host = request.getHeader("host");
        String uri = request.getRequestURI();

        SlackTarget slackTarget = new SlackTarget(notifyErrorUrl, "");
        SlackParameter slackParameter = new SlackParameter();
        slackParameter.setText(String.format("`%s` 서버에서 에러가 발생했습니다.", host));
        SlackAttachment slackAttachment = new SlackAttachment();
        String errorName = e.getClass().getSimpleName();
        String errorFile = e.getStackTrace()[0].getFileName();
        String errorMessage = e.getMessage();
        int errorLine = e.getStackTrace()[0].getLineNumber();

        String requestParam = new ObjectMapper().writeValueAsString(ParserUtil.splitQueryString(request.getQueryString()));
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        String message = String.format("```%s %s Line %d```\n```===== [Message] ===== \n%s\n\n===== [Controller] =====\n%s\n\n===== [RequestParameter] =====\n%s\n\n===== [RequestBody] =====\n%s```",
                errorName, errorFile, errorLine, errorMessage, handlerMethod, requestParam, requestBody);

        slackAttachment.setTitle(String.format("URI : %s", uri));
        slackAttachment.setText(message);
        slackParameter.getSlackAttachments().add(slackAttachment);
        slackNotiSender.send(slackTarget, slackParameter);
    }
}
