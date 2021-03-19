package top.naccl.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 文章实体类
 * @Author: Naccl
 * @Date: 2020-10-06
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
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
}
