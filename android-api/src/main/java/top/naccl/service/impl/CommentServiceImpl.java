package top.naccl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.naccl.mapper.CommentMapper;
import top.naccl.model.entity.Comment;
import top.naccl.service.CommentService;

import java.util.List;

/**
 * @Description: 文章评论业务层实现
 * @Author: Naccl
 * @Date: 2020-11-16
 */
@Service
public class CommentServiceImpl implements CommentService {
	@Autowired
	CommentMapper commentMapper;

	@Override
	public List<top.naccl.model.vo.Comment> listComment(Long articleId) {
		return commentMapper.listComment(articleId);
	}

	@Override
	public int saveComment(Comment comment) {
		return commentMapper.saveComment(comment);
	}
}
