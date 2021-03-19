package top.naccl.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import top.naccl.model.entity.Article;

import java.util.List;

/**
 * @Description: 文章持久层接口
 * @Author: Naccl
 * @Date: 2020-10-16
 */
@Mapper
@Repository
public interface ArticleMapper {
	List<Article> listArticle();

	Article getFavoriteArticle(Long id);

	Article findById(Long id);

	int updateViewsById(Long id);

	int updateStarById(Long id);
}
