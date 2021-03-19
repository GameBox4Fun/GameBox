package top.naccl.gamebox.service;

import top.naccl.gamebox.bean.User;

public interface UserService {
	User getUser();

	boolean saveUser(User user);

	boolean updateUser(User user);

	boolean deleteUser();
}
