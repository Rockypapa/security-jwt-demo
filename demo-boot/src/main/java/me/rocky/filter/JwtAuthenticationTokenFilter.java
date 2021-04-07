package me.rocky.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import me.rocky.config.properties.SystemUrlProperties;
import me.rocky.constant.SecurityConstants;
import me.rocky.util.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rocky
 * @version 1.0
 * @description token校验过滤器
 * @createDate 2020/9/23 0:54
 * @log
 */
@Log4j2
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
	private SystemUrlProperties systemUrlProperties;

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * 调用接口进行拦截，用于判断token是否过期
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {
		// 获取 认证头
		String authorizationHeader = request.getHeader(SecurityConstants.TOKEN_HEADER);
		//如果没有授权信息
		if (!checkIsTokenAuthorizationHeader(authorizationHeader)) {
			log.debug("获取到认证头Authorization的值:[{}]但不是我们系统中登录后签发的。", authorizationHeader);
			//进入 /sso/token需要经过该拦截器
			filterChain.doFilter(request, response);
			return;

		}
		// 获取到真实的token
		String realToken = getRealAuthorizationToken(authorizationHeader);
		// 解析 jwt token
		Jws<Claims> jws = JwtTokenUtil.parserAuthenticateToken(realToken, SecurityConstants.JWT_SECRET);
		// token 不合法
		if (null == jws) {
			log.warn("认证token不合法:{}",realToken);
			writeJson(response, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value(),"认证token不合法");
			return;
		}
		// token 是否过期
		if (JwtTokenUtil.isJwtExpired(jws)) {
			// 处理过期
			log.warn("token过期了请重新登录:{}",realToken);
			writeJson(response, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value(),"token过期了请重新登录");
			return;
		}
		filterChain.doFilter(request, response);
		}

		boolean pathMatched(String path){
			for (String s: systemUrlProperties.getShouldSkipList()){
				if (pathMatcher.match(s,path)){
					return true;
				}
			}
			return false;
		}

	/**
	 * 写 json 数据给前端
	 *
	 * @param response
	 * @throws IOException
	 */
	private void writeJson(HttpServletResponse response, String msg) throws IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		Map<String, Object> params = new HashMap<>(4);
		params.put("code", HttpStatus.UNAUTHORIZED.value());
		params.put("message", msg);
		params.put("data", null);
		response.getWriter().print(OBJECT_MAPPER.writeValueAsString(params));
	}

	/**
	 * 写 json 数据给前端
	 *
	 * @param response
	 * @throws IOException
	 */
	private void writeJson(HttpServletResponse response,int code, String msg) throws IOException {
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		Map<String, Object> params = new HashMap<>(4);
		params.put("code",code);
		params.put("message", msg);
		params.put("data", null);
		response.getWriter().print(OBJECT_MAPPER.writeValueAsString(params));
	}

	/**
	 * 获取到真实的 token 串
	 * @param authorizationToken
	 * @return
	 */
	private String getRealAuthorizationToken(String authorizationToken) {
		return StringUtils.substring(authorizationToken, SecurityConstants.TOKEN_PREFIX.length()).trim();
	}

	/**
	 * 判断是否是系统中登录后签发的token
	 * @param authorizationHeader
	 * @return
	 */
	private boolean checkIsTokenAuthorizationHeader(String authorizationHeader) {
		if (StringUtils.isEmpty(authorizationHeader)) {
			return false;
		}
		if (StringUtils.isBlank(authorizationHeader)) {
			return false;
		}
		return StringUtils.startsWith(authorizationHeader, SecurityConstants.TOKEN_PREFIX);
	}
}