package top.naccl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.naccl.model.entity.Article;
import top.naccl.model.entity.Comment;
import top.naccl.model.entity.Favorite;
import top.naccl.model.vo.Result;
import top.naccl.service.ArticleService;
import top.naccl.service.CommentService;
import top.naccl.service.FavoriteService;
import top.naccl.service.UserService;
import top.naccl.util.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 文章
 * @Author: Naccl
 * @Date: 2020-10-16
 */
@RestController
@RequestMapping("/article")
public class ArticleController {
	@Autowired
	ArticleService articleService;
	@Autowired
	UserService userService;
	@Autowired
	FavoriteService favoriteService;
	@Autowired
	CommentService commentService;

	/**
	 * 分页获取文章List
	 *
	 * @param pageNum 页码
	 * @return
	 */
	@GetMapping("/list/{pageNum}")
	public Result listArticle(@PathVariable Integer pageNum) {
		List<Article> articles = articleService.listArticle(pageNum);
		return Result.ok("请求成功", articles);
	}

	/**
	 * 按id获取文章详情
	 *
	 * @param id      文章id
	 * @param request
	 * @return
	 */
	@GetMapping("/{id}")
	public Result article(@PathVariable Long id, HttpServletRequest request) {
		boolean favorite = checkFavorite(id, request);
		Article article = articleService.findById(id);
		Map<String, Object> map = new HashMap<>();
		map.put("article", article);
		map.put("favorite", favorite);
		return Result.ok("请求成功", map);
	}

	/**
	 * 按文章id点赞
	 *
	 * @param id      文章id
	 * @param request
	 * @return
	 */
	@PostMapping("/star/{id}")
	public Result star(@PathVariable Long id, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization");
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				JwtUtils.getTokenBody(jwt);
				articleService.updateArticleStarById(id);
				return Result.ok("点赞成功");
			} catch (Exception e) {
				e.printStackTrace();
				return Result.create(403, "Token已失效，请重新登录！");
			}
		} else {
			return Result.create(403, "请登录");
		}
	}

	/**
	 * 按文章id增加浏览量并获取当前用户是否已收藏
	 *
	 * @param id      文章id
	 * @param request
	 * @return
	 */
	@GetMapping("/view/{id}")
	public Result updateViewAndGetFavorite(@PathVariable Long id, HttpServletRequest request) {
		boolean favorite = checkFavorite(id, request);
		articleService.updateArticleViewById(id);
		Map<String, Object> map = new HashMap<>();
		map.put("favorite", favorite);
		return Result.ok("请求成功", map);
	}

	/**
	 * 按文章id查询当前用户是否已收藏文章
	 *
	 * @param id      文章id
	 * @param request
	 * @return
	 */
	private boolean checkFavorite(Long id, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization");
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				String userId = JwtUtils.getUserId(jwt);
				Favorite favorite = favoriteService.findByUserIdAndArticleId(Long.valueOf(userId), id);
				if (favorite != null) {
					return true;
				}
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 按文章id获取文章下评论
	 *
	 * @param articleId 文章id
	 * @return
	 */
	@GetMapping("/comment/{articleId}")
	public Result comment(@PathVariable Long articleId) {
		List<top.naccl.model.vo.Comment> comment = commentService.listComment(articleId);
		return Result.ok("请求成功", comment);
	}

	/**
	 * 用户提交评论
	 *
	 * @param articleId 文章id
	 * @param content   评论内容
	 * @return
	 */
	@PostMapping("/comment/{articleId}")
	public Result comment(@PathVariable Long articleId, @RequestParam String content, HttpServletRequest request) {
		String jwt = request.getHeader("Authorization");
		if (JwtUtils.judgeTokenIsExist(jwt)) {
			try {
				String userId = JwtUtils.getUserId(jwt);
				Comment comment = new Comment();
				comment.setUserId(Long.valueOf(userId));
				comment.setArticleId(articleId);
				comment.setContent(content);
				comment.setCreateTime(new Date());
				commentService.saveComment(comment);
				return Result.ok("评论成功");
			} catch (Exception e) {
				e.printStackTrace();
				return Result.create(403, "Token已失效，请重新登录！");
			}
		} else {
			return Result.create(403, "请登录");
		}
	}
}
