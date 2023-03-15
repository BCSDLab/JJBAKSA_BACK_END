package com.jjbacsa.jjbacsabackend.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@ConstructorBinding
@ConfigurationProperties("auth-url")
public class AuthLinkUtil {
    private final String rootUrl;
    private final String link;
    private final Map<String, String> query;

    public AuthLinkUtil(String rootUrl, String link, Map<String, String> query) {
        this.rootUrl = rootUrl;
        this.link = link;
        this.query = query;
    }

    public URI getAuthLink(String accessToken, String refreshToken) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(rootUrl)
                .queryParam("link", addToken(link, accessToken, refreshToken));

        for (String key : query.keySet()) {
            uriComponentsBuilder.queryParam(key, query.get(key));
        }

        return uriComponentsBuilder.build().toUri();
    }

    private String addToken(String str, String accessToken, String refreshToken) {
        StringBuilder sb = new StringBuilder(str);

        sb.append("?accessToken=").append(accessToken);
        sb.append("&refreshToken=").append(refreshToken);

        return sb.toString();
    }
}
