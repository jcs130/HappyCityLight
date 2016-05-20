# 数据获取模块

本模块为数据抓取模块，本项目目前有Twitter获取模块和Instagram. 

Data collector is used to acquire and upload the data. There can be multiple collectors for different data sources. In our CDP, we develop two collectors for gathering data from two social networks (i.e., Twitter and Instagram).

The collectors are responsible for collecting different kinds of data from different data sources, and sending the data to the cloud servers. As the data may be contributed by various sensors, we define a collector for each sensor separately. The intra-connections are also indicated.A data collector should provide 3 kinds of APIs.


- Data Collector Controller API
  The collector should flollow the software as a service (SaaS) model and can be controled remotely. Data collector controller API can provide the Create, Read, Update and Delete (CRUD) functions.
  - Create: Start a new collection task. After the collector is started, data collector controller will change the status in the collector status database and begin collecting data from sensors or data sources.
  - Read: Get the status of collector.It can let other part of the system knows the collector is working or not. If the collector need to restart or crashed or has other problems, it can easily get the status from the status database without reset the settings.
  - Update: This allows to modify the setting of the task such change the address for uploading the real-time data.
  - Delete: Remove a task from the collector to stop collecting.

- Get Real-time Data API
  Get Real-time Data API will return the latest data from the sensors or data sources if the collector is running. It's the most important function in this part. In CDP, the collector send the real-time data to the address provided in the collector status database, that means we can let the collector send the real-time data to different address.
- Get History Data API
  The data from sensors or data sources also goes into the local backup database. This database is designed to store the recent data. If the network has problems or other unusual situation, other part of the system can call the get history data API to get a lot data using one API call. This can make the system has more availability.

TwitterStreamGetter2SQL 为实时获取Twitter的模块

yuanyuanli-use-web-crawler-to-get-sns-posts 文件夹中是 Yuanyuan Li 的通过爬虫获取数据的模块
