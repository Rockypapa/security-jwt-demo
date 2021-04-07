package me.rocky.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.rocky.base.BaseController;
import me.rocky.constant.SecurityConstants;
import me.rocky.domain.LoginData;
import me.rocky.util.HttpWriterUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rocky
 * @version 1.0
 * @description 登录验证过滤器
 * @email inaho00@foxmail.com
 * @createDate 2020/9/22 下午4:26
 * @log
 */
@Log4j2
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
    }

    /**
     * 登录验证
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginData loginData = parseLoginData(request);
        //获取账号密码,通过userDetailsService认证,认证通过将路由到下方权限颁发

        log.info("账号信息为:{}",loginData.toString());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginData.getUsername(),loginData.getPassword());
       return this.authenticationManager.authenticate(token);
    }

    /**
     * 认证成功返回token
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //成功获取到用户信息
        User user = (User)authResult.getPrincipal();
        log.info("成功获取到用户信息:{},将为其颁发token",user.toString());
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String role = roles.get(0);
        String platform = BaseController.getRequest().getHeader("platform-id");
        log.info("role:{},platform:{}",role,platform);
        SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes());
        //基于权限颁发token
        String token = Jwts.builder()
            .setHeaderParam("TYP",SecurityConstants.TOKEN_TYPE)
            .setIssuer(SecurityConstants.TOKEN_ISSUER)
            .setAudience(SecurityConstants.TOKEN_AUDIENCE)
            .setExpiration(new Date(System.currentTimeMillis()+SecurityConstants.TOKEN_EXPIRE))
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .claim("roles",roles)
            .signWith(secretKey).compact();
        log.info("该user的token为:{}",token);
        HttpWriterUtil.outWriter(response,StandardCharsets.UTF_8.name(), MediaType.APPLICATION_JSON_VALUE, HttpStatus.OK.value(), "获取token成功",SecurityConstants.TOKEN_PREFIX+token);
    }

    private LoginData parseLoginData(HttpServletRequest request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(request.getInputStream(),LoginData.class);
    }
}
