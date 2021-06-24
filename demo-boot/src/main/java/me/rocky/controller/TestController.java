package me.rocky.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @email inaho00@foxmail.com
 * @createDate 2021/6/24 下午2:35
 * @log
 */
@RestController
@RequestMapping("/test")
public class TestController {

	@GetMapping("/a1")
	public String a1(){
		return UUID.randomUUID().toString();
	}
}
