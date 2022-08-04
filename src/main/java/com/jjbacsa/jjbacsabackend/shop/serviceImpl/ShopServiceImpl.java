package com.jjbacsa.jjbacsabackend.shop.serviceImpl;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.service.ShopService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
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
    private final String BASE_URL="https://dapi.kakao.com";

    public ShopServiceImpl(WebClient.Builder webclientBuilder, @Value("${external.api.key}")String key) {

        DefaultUriBuilderFactory factory=new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        this.webClient = webclientBuilder.uriBuilderFactory(factory).baseUrl(BASE_URL)
                .defaultHeader("Authorization",key)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

    }

    @Override
    public List<ShopDto> search(String query, String x, String y) {

        ShopResponse res= webClient.get().uri(uriBuilder ->
                        uriBuilder.path("/v2/local/search/keyword.json")
                                .queryParam("query",query)
                                .queryParam("x",x)
                                .queryParam("y",y)
                                .queryParam("sort","accuracy")
                                .build()
                ).retrieve().bodyToMono(ShopResponse.class).block();

        return res.getDocuments();
    }
}
