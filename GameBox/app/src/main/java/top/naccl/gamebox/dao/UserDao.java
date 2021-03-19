package top.naccl.gamebox.dao;

import android.content.Context;
import android.database.Cursor;

import top.naccl.gamebox.bean.User;
import top.naccl.gamebox.util.DBUtils;


public class UserDao extends DBUtils {
	private static final String TABLE = "user";
	private static final String TABLE_CREATE = "create table user(_id integer primary key autoincrement, username text not null, password text, avatar text, introduction text, sex text, email text, education text, job text, birthday text);";

	public UserDao(Context context) {
		super(context, TABLE, TABLE_CREATE);
	}

	@Override
	public User[] ConvertToObject(Cursor cursor) {
		int resultCounts = cursor.getCount();
		if (resultCounts == 0 || !cursor.moveToFirst()) {
			return null;
		}
		User[] users = new User[resultCounts];
		for (int i = 0; i < resultCounts; i++) {
			users[i] = new User();
			users[i].setId(cursor.getLong(0));
			users[i].setUsername(cursor.getString(cursor.getColumnIndex("username")));
			users[i].setPassword(cursor.getString(cursor.getColumnIndex("password")));
			users[i].setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
			users[i].setIntroduction(cursor.getString(cursor.getColumnIndex("introduction")));
			users[i].setSex(cursor.getString(cursor.getColumnIndex("sex")));
			users[i].setEmail(cursor.getString(cursor.getColumnIndex("email")));
			users[i].setEducation(cursor.getString(cursor.getColumnIndex("education")));
			users[i].setJob(cursor.getString(cursor.getColumnIndex("job")));
			users[i].setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
			cursor.moveToNext();
		}
		return users;
	}
}
