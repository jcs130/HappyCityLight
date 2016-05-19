# City digital Pulse

本项目包括以下几个部分：

- 数据获取模块 (MessageGetter)
- 使用Weka的独立情绪分析模块 (NLPPart) 以及打包好的项目文件 (ZLNLPLibs)
- 数据分析模块 (OfflineStatistic)
- 后台服务器模块 (HappyCityLight)
- 前台网页部分 (WebPages)
- 部署或初始化要用到的脚本(script file)
- 部署用的文件(Develop)

## 1.开发环境

1.安装java8 sdk

本项目主要使用JAVA作为程序语言，由于自然语言处理模块使用了一个第三方库需要使用JAVA8，所以整个项目也使用JAVA8，如果需要更改需要更改自然语言处理模块。本项目大部分组件都使用Java编写所以可以很好地支持多种平台。

http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

默认路径安装

2.IDE Eclipse (JavaEE)

本项目推荐使用Eclipse作为开发工具，使用Eclipse可以直接导入项目文件而不需要做过多更改。

http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars2

直接解压缩

3.MySQL 5.7

本项目使用MySQL作为结构化数据存储数据库，可以不使用MySQL5.7，用其他的版本也可以。但是MySQL5.7原生支持直接存储JSON格式的数据或许对以后的扩展更方便。推荐使用MySQLWorkbench作为数据库操作软件

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

## 2.导入项目

- 下载项目的源代码，解压缩至文件夹
- 打开Eclipse，导入所有工程项目 (File -> Import -> General -> Existing Projects into Workspace)
- 选择源代码文件夹中的所有项目（如果不想对线上版本做出任何改动，选择 Copy projects into workspace）
- 设置Eclipse的服务器环境为Tomcat8 (Properties -> Server -> Runtime Enviroments -> add Tomecat 8.0)
- 导入数据库表结构 (MySQLWorkbench -> Data Import/Restore -> Import from Self-Contained File Choose"DatabaseInit.sql" -> Dump Structure and Data -> Start Import)
- 设置项目数据库地址、用户名、密码为本机的数据库
  - 后台服务器： /HappyCityLight/src/com/citydigitalpulse/webservice/app/ServerConfig.java
  - 统计模块： /OfflineStatistic/src/com/citydigitalpulse/OfflineStatistic/app/AppConfig.java
  - 数据采集模块： /TwitterStreamGetter2SQL/src/com/citydigitalpulse/collector/TwitterGetter/app/Config.java
- 改变数据采集模块上传的目标地址，以本地为例： (public static String DCI_SERVER_URL = "http://localhost:8080/HappyCityLight/api/";)


## 2.部署环境

部署环境为部署在服务上长时间运行的环境，可以是本地服务器也可以是云服务器。部署环境要与开发环境一致但是要更改某些设置。

- 可以只安装JRE不安装JDK
- Tomecat 服务器端口设置为80/443, 并设置服务器防火墙打开这个端口, 加入系统服务并开机自动运行
- MySQL 安装时选择服务器设置并开机自动运行，设置防火墙打开MySQL的网络端口（默认为3306）
