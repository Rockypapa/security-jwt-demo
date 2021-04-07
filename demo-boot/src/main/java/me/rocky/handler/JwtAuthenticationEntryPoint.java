package me.rocky.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @createDate 2020/9/23 0:14
 * @log
 */
@Log4j2
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	/**
	 * 当用户尝试访问需要权限才能的REST资源而不提供Token或者Token过期时，
	 * 将调用此方法发送401响应以及错误信息
	 * 处理登录出现的异常，虽然没找到用户是UsernameNotFoundException 异常，但是经过系统回调，异常变成了 InsufficientAuthenticationException
	 */
	@Override
	public void commence(HttpServletRequest request,
	                     HttpServletResponse response,
	                     AuthenticationException authException) throws IOException {

		if (authException instanceof InsufficientAuthenticationException){
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Map<String, Object> params = new HashMap<>(4);
			params.put("code", 401);
			params.put("message", "用户名或密码错误");
			params.put("data", null);
			response.getWriter().print(new ObjectMapper().writeValueAsString(params));
		}else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
		}
	}
}
