package me.rocky.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @email inaho00@foxmail.com
 * @createDate 2021/4/7 17:16
 * @log
 */
@Data
public class UserRoleAuthority implements Serializable {
	private static final long serialVersionUID = 6672524184032784171L;

	private String username;

	private String password;

	private  boolean enabled;

	private List<String> roleList;

	private List<String> authorityList;

	public UserRoleAuthority(){
	}


	public UserRoleAuthority(String username,String password,List<String> roleList,List<String> authorityList){
		this.enabled = true;
		this.username = username;
		this.password = password;
		this.roleList = roleList;
		this.authorityList = authorityList;
	}

}
