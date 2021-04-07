package me.rocky.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @email inaho00@foxmail.com
 * @createDate 2021/4/7 16:56
 * @log
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"me.rocky.mapper"})
public class MyBatisConfig {
}
