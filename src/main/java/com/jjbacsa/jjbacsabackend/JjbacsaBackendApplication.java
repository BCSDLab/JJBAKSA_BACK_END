package com.jjbacsa.jjbacsabackend;

import com.jjbacsa.jjbacsabackend.util.AuthLinkUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AuthLinkUtil.class})
public class JjbacsaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JjbacsaBackendApplication.class, args);
    }

}
