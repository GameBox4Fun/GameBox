package top.naccl.service;

import top.naccl.model.entity.Comment;

import java.util.List;

public interface CommentService {
	List<top.naccl.model.vo.Comment> listComment(Long articleId);

	int saveComment(Comment comment);
}
