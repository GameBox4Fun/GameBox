package top.naccl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.naccl.exception.PersistenceException;
import top.naccl.mapper.UserMapper;
import top.naccl.model.entity.User;
import top.naccl.service.UserService;

/**
 * @Description: 用户业务层实现
 * @Author: Naccl
 * @Date: 2020-10-16
 */
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserMapper userMapper;

	@Override
	public User checkUserByUsername(String username) {
		return userMapper.findByUsername(username);
	}

	@Transactional
	@Override
	public void saveUser(User user) {
		if (userMapper.saveUser(user) != 1) {
			throw new PersistenceException("注册失败");
		}
	}

	@Transactional
	@Override
	public void updateUserById(User user, Long id) {
		if (userMapper.updateUserById(user, id) != 1) {
			throw new PersistenceException("更新失败");
		}
	}
}
