<p align="center">
	<a href="https://github.com/GameBox4Fun/GameBox" target="_blank">
		<img src="./image/gamebox.png" alt="GameBox logo" style="width: 200px; height: 200px">
	</a>
</p>
<p align="center">
	<img src="https://img.shields.io/badge/JDK-1.8+-orange">
	<img src="https://img.shields.io/badge/SpringBoot-2.2.7.RELEASE-brightgreen">
	<img src="https://img.shields.io/badge/MyBatis-3.5.5-red">
	<img src="https://img.shields.io/badge/API-29-yellow">
	<img src="https://img.shields.io/hexpm/l/plug">
	<img src="https://hits.dwyl.com/GameBox4Fun/GameBox.svg">
</p>


## Features

仿[小黑盒](https://www.xiaoheihe.cn/)安卓APP，含界面原型设计、最新资讯爬虫

主要实现功能：

1. 爬取[触乐网](http://www.chuapp.com/)最新游戏资讯
2. 登录注册
3. jwt 实现登录状态鉴权
4. 首页文章布局
5. 上拉加载更多
6. 文章 HTML 渲染
7. 文章配图从网络动态获取
8. 本地 SQLite 缓存查看过的文章和图片资源
9. 文章点赞收藏
10. 个人资料修改



## 后端

1. 核心框架：[Spring Boot](https://github.com/spring-projects/spring-boot)
2. Token 认证：[jjwt](https://github.com/jwtk/jjwt)
3. 持久层框架：[MyBatis](https://github.com/mybatis/spring-boot-starter)
4. 分页插件：[PageHelper](https://github.com/pagehelper/Mybatis-PageHelper)
5. JSON 库：[fastjson](https://github.com/alibaba/fastjson)



## Android端

1. 网络请求框架：[okhttp3](https://github.com/square/okhttp)
2. JSON 库：[fastjson](https://github.com/alibaba/fastjson)
3. 下拉刷新、上拉加载：[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout)
4. 二维码：[zxing](https://github.com/zxing/zxing)
5. 数据库缓存：[SQLite](https://www.sqlite.org/)



## Screenshots

<table>
	<tr>
		<td><img src="./image/首页.png">首页</td>
		<td><img src="./image/文章1.png">文章1</td>
	</tr>
	<tr>
		<td><img src="./image/文章2.png">文章2</td>
		<td><img src="./image/评论.png">评论</td>
	</tr>
	<tr>
		<td><img src="./image/静态页.png">静态页</td>
		<td><img src="./image/我的.png">我的</td>
	</tr>
	<tr>
		<td><img src="./image/收藏夹.png">收藏夹</td>
		<td><img src="./image/扫描二维码.png">扫描二维码</td>
	</tr>
	<tr>
		<td><img src="./image/设置.png">设置</td>
		<td><img src="./image/修改资料.png">修改资料</td>
	</tr>
	<tr>
		<td><img src="./image/登录.png">登录</td>
		<td></td>
</table>