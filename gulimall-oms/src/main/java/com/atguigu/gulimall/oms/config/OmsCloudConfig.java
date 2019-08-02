package com.atguigu.gulimall.oms.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient //服务注册发现
//@EnableFeignClients(basePackages = "") //远程调用 feign
public class OmsCloudConfig {
}
