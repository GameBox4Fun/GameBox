package top.naccl.gamebox.bean;

import java.util.Date;

public class Comment {
	private String username;
	private String content;
	private Date createTime;
	private String avatar;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return "Comment{" +
				"username='" + username + '\'' +
				", content='" + content + '\'' +
				", createTime='" + createTime + '\'' +
				", avatar='" + avatar + '\'' +
				'}';
	}
}
