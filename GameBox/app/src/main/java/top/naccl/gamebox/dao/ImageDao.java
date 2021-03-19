package top.naccl.gamebox.dao;

import android.content.Context;
import android.database.Cursor;

import top.naccl.gamebox.bean.Image;
import top.naccl.gamebox.util.DBUtils;

public class ImageDao extends DBUtils {
	private static final String TABLE = "image";
	private static final String TABLE_CREATE = "create table image(url text not null, base64 text not null);";

	public ImageDao(Context context) {
		super(context, TABLE, TABLE_CREATE);
	}

	@Override
	public Image[] ConvertToObject(Cursor cursor) {
		int resultCounts = cursor.getCount();
		if (resultCounts == 0 || !cursor.moveToFirst()) {
			return null;
		}
		Image[] images = new Image[resultCounts];
		for (int i = 0; i < resultCounts; i++) {
			images[i] = new Image();
			images[i].setUrl(cursor.getString(cursor.getColumnIndex("url")));
			images[i].setBase64(cursor.getString(cursor.getColumnIndex("base64")));
			cursor.moveToNext();
		}
		return images;
	}
}
