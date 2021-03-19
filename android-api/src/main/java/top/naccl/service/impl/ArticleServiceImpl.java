package top.naccl.service.impl;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.naccl.exception.PersistenceException;
import top.naccl.mapper.ArticleMapper;
import top.naccl.model.entity.Article;
import top.naccl.service.ArticleService;

import java.util.List;

/**
 * @Description: 文章业务层实现
 * @Author: Naccl
 * @Date: 2020-10-16
 */
@Service
public class ArticleServiceImpl implements ArticleService {
	@Autowired
	ArticleMapper articleMapper;
	//每页显示7条资讯简介
	private static final Integer pageSize = 7;

	@Override
	public List<Article> listArticle(Integer pageNum) {
		PageHelper.startPage(pageNum, pageSize);
		return articleMapper.listArticle();
	}

	@Override
	public Article getFavoriteArticle(Long id) {
		return articleMapper.getFavoriteArticle(id);
	}

	@Override
	public Article findById(Long id) {
		updateArticleViewById(id);
		return articleMapper.findById(id);
	}

	@Transactional
	@Override
	public void updateArticleStarById(Long id) {
		if (articleMapper.updateStarById(id) != 1) {
			throw new PersistenceException("点赞失败");
		}
	}

	@Transactional
	@Override
	public void updateArticleViewById(Long id) {
		if (articleMapper.updateViewsById(id) != 1) {
			throw new PersistenceException("更新失败");
		}
	}
}
