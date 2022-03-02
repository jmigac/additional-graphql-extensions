package com.juricamigac.additionalgraphqlextensions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass=true)
@SpringBootApplication
public class AdditionalGraphqlExtensionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdditionalGraphqlExtensionsApplication.class, args);
    }

}
