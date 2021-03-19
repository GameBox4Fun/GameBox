package top.naccl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.naccl.exception.PersistenceException;
import top.naccl.mapper.FavoriteMapper;
import top.naccl.model.entity.Favorite;
import top.naccl.service.FavoriteService;

import java.util.List;

/**
 * @Description: 收藏夹业务层实现
 * @Author: Naccl
 * @Date: 2020-10-16
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
	@Autowired
	FavoriteMapper favoriteMapper;

	@Transactional
	@Override
	public void saveFavorite(Favorite favorite) {
		if (favoriteMapper.saveFavorite(favorite) != 1) {
			throw new PersistenceException("操作失败");
		}
	}

	@Transactional
	@Override
	public void removeFavorite(Favorite favorite) {
		if (favoriteMapper.removeFavorite(favorite) != 1) {
			throw new PersistenceException("操作失败");
		}
	}

	@Override
	public Favorite findByUserIdAndArticleId(Long userId, Long articleId) {
		return favoriteMapper.findByUserIdAndArticleId(userId, articleId);
	}

	@Override
	public List<Favorite> listFavorite(Long userId) {
		return favoriteMapper.listFavoriteByUserId(userId);
	}

	@Override
	public int countFavoriteByUserId(Long userId) {
		return favoriteMapper.countFavoriteByUserId(userId);
	}
}
