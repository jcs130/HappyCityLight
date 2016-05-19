# 后台服务器

本项目为后台服务器，使用 Jersery (https://jersey.java.net) 作为RESTful框架。

本项目使用Maven进行依赖包的管理，下载源代码以后直接通过Eclipse导入项目等待自动下载以来的包即可。

## 包结构及功能说明

- com.citydigitalpulse.webservice.api
  - 实现了服务器的API，根据不同的功能分为不同的文件，原则上一个文件负责一类的功能，例如UserResource.java里主要实现用户注册登录验证等相关功能。
- com.citydigitalpulse.webservice.app
  - APIApplication.java: 应用程序的设置文件，在添加一个新的API文件时需要在APIApplication.java中注册
  - jersey文档：https://jersey.java.net/documentation/latest/index.html
  - jersey 例子: https://afsinka.wordpress.com/2015/12/27/restful-web-service-example-with-jersey-2-and-tomcat-8/
  - CORSFilter.java: 由于前后端分离，需要设置跨域访问 http://www.codingpedia.org/ama/how-to-add-cors-support-on-the-server-side-in-java-with-jersey/
  - ServerConfig.java: 用于设置服务器连接数据库的地址（重要）
- com.citydigitalpulse.webservice.dao
  - 用于实现后台服务器与数据库的操作
  - 系统中有四个数据库，分别是
    - 用户账户资料数据库（UserAccountDAO）
    - 语料标注数据库（MarkingMessageDAO）
    - 数据获取模块控制数据库（CollectorControllerDAO）
    - 数据存储数据库（MessageSavingDAO）
- com.citydigitalpulse.webservice.dao.impl
  - 用于连接数据库以及实现具体的数据操作方法
  - RedisUtil.java 用于操作 Redis缓存数据库
- com.citydigitalpulse.webservice.mail
  - 使用javax.mail包进行邮件发送, 例子：http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/
  - 现在我是用的是我的私人邮箱，需要替换为一个专用的邮箱（例如申请一个support@CityDigitalPulse.com）
- com.citydigitalpulse.webservice.model
  - 应用用到的各种对象的类，类似于API，推荐将相关的对象放在一个包下，例如与用户相关的类都放在user包内
  - 注意在创建自定义对象时要符合JavaBeans规范(https://en.wikipedia.org/wiki/JavaBeans)方便组装json格式
  - 可根据需求重写toString, equals 等方法
- com.citydigitalpulse.webservice.tool
  - 实现加密解密发送邮件情绪识别等通用功能的方法，采用静态类的形式方便调用
- models
  - 存放训练好的情绪分类的模型
- lib
  - 存放不在Maven上的外部类库（例如我自己的情绪判断包）
- WebContent
  - 存放与前端相关的文件
  - WEB-INF 的 web.xml 为服务器设置，在这里设定服务器的参数（例如Jersery的设置）
  - 注意在WebContent的网页应该与WebPages项目中的保持一致，但是WebContent/resources/dist/js/CDP.serverConfig.js中的serverURL 要改为 "api/"
- pom.xml 是maven的配置文件，在这个文件中修改包的引用
