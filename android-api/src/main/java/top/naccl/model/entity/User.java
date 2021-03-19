package top.naccl.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 用户实体类
 * @Author: Naccl
 * @Date: 2020-10-06
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {
	private Long id;
	private String username;
	private String password;
	private String avatar;
	private String introduction;
	private String sex;
	private String email;
	private String education;
	private String job;
	private String birthday;
}
