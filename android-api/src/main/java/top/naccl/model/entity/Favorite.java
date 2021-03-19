package top.naccl.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description: 收藏夹实体类
 * @Author: Naccl
 * @Date: 2020-10-06
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Favorite {
	private Long id;
	private Long userId;
	private Long articleId;
}
