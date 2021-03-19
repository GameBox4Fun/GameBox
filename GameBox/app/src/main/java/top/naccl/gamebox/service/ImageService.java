package top.naccl.gamebox.service;

import top.naccl.gamebox.bean.Image;

public interface ImageService {
	Image getImage(String url);

	boolean saveImage(Image image);
}
