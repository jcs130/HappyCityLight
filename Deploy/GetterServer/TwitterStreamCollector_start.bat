@echo on
cd C:\Users\Administrator\Google Drive\LabCollector\
set errorlevel=0
set user_mysql=jcs130
set password_mysql=jcsss130
set path_home_mysql="C:\Program Files\MySQL\MySQL Server 5.7\bin\"
%path_home_mysql%mysql -u%user_mysql% -p%password_mysql% < init.sql
java -jar MessageGetter4Twitter_lab_split.jar


