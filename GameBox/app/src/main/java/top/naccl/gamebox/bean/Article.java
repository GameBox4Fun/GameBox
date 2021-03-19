package top.naccl.gamebox.bean;


public class Article {
	private Long id;
	private String title;
	private String author;
	private String date;
	private String description;
	private String firstPicture;
	private String content;
	private Integer star;
	private Integer views;

	public Article() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFirstPicture() {
		return firstPicture;
	}

	public void setFirstPicture(String firstPicture) {
		this.firstPicture = firstPicture;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getStar() {
		return star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	@Override
	public String toString() {
		return "Article{" +
				"id=" + id +
				", title='" + title + '\'' +
				", author='" + author + '\'' +
				", date='" + date + '\'' +
				", description='" + description + '\'' +
				", firstPicture='" + firstPicture + '\'' +
				", content='" + content + '\'' +
				", star=" + star +
				", views=" + views +
				'}';
	}
}
