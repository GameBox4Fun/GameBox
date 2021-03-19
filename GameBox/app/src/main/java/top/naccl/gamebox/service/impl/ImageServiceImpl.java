package top.naccl.gamebox.service.impl;

import android.content.Context;

import top.naccl.gamebox.bean.Image;
import top.naccl.gamebox.dao.ImageDao;
import top.naccl.gamebox.service.ImageService;

public class ImageServiceImpl implements ImageService {
	private static final String[] allColumns = new String[]{"url", "base64"};
	ImageDao imageDao;

	public ImageServiceImpl(Context context) {
		this.imageDao = new ImageDao(context);
	}

	@Override
	public Image getImage(String url) {
		imageDao.open();
		Image[] images = (Image[]) imageDao.queryOneData(allColumns, "url", url);
		imageDao.close();
		if (images != null && images.length == 1) {
			return images[0];
		}
		return null;
	}

	@Override
	public boolean saveImage(Image image) {
		imageDao.open();
		long res = imageDao.insert(allColumns, new String[]{image.getUrl(), image.getBase64()});
		imageDao.close();
		if (res == -1) {
			return false;
		}
		return true;
	}
}
