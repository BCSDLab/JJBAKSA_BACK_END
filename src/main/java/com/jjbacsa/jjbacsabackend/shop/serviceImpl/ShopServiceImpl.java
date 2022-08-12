package com.jjbacsa.jjbacsabackend.shop.serviceImpl;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ShopServiceImpl implements ShopService {

    private final WebClient webClient;

    public ShopServiceImpl(WebClient.Builder webclientBuilder, @Value("${external.api.key}")String key) {

        DefaultUriBuilderFactory factory=new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        this.webClient = webclientBuilder.uriBuilderFactory(factory)
                .defaultHeader("Authorization",key)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

    }

    @Override
    public ShopDto getShop(String place_id) {
        //1. DB에 place_id의 상점이 있는지 검색
        //2. place_id를 기반으로 google에 상점 검색 -> ShopDto로 파싱

        //1에서 상점이 있는 경우 ->
        //1에서 상점이 없는 경우 ->
        return null;
    }

    @Override
    public ShopDto.Shop searchShop(String place_id) {

        return null;
    }
}
