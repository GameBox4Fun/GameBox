package top.naccl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.naccl.model.entity.Comment;

import java.util.List;

/**
 * @Description: 文章评论持久层接口
 * @Author: Naccl
 * @Date: 2020-11-16
 */
@Mapper
@Repository
public interface CommentMapper {
	List<top.naccl.model.vo.Comment> listComment(Long articleId);

	int saveComment(Comment comment);
}
