package me.rocky.filter;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import me.rocky.constant.SecurityConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rocky
 * @version 1.0
 * @description 权限认证拦截器
 * @email inaho00@foxmail.com
 * @createDate 2020/9/22 下午5:30
 * @log
 */
@Log4j2
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null){

            chain.doFilter(request,response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request){
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
//        log.info("权限过滤器获取到的token为:{}",token);
        if (StringUtils.isNoneEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)){
           try {
               Jws<Claims> parseToken = Jwts.parserBuilder()
                       .setSigningKey(SecurityConstants.JWT_SECRET.getBytes())
                       .build()
                       .parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""));
               String username = parseToken.getBody().getSubject();
               List<SimpleGrantedAuthority> authorities = ((List<?>) parseToken.getBody().get("roles")).stream()
                       .map(authority -> new SimpleGrantedAuthority((String) authority))
                       .collect(Collectors.toList());
               if(StringUtils.isNotEmpty(username)){
                   return new UsernamePasswordAuthenticationToken(username,null,authorities);
               }
           }catch (ExpiredJwtException e){
               log.warn("token 解密失败");
               log.warn("request to parse expired JWT:{} failed:{}",token,e);
           }catch (UnsupportedJwtException e){
               log.warn("token 解密失败");
               log.warn("request to parse unsupported JWT:{} failed:{}",token,e);
           }catch (MalformedJwtException e){
               log.warn("token 解密失败");
               log.warn("request to parse invalid JWT:{} failed:{}",token,e);
           }catch (SignatureException e){
               log.warn("token 解密失败");
               log.warn("request to parse  JWT with invalid signature :{} failed:{}",token,e);
           }catch (IllegalArgumentException e){
               log.warn("token 解密失败");
               log.warn("request to parse empty or null JWT:{} failed:{}",token,e);
           }
        }
        return null;
    }
}
