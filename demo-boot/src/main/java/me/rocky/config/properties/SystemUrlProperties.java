package me.rocky.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @email inaho00@foxmail.com
 * @createDate 2020/12/4 9:00
 * @log
 */
@Data
@ConfigurationProperties(prefix = "system.url")
public class SystemUrlProperties {
    private String[] shouldSkipList;
}
