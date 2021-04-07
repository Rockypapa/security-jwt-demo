package me.rocky.constant;

/**
 * @author Rocky
 * @version 1.0
 * @description  jwt相关配置
 * @email inaho00@foxmail.com
 * @createDate 2020/9/22 下午4:47
 * @log
 */

public interface SecurityConstants {
     String JWT_SECRET = "citrusProductionManagementSecretCreatedByRocky=";
     String AUTH_LOGIN_URL = "/sso/token";
     String TOKEN_HEADER = "Authorization";
     String TOKEN_PREFIX = "Bearer ";
     String TOKEN_TYPE = "JWT";
     String TOKEN_ISSUER = "security-api";
     String TOKEN_AUDIENCE = "security-app";
     String REFRESH_TOKEN = "Refresh-Token";
     Long TOKEN_EXPIRE = 30*24*60*60*1000L;

}
