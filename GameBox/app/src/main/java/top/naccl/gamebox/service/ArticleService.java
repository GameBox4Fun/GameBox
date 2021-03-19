package top.naccl.gamebox.service;

import top.naccl.gamebox.bean.Article;

public interface ArticleService {
	Article getArticle(Long id);

	boolean deleteArticle(Long id);

	boolean saveArticle(Article article);

	boolean updateViews(Article article);

	boolean updateStar(Article article);
}
