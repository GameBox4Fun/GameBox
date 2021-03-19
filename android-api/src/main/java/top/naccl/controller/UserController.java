package top.naccl.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.naccl.model.entity.User;
import top.naccl.model.vo.Result;
import top.naccl.service.UserService;
import top.naccl.util.JwtUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 用户资料
 * @Author: Naccl
 * @Date: 2020-10-06
 */
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	ObjectMapper objectMapper;

	/**
	 * 更新用户个人资料
	 *
	 * @param request
	 * @return
	 */
	@PostMapping("/update")
	public Result updateUser(HttpServletRequest request) {
		User user = JSON.parseObject(request.getParameter("user"), User.class);
		String jwt = request.getHeader("Authorization");
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				String userId = JwtUtils.getUserId(jwt);
				userService.updateUserById(user, Long.valueOf(userId));
			} catch (Exception e) {
				e.printStackTrace();
				return Result.create(403, "Token已失效，请重新登录！");
			}
		} else {
			return Result.create(403, "请登录");
		}
		return Result.ok("更新成功");
	}
}
