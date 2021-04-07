package me.rocky.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @email inaho00@foxmail.com
 * @createDate 2020/9/22 下午4:29
 * @log
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginData implements Serializable {
    private static final long serialVersionUID = 5120310532229453494L;
    private String username;
    private String password;
}
