package me.rocky.service.impl;

import lombok.SneakyThrows;
import me.rocky.domain.UserRoleAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @email inaho00@foxmail.com
 * @createDate 2021/4/7 17:11
 * @log
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Pattern CHINA_PATTERN = Pattern.compile("^((13[0-9])|(14[0,1,4-9])|(15[0-3,5-9])|(16[2,5,6,7])|(17[0-8])|(18[0-9])|(19[0-3,5-9]))\\d{8}$");

	@SneakyThrows
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Matcher matcher = CHINA_PATTERN.matcher(username);
		if (matcher.matches()){
			//如果匹配手机号，就通过手机号查询数据库
		}else {
			//如果不匹配手机号，就通过账号查询数据库
		}
		// 伪造用户信息
		UserRoleAuthority userRoleAuthority = new UserRoleAuthority("admin123",
				new BCryptPasswordEncoder().encode("123456"),
				Arrays.asList("ROLE_user","ROLE_admin","ROLE_test"),
				Arrays.asList("read","write"));
		List<SimpleGrantedAuthority> collect = new ArrayList<>();
		collect.addAll(userRoleAuthority.getAuthorityList().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		collect.addAll(userRoleAuthority.getRoleList().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		return new User(userRoleAuthority.getUsername(), userRoleAuthority.getPassword(), collect);
	}
}