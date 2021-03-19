package top.naccl.service;

import top.naccl.model.entity.User;

public interface UserService {
	User checkUserByUsername(String username);

	void saveUser(User user);

	void updateUserById(User user, Long id);
}
