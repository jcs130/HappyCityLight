# Twitter实时获取程序

## 程序流程概述

In our system, we use Twitter4J as the client of the Twitter API. Twitter4J is an unofficial Java library for the Twitter API, we developed a multi-thread collector program to receive the income data. When new data comes, Upload Real-time Thread will upload the data to the cloud Server. In order to reduce the workload of the database system, we write the new data into cache memory first, at the same time, the Insert to DB Thread keep checking the memory and if there's enough data in cache then upload the data from cache into database.

Twitter's Streaming APIs continuously deliver new responses to REST API queries over a long-lived HTTP connection. Receive updates on the latest Tweets matching a search query, stay in sync with user profile updates, and more. The streaming process gets the input Tweets and performs any parsing, filtering, and/or aggregation needed before storing the result to a data store. The HTTP handling process queries the data store for results in response to user requests. While this model is more complex than the first example, the benefits from having a real-time stream of Tweet data make the integration worthwhile for many types of apps. The process of the Streaming API shows in https://dev.twitter.com/streaming/overview. The Twitter Streaming API have many parameters (https://dev.twitter.com/streaming/reference/post/statuses/filter) and in  our system we only use locations parameter to get the data.
  
##包结构及功能

### com.citydigitalpulse.collector.TwitterGetter.app
- Config.java 程序的设置文件，用来设置数据库地址
- getterMain.java 程序运行入口

### com.citydigitalpulse.collector.TwitterGetter.dao
  本程序要用到两个数据库：
  
  - 状态及控制数据库(InfoGetterDAO.java) 用来获得需要监听的地区信息以及更新监听状态
  - 数据存储数据库(TwitterSaveDAO.java) 用来存储获得的数据

### com.citydigitalpulse.collector.TwitterGetter.dao.impl 
  实现了具体的数据操作的方法
### com.citydigitalpulse.collector.TwitterGetter.model
  包含所有和数据获取程序相关的对象的文件
### com.citydigitalpulse.collector.TwitterGetter.service
  数据抓取程序所用到的线程
  
  - StreamsManager.java 管理Stream监视线程的管理线程，查询区块表，获取所有使用次数大于0且运行状态为0（停止）的区块，新建相应的Stream监听线程
  - ServiceThread.java 为其他线程的父类，规定了线程的一些通用属性和方法，方便进行区分和管理
  - InsertIntoDBThread.java 循环将内存中的数据插入数据库，等到一定数量在进行数据库写入操作，数量过大可能会造成数据丢失，数量过小可能会造成数据库压力过大。
  - ScanRegsThread.java 循环扫描控制数据库中有没有需要状态变更的区域（新增，暂停，删除等）目前只实现了新增监听区域
  - StartRegThreads.java 1.扫描Reg数据库，获取所有状态为3的区域并将所有的reg区域的小区域添加 2.根据小区域的坐标找到Stream区块并且修改Stream区块的状态
  - ThreadsPool.java 自己写的一个用于管理正在运行中的线程的静态类，用来实现线程的按照要求的增加和删除以及将不同区域整合的方法（还未实现，为了节约资源，将若干个城市的收集任务放在一个线程中，当增加城市时优先将该城市添加入还未饱和的线程中）
  
### com.citydigitalpulse.collector.TwitterGetter.service.twitter4j
  使用Twitter4j获取数据的包
  
  - LocatedTwitterListener.java 实时获取原始数据并将原始的JSON解析并提取需要的信息组装成Java对象
  - TwitterAPIKey.java 用来存储API Key的对象
  - TwitterStreamThread.java 用来获取数据的线程，将若干个区块加入一个监视线程进行监视，并且允许修改这几个区域或是直接停止
  - TwitterTools.java 用来存储Twitter的API Key并且循环调用防止超出限制

### com.citydigitalpulse.collector.TwitterGetter.tool
加密解密等方法
