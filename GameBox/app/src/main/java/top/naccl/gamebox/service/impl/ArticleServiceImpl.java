package top.naccl.gamebox.service.impl;

import android.content.Context;

import top.naccl.gamebox.bean.Article;
import top.naccl.gamebox.dao.ArticleDao;
import top.naccl.gamebox.service.ArticleService;

public class ArticleServiceImpl implements ArticleService {
	private static final String[] allColumns = new String[]{"_id", "title", "author", "date", "description", "firstPicture", "content", "star", "views"};
	ArticleDao articleDao;

	public ArticleServiceImpl(Context context) {
		this.articleDao = new ArticleDao(context);
	}

	@Override
	public Article getArticle(Long id) {
		articleDao.open();
		Article[] articles = (Article[]) articleDao.queryOneData(allColumns, "_id", String.valueOf(id));
		articleDao.close();
		if (articles != null && articles.length == 1) {
			return articles[0];
		}
		return null;
	}

	@Override
	public boolean deleteArticle(Long id) {
		articleDao.open();
		long res = articleDao.deleteOneData("_id", String.valueOf(id));
		articleDao.close();
		if (res == -1) {
			return false;
		}
		return true;
	}

	@Override
	public boolean saveArticle(Article article) {
		articleDao.open();
		long res = articleDao.insert(allColumns, new String[]{String.valueOf(article.getId()), article.getTitle(), article.getAuthor(), article.getDate(), article.getDescription(), article.getFirstPicture(), article.getContent(), String.valueOf(article.getStar()), String.valueOf(article.getViews())});
		articleDao.close();
		if (res == -1) {
			return false;
		}
		return true;
	}

	@Override
	public boolean updateViews(Article article) {
		articleDao.open();
		long res = articleDao.updateOneData(new String[]{"views"}, new String[]{String.valueOf(article.getViews())}, "_id", String.valueOf(article.getId()));
		articleDao.close();
		if (res == -1) {
			return false;
		}
		return true;
	}

	@Override
	public boolean updateStar(Article article) {
		articleDao.open();
		long res = articleDao.updateOneData(new String[]{"star"}, new String[]{String.valueOf(article.getStar())}, "_id", String.valueOf(article.getId()));
		articleDao.close();
		if (res == -1) {
			return false;
		}
		return true;
	}
}
