package me.rocky.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJws;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import me.rocky.base.BaseController;
import me.rocky.constant.SecurityConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @email inaho00@foxmail.com
 * @createDate 2020/9/23 上午9:07
 * @log
 */
@Log4j2
public class JwtTokenUtil {

    public static String getUsernameByRequest(){
        String token = BaseController.getRequest().getHeader(SecurityConstants.TOKEN_HEADER);
        if (StringUtils.isNotEmpty(token)){
            try{
                // 根据token 获取用户名
                Jws<Claims> parseToken = Jwts.parserBuilder()
                        .setSigningKey(SecurityConstants.JWT_SECRET.getBytes())
                        .build()
                        .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""));
                return parseToken.getBody().getSubject();
            }catch (ExpiredJwtException e){
                log.warn("request to parse expired JWT:{} failed:{}",token,e);
            }catch (UnsupportedJwtException e){
                log.warn("request to parse unsupported JWT:{} failed:{}",token,e);
            }catch (MalformedJwtException e){
                log.warn("request to parse invalid JWT:{} failed:{}",token,e);
            }catch (SignatureException e){
                log.warn("request to parse  JWT with invalid signature :{} failed:{}",token,e);
            }catch (IllegalArgumentException e){
                log.warn("request to parse empty or null JWT:{} failed:{}",token,e);
            }
        }
        return null;
    }


    /**
     * 解析 jwt token
     * @param token     需要解析的json
     * @param secretKey 密钥
     * @return
     */
    public static Jws<Claims> parserAuthenticateToken(String token, String secretKey) {
        try {
            final Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(SecurityConstants.JWT_SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""));
            return claimsJws;
        } catch (ExpiredJwtException e) {
            return new DefaultJws<>(null, e.getClaims(), "");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException | IncorrectClaimException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 判断 jwt 是否过期
     *
     * @param jws
     * @return true:过期 false:没过期
     */
    public static boolean isJwtExpired(Jws<Claims> jws) {
        return jws.getBody().getExpiration().before(new Date());
    }

    /**
     * 构建认证过的认证对象
     */
    public static Authentication buildAuthentication(Jws<Claims> jws, String username) {
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(username, null, new ArrayList<>(0));
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
