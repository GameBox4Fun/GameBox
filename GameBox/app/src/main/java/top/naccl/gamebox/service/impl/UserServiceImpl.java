package top.naccl.gamebox.service.impl;

import android.content.Context;

import top.naccl.gamebox.bean.User;
import top.naccl.gamebox.dao.UserDao;
import top.naccl.gamebox.service.UserService;

public class UserServiceImpl implements UserService {
	private static final String[] allColumns = new String[]{"_id", "username", "password", "avatar", "introduction", "sex", "email", "education", "job", "birthday"};
	UserDao userDao;

	public UserServiceImpl(Context context) {
		this.userDao = new UserDao(context);
	}

	private String[] getAllParams(User user) {
		return new String[]{String.valueOf(user.getId()), user.getUsername(), user.getPassword(), user.getAvatar(), user.getIntroduction(), user.getSex(), user.getEmail(), user.getEducation(), user.getJob(), user.getBirthday()};
	}

	@Override
	public User getUser() {
		userDao.open();
		User[] users = (User[]) userDao.queryAllData(allColumns);
		userDao.close();
		if (users != null && users.length == 1) {
			return users[0];
		}
		return null;
	}

	@Override
	public boolean saveUser(User user) {
		userDao.open();
		long res = userDao.insert(allColumns, getAllParams(user));
		userDao.close();
		if (res == -1) {
			return false;
		}
		return true;
	}

	@Override
	public boolean updateUser(User user) {
		userDao.open();
		//正常情况下，数据库中只存放一条用户记录，直接updateAllData
		long res = userDao.updateAllData(allColumns, getAllParams(user));
		userDao.close();
		if (res == -1) {
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteUser() {
		userDao.open();
		long res = userDao.deleteAllData();
		userDao.close();
		if (res == -1) {
			return false;
		}
		return true;
	}
}
