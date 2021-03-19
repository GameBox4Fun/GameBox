package top.naccl.service;

import top.naccl.model.entity.Article;

import java.util.List;

public interface ArticleService {
	List<Article> listArticle(Integer start);

	Article getFavoriteArticle(Long id);

	Article findById(Long id);

	void updateArticleStarById(Long id);

	void updateArticleViewById(Long id);
}
