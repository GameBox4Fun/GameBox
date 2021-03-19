package top.naccl.service;

import top.naccl.model.entity.Favorite;

import java.util.List;

public interface FavoriteService {
	void saveFavorite(Favorite favorite);

	void removeFavorite(Favorite favorite);

	Favorite findByUserIdAndArticleId(Long userId, Long ArticleId);

	List<Favorite> listFavorite(Long userId);

	int countFavoriteByUserId(Long userId);
}
