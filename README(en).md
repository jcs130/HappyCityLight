# City digital Pulse

This project has the following seven parts:

- Getting message part(MeasageGetter)
- Nature language processing part by Weka(NLPPart) and the zipped project files(ZLNLPLibs)
- Data analysis part(OfflineStatistic)
- Server Part(HappyCityLight)
- Web pages(WebPages)
- Script files for initializing and deployment 

- Files for deployment (Deploy)

## 1.Develop Environment 

1.Install java8 sdk

This project is mainly done by JAVA and since a third party library for nature language processing is developed in JAVA 8 so this whole project adopted JAVA 8. Thus, this project can be run on different platforms

http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Default installation path

2.IDE Eclipse (JavaEE)

The suggested IDE for this project is Eclipse and you can easily import the project files to Eclipse without making many changes.


http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/mars2
Unzipped the file directly 

3.MySQL 5.7

MySQL is the database to store the structured data for this project. You can use other versions as well. However, MySQL 5.7supports JSON data type and may make it easier for future updates. MySQL workbench is suggested for operation to the database

http://dev.mysql.com/downloads/mysql/

Default installation path. Set up develop environment, user name and password during the installation

Set the default charset as the utf8mb4.

https://mathiasbynens.be/notes/mysql-utf8mb4


4.Redis 3.0

Redis is used for the in-memory database in this project. Choose different client versions for different platforms

- http://redis.io/download (Linux)
- https://github.com/MSOpenTech/redis (Windows)
- https://medium.com/@petehouston/install-and-config-redis-on-mac-os-x-via-homebrew-eb8df9a4f298#.rhrdn6z6q (Mac OS X)


5.Tomcat 8.0 (Deployer)

Tomcat 8 is used as the server.

http://tomcat.apache.org/download-80.cgi

Unzip directly

## 2.Deployment environment

Deployment environment should be able to run for a long period of time. It could be deployed locally or on the cloud. The environment of deployment and development are of the same. However, you have to change some configuration files.
- The user should install JRE however, JDK is not required.
- The port of Tomcat should be configured as 80/443. The user also need to configure firewall to open this port. Also, please add tomcat into system schedule and have it
 auto start when server restarted.
-While install Mysql, please select auto start and configure firewall to open default port for Mysql(3336)

## 3.Import project

- Download project source code and unzip the file to target directory.
- Open Eclipse and import all project file. (File -> Import -> General -> Existing Projects into Workspace)
- Select all files in source code file.（If the user do not want to make any change，select "Copy projects into workspace"）
- Tomcat8 Configure Eclipse sever service to Tomcat8 (Properties -> Server -> Runtime Enviroments -> add Tomecat 8.0)
- Import database (MySQLWorkbench -> Data Import/Restore -> Import from Self-Contained File Choose -> Dump Structure and Data -> Start Import)
- Initialize database file in "Deploy/script file/" folder
    - cdpsavingstructure.sql (Database structure)
    - cdpwebserverdatabase.sql (Backend database structure and data)
    - controllerdb.sql (The controller of data collector)
    - DatabaseInit.sql (Contains all three database schema upwards and a little amount of data)

- Configure database address, username,password as local database.
  - Backend server： /HappyCityLight/src/com/citydigitalpulse/webservice/app/ServerConfig.java
  - Statistic module： /OfflineStatistic/src/com/citydigitalpulse/OfflineStatistic/app/AppConfig.java
  - Data capture module： /TwitterStreamGetter2SQL/src/com/citydigitalpulse/collector/TwitterGetter/app/Config.java
- Change upwards modules's target address，for example： (public static String DCI_SERVER_URL = "http://localhost:8080/HappyCityLight/api/";)

## 4.Deployment on the server

### Website server

For this project we are using Tomcat8.0 as server. However,the user can also use Jetty(https://en.wikipedia.org/wiki/Jetty_(web_server)).

Use eclipse to compress backend server to .war file  and copy this file into Tomcat/Webapp folder. If Tomcat has already started, it will automatically unzip those file. Else, the user will have to start Tomcat manually. Our recommendation is that restart Tomcat each time when there are any updates.

After deploying the service on the server, enter http://localhost/(WAR_FILE_NAME) in any browser，if the user has named .war as ROOT.war,the user can use the service directly by typing http://localhost in the browser.

### Data capture server

Copy paste the GetterServer in Deploy folder to the server.

- MessageGetter4Twitter_lab_split.jar This is used for get data from twitter. It will store all the data based on date into the database.
- init.sql When reopen twitter capture program, the user must run the SQL script file in order to restore controller database.
- YesterdayStatiisticAndUpdate2days.jar  Statistics for all the data captured yesterday. It will process the emotion and store the result into the database.
- StatiisticAndUpdate.jar Used for processing data within certain period of time which is configured manually by users. The user can set different date by manually configure (statistic_settings.txt)

### Scripts for automatically run the program:
The user can write a script to set the routine for running the program. Currently we have two scripts:
- TwitterStreamCollector_start.bat This one is used for automatically running the program on start.
- YesterdayStatiisticAndUpdate.bat This one is used for statistic of data captured yesterday.
