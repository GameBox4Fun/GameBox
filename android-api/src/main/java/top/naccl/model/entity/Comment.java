package top.naccl.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 文章评论
 * @Author: Naccl
 * @Date: 2020-11-16
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Comment {
	private Long id;
	private Long userId;
	private Long articleId;
	private String content;
	private Date createTime;
}
