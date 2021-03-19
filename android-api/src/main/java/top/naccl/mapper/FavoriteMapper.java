package top.naccl.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.naccl.model.entity.Favorite;

import java.util.List;

/**
 * @Description: 收藏夹持久层接口
 * @Author: Naccl
 * @Date: 2020-10-16
 */
@Mapper
@Repository
public interface FavoriteMapper {
	int saveFavorite(Favorite favorite);

	int removeFavorite(Favorite favorite);

	Favorite findByUserIdAndArticleId(Long userId, Long articleId);

	List<Favorite> listFavoriteByUserId(Long userId);

	int countFavoriteByUserId(Long userId);
}
