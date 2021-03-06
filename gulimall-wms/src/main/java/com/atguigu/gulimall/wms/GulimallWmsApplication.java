package com.atguigu.gulimall.wms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
@RefreshScope
@MapperScan(basePackages = "com.atguigu.gulimall.wms.dao")
public class GulimallWmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWmsApplication.class, args);
    }

}
