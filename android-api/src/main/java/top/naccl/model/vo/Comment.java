package top.naccl.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @Description: 文章评论vo
 * @Author: Naccl
 * @Date: 2020-11-16
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Comment {
	private String username;
	private String avatar;
	private String content;
	private Date createTime;
}
