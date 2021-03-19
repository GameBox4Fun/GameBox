package top.naccl.gamebox.bean;


public class Image {
	private String url;
	private String base64;

	public Image() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	@Override
	public String toString() {
		return "Image{" +
				"url='" + url + '\'' +
				", base64='" + base64 + '\'' +
				'}';
	}
}
