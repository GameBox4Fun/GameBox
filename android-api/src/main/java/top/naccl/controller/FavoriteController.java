package top.naccl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.naccl.model.entity.Article;
import top.naccl.model.entity.Favorite;
import top.naccl.model.vo.Result;
import top.naccl.service.ArticleService;
import top.naccl.service.FavoriteService;
import top.naccl.service.UserService;
import top.naccl.util.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 收藏夹
 * @Author: Naccl
 * @Date: 2020-10-16
 */

@RestController
@RequestMapping("/favorite")
public class FavoriteController {
	@Autowired
	FavoriteService favoriteService;
	@Autowired
	UserService userService;
	@Autowired
	ArticleService articleService;

	/**
	 * 按文章id查询用户是否已收藏，未收藏则添加收藏，已收藏则取消收藏
	 *
	 * @param id      文章id
	 * @param request
	 * @return
	 */
	@PostMapping("/{id}")
	public Result favorite(@PathVariable Long id, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization");
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				String userId = JwtUtils.getUserId(jwt);
				Favorite favorite = favoriteService.findByUserIdAndArticleId(Long.valueOf(userId), id);
				if (favorite != null) {//已收藏，取消收藏
					favoriteService.removeFavorite(favorite);
					Map<String, Object> map = new HashMap<>();
					map.put("favorite", false);
					return Result.ok("已取消收藏", map);
				} else {//未收藏，添加收藏
					Favorite f = new Favorite();
					f.setUserId(Long.valueOf(userId));
					f.setArticleId(id);
					favoriteService.saveFavorite(f);
					Map<String, Object> map = new HashMap<>();
					map.put("favorite", true);
					return Result.ok("收藏成功", map);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return Result.create(403, "Token已失效，请重新登录！");
			}
		} else {
			return Result.create(403, "请登录");
		}
	}

	/**
	 * 获取当前用户文章收藏夹列表
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/list")
	public Result userFavorite(HttpServletRequest request) {
		String jwt = request.getHeader("Authorization");
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				String userId = JwtUtils.getUserId(jwt);
				List<Favorite> favorites = favoriteService.listFavorite(Long.valueOf(userId));
				List<Article> articles = new ArrayList<>();
				for (Favorite favorite : favorites) {
					articles.add(articleService.getFavoriteArticle(favorite.getArticleId()));
				}
				return Result.ok("请求成功", articles);
			} catch (Exception e) {
				e.printStackTrace();
				return Result.create(403, "Token已失效，请重新登录！");
			}
		} else {
			return Result.create(403, "请登录");
		}
	}

	/**
	 * 获取用户收藏文章数
	 *
	 * @param request
	 * @return
	 */
	@GetMapping("/num")
	public Result userFavoriteNum(HttpServletRequest request) {
		String jwt = request.getHeader("Authorization");
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				String userId = JwtUtils.getUserId(jwt);
				int num = favoriteService.countFavoriteByUserId(Long.valueOf(userId));
				return Result.ok("请求成功", num);
			} catch (Exception e) {
				e.printStackTrace();
				return Result.create(403, "Token已失效，请重新登录！");
			}
		} else {
			return Result.create(403, "请登录");
		}
	}
}
