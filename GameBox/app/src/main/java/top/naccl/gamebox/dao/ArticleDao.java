package top.naccl.gamebox.dao;

import android.content.Context;
import android.database.Cursor;

import top.naccl.gamebox.bean.Article;
import top.naccl.gamebox.util.DBUtils;

public class ArticleDao extends DBUtils {
	private static final String TABLE = "article";
	private static final String TABLE_CREATE = "create table article(_id integer primary key, title text, author text, date text, description text, firstPicture text, content text, star integer, views integer);";

	public ArticleDao(Context context) {
		super(context, TABLE, TABLE_CREATE);
	}

	@Override
	public Article[] ConvertToObject(Cursor cursor) {
		int resultCounts = cursor.getCount();
		if (resultCounts == 0 || !cursor.moveToFirst()) {
			return null;
		}
		Article[] articles = new Article[resultCounts];
		for (int i = 0; i < resultCounts; i++) {
			articles[i] = new Article();
			articles[i].setId(cursor.getLong(0));
			articles[i].setTitle(cursor.getString(cursor.getColumnIndex("title")));
			articles[i].setAuthor(cursor.getString(cursor.getColumnIndex("author")));
			articles[i].setDate(cursor.getString(cursor.getColumnIndex("date")));
			articles[i].setDescription(cursor.getString(cursor.getColumnIndex("description")));
			articles[i].setFirstPicture(cursor.getString(cursor.getColumnIndex("firstPicture")));
			articles[i].setContent(cursor.getString(cursor.getColumnIndex("content")));
			articles[i].setStar(cursor.getInt(cursor.getColumnIndex("star")));
			articles[i].setViews(cursor.getInt(cursor.getColumnIndex("views")));
			cursor.moveToNext();
		}
		return articles;
	}
}
