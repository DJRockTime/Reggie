package com.app;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@SpringBootApplication
//@MapperScan("com.app.mapper") // 扫描mapper 包
@EnableTransactionManagement // 开启事务
@EnableCaching
public class ReggieApp {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApp.class, args);

    }

}
