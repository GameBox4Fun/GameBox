package top.naccl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.naccl.model.entity.User;
import top.naccl.model.vo.Result;
import top.naccl.service.UserService;
import top.naccl.util.JwtUtils;
import top.naccl.util.MD5Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 用户登录
 * @Author: Naccl
 * @Date: 2020-11-05
 */
@RestController
public class LoginController {
	@Autowired
	UserService userService;

	/**
	 * 用户登录
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 */
	@PostMapping("/login")
	public Result login(@RequestParam String username, @RequestParam String password) {
		User user = userService.checkUserByUsername(username);
		if (user == null || !user.getPassword().equals(MD5Utils.getMD5(password))) {
			return Result.create(401, "用户名或密码错误！");
		}
		user.setPassword(null);
		String subject = user.getId() + ":" + user.getUsername();
		String jwt = JwtUtils.generateToken(subject);
		Map<String, Object> map = new HashMap<>();
		map.put("user", user);
		map.put("token", jwt);
		return Result.ok("登录成功", map);
	}
}
