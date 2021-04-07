package me.rocky.handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
 * @createDate 2020/9/23 0:01
 * @log
 */
@Log4j2
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	/**
	 * 当用户尝试访问需要权限才能的REST资源而权限不足的时候，
	 * 将调用此方法发送401响应以及错误信息
	 * 2021年4月6日14:56:36  自定义了返回数据模板
	 *
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
//		accessDeniedException = new AccessDeniedException("Sorry you don not enough permissions to access it!");
//		response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		Map<String, Object> params = new HashMap<>(4);
		params.put("code", 403);
		params.put("message", "您没有相关的访问权限");
		params.put("data", null);
		response.getWriter().print(new ObjectMapper().writeValueAsString(params));
	}

}