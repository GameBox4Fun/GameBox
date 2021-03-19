package top.naccl.gamebox.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBUtils {
	private static final String DB_NAME = "gamebox.db";
	private static final int DB_VERSION = 1;

	private SQLiteDatabase db;
	private DBOpenHelper dbOpenHelper;
	private Context context;
	private String table;
	private String table_create;

	public DBUtils(Context context, String table, String table_create) {
		this.context = context;
		this.table = table;
		this.table_create = table_create;
	}

	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

	public void open() throws SQLiteException {
		dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION, table, table_create);
		try {
			db = dbOpenHelper.getWritableDatabase();//执行此方法时，会判断数据库是否存在，如果已存在，就不在调用onCreate方法
		} catch (SQLiteException ex) {
			db = dbOpenHelper.getReadableDatabase();
		}
	}

	public long insert(String[] columns, String[] params) {
		ContentValues newValues = new ContentValues();
		for (int i = 0; i < columns.length; i++) {
			newValues.put(columns[i], params[i]);
		}
		return db.insert(table, null, newValues);
	}

	public long updateOneData(String[] columns, String[] params, String selectColumn, String selectParam) {
		ContentValues updateValues = new ContentValues();
		for (int i = 0; i < columns.length; i++) {
			updateValues.put(columns[i], params[i]);
		}
		return db.update(table, updateValues, selectColumn + "=" + selectParam, null);
	}

	public long updateAllData(String[] columns, String[] params) {
		ContentValues updateValues = new ContentValues();
		for (int i = 0; i < columns.length; i++) {
			updateValues.put(columns[i], params[i]);
		}
		return db.update(table, updateValues, null, null);
	}

	public long deleteAllData() {
		return db.delete(table, null, null);
	}

	public long deleteOneData(String selectColumn, String selectParam) {
		return db.delete(table, selectColumn + "=" + selectParam, null);
	}

	public Object[] queryAllData(String[] columns) {
		Cursor results = db.query(table, columns, null, null, null, null, null);
		return ConvertToObject(results);
	}

	public Object[] queryOneData(String[] columns, String selectColumn, String selectParam) {
		Cursor results = db.query(table, columns, selectColumn + "=" + selectParam,
				null, null, null, null);
		return ConvertToObject(results);
	}

	public Object[] ConvertToObject(Cursor cursor) {
		int resultCounts = cursor.getCount();
		if (resultCounts == 0 || !cursor.moveToFirst()) {
			return null;
		}
		Object[] objects = new Object[resultCounts];
		return objects;
	}

	private class DBOpenHelper extends SQLiteOpenHelper {
		private String table;
		private String table_create;

		public DBOpenHelper(Context context, String name, CursorFactory factory, int version, String table, String table_create) {
			super(context, name, factory, version);
			this.table = table;
			this.table_create = table_create;
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL("create table user(_id integer primary key autoincrement, username text not null, password text, avatar text, introduction text, sex text, email text, education text, job text, birthday text);");
			database.execSQL("create table article(_id integer primary key, title text, author text, date text, description text, firstPicture text, content text, star integer, views integer);");
			database.execSQL("create table image(url text not null, base64 text not null);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			database.execSQL("DROP TABLE IF EXISTS " + table);
			onCreate(database);
		}
	}
}