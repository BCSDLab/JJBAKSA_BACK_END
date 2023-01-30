package com.jjbacsa.jjbacsabackend.etc.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.dto.slack.SlackParameter;
import com.jjbacsa.jjbacsabackend.etc.dto.slack.SlackTarget;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Configuration
public class SlackNotiSender {

    WebClient webClient;

    SlackNotiSender() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(httpClient);

        this.webClient = WebClient.builder()
                .clientConnector(clientHttpConnector)
                .build();
    }

    public void send(SlackTarget slackTarget, SlackParameter slackParameter) {
        if (checkUrl(slackTarget.getWebHookUrl())) {
            try {
                //System.out.println(new ObjectMapper().writeValueAsString(slackParameter));
                webClient.post().uri(slackTarget.getWebHookUrl())
                        .bodyValue(new ObjectMapper().writeValueAsString(slackParameter))
                        .retrieve().toBodilessEntity().block();
            } catch (RestClientException | JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkUrl(String url) {
        if (url == null || url.equals("")) {
            return false;
        }
        return true;
    }
}
