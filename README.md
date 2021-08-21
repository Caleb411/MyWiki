# 我的知识库Wiki项目（基于SpringBoot+Vue）

## 源码下载

```
git clone https://github.com/Caleb411/MyWiki.git
```

## 项目初始化

- 刷新maven依赖
- 安装vue cli并初始化web目录下的前端项目
- 本地创建数据库wikidev并执行doc目录下的all.sql脚本
- 在application.properties中修改数据库配置为本地配置
- 如需在线部署：服务器部署脚本和配置文件在/doc/deploy目录下

## 项目启动

- 启动服务端：WikiApplication
- 启动前端网站：web\package.json
- 点击启动日志里的网站链接即可访问首页

## 特色功能

- 用户登录后可以对内容进行管理（登录状态维持24小时），未登录状态只可以查看
- 同一个IP24小时内对同一篇文档不能重复点赞，点赞后会通知所有连接会话的用户
- 预计今日阅读量由今日当前阅读量和当前时间点占一天总时间的百分比推算出来
- 预计今日阅读增长率是预计今日阅读量相对昨日阅读量的增长率
