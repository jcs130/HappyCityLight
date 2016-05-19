[![Build status](https://travis-ci.org/mpromonet/v4l2rtspserver.png)](https://travis-ci.org/mpromonet/v4l2rtspserver)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/4644/badge.svg)](https://scan.coverity.com/projects/4644)
[![Coverage Status](https://coveralls.io/repos/github/mpromonet/v4l2rtspserver/badge.svg?branch=master)](https://coveralls.io/github/mpromonet/v4l2rtspserver?branch=master)

# HappyCityLight

本项目包括以下几个部分：
- 数据获取模块 (MessageGetter)
- 使用Weka的独立情绪分析模块 (NLPPart) 以及打包好的项目文件 (ZLNLPLibs)
- 数据分析模块 (OfflineStatistic)
- 后台服务器模块 (HappyCityLight)
- 前台网页部分 (WebPages)

## 1.开发环境搭建

1.安装java8 sdk

本项目主要使用JAVA作为程序语言，由于自然语言处理模块使用了一个第三方库需要使用JAVA8，所以整个项目也使用JAVA8，如果需要更改需要更改自然语言处理模块。本项目大部分组件都使用Java编写所以可以很好地支持多种平台。

http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

默认路径安装

2.IDE Eclipse (JavaEE)

本项目推荐使用Eclipse作为开发工具，使用Eclipse可以直接导入项目文件而不需要做过多更改。

http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars2

直接解压缩

3.MySQL 5.7

本项目使用MySQL作为结构化数据存储数据库，可以不使用MySQL5.7，用其他的版本也可以。但是MySQL5.7原生支持直接存储JSON格式的数据或许对以后的扩展更方便。

http://dev.mysql.com/downloads/mysql/

默认路径安装,安装时选择安装为开发环境 设置用户名和密码

4.Redis 3.0

本项目使用Redis作为内存缓存数据库，Redis不是使用Java开发所以不同平台需要不同的客户端。

- http://redis.io/download (Linux) 
- https://github.com/MSOpenTech/redis (Windows)
- https://medium.com/@petehouston/install-and-config-redis-on-mac-os-x-via-homebrew-eb8df9a4f298#.rhrdn6z6q (Mac OS X)


5.Tomcat 8.0 (Deployer)

本项目使用Tomcat8作为后台服务器，开发环境可以直接下载压缩包。

http://tomcat.apache.org/download-80.cgi

直接解压缩至文件夹

