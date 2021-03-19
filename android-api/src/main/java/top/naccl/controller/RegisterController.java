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
 * @Description: 注册
 * @Author: Naccl
 * @Date: 2020-10-06
 */
@RestController
public class RegisterController {
	@Autowired
	UserService userService;

	/**
	 * 用户注册
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 */
	@PostMapping("/register")
	public Result register(@RequestParam String username, @RequestParam String password) {
		User user = userService.checkUserByUsername(username);
		if (user != null) {
			return Result.create(400, "用户名已存在");
		}
		User u = new User();
		u.setUsername(username);
		u.setPassword(MD5Utils.getMD5(password));
		userService.saveUser(u);

		u.setPassword(null);
		String subject = u.getId() + ":" + u.getUsername();
		String jwt = JwtUtils.generateToken(subject);
		Map<String, Object> map = new HashMap<>();
		map.put("user", u);
		map.put("token", jwt);
		return Result.ok("注册成功", map);
	}
}
