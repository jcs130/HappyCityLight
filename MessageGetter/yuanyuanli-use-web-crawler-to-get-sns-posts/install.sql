CREATE DATABASE `msgsaving` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
CREATE DATABASE `searchcontroller` /*!40100 DEFAULT CHARACTER SET utf8 */;

CREATE TABLE `msgsaving`.`full_message_twitter` (
	`num_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
	`raw_id_str` varchar(45) DEFAULT NULL,
	`user_name` varchar(100) DEFAULT NULL,
	`profile_img` varchar(255) DEFAULT NULL,
	`creat_at` bigint(20) DEFAULT NULL,
	`text` mediumtext,
	`emotion_text` varchar(255) DEFAULT NULL,
	`media_types` varchar(255) DEFAULT NULL,
	`media_urls` varchar(255) DEFAULT NULL,
	`media_urls_local` varchar(255) DEFAULT NULL,
	`emotion_medias` varchar(255) DEFAULT NULL,
	`emotion_all` varchar(255) DEFAULT NULL,
	`place_type` varchar(45) DEFAULT NULL,
	`place_name` varchar(45) DEFAULT NULL,
	`place_fullname` varchar(100) DEFAULT NULL,
	`country` varchar(45) DEFAULT NULL,
	`province` varchar(45) DEFAULT NULL,
	`city` varchar(45) DEFAULT NULL,
	`query_location_latitude` double DEFAULT NULL,
	`query_location_langtitude` double DEFAULT NULL,
	`is_real_location` varchar(10) DEFAULT '0',
	`hashtags` mediumtext,
	`replay_to` varchar(45) DEFAULT NULL,
	`lang` varchar(45) DEFAULT NULL,
	`message_from` varchar(45) DEFAULT NULL,
	PRIMARY KEY (`num_id`),
	UNIQUE KEY `record_id_UNIQUE` (`num_id`),
	UNIQUE KEY `raw_id_str_UNIQUE` (`raw_id_str`),
	KEY `times_index` (`creat_at`) USING BTREE,
	KEY `geo_index` (`query_location_latitude`,`query_location_langtitude`) USING BTREE,
	KEY `time_and_geo_index` (`creat_at`,`query_location_latitude`,`query_location_langtitude`) USING BTREE,
	FULLTEXT KEY `text` (`text`)
) ENGINE=InnoDB AUTO_INCREMENT=7157574 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `msgsaving`.`full_message_ig` (
	`num_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
	`raw_id_str` varchar(45) DEFAULT NULL,
	`user_name` varchar(100) DEFAULT NULL,
	`profile_img` varchar(255) DEFAULT NULL,
	`creat_at` bigint(20) DEFAULT NULL,
	`text` mediumtext,
	`emotion_text` varchar(255) DEFAULT NULL,
	`media_types` varchar(255) DEFAULT NULL,
	`media_urls` varchar(255) DEFAULT NULL,
	`media_urls_local` varchar(255) DEFAULT NULL,
	`emotion_medias` varchar(255) DEFAULT NULL,
	`emotion_all` varchar(255) DEFAULT NULL,
	`place_type` varchar(45) DEFAULT NULL,
	`place_name` varchar(45) DEFAULT NULL,
	`place_fullname` varchar(100) DEFAULT NULL,
	`country` varchar(45) DEFAULT NULL,
	`province` varchar(45) DEFAULT NULL,
	`city` varchar(45) DEFAULT NULL,
	`query_location_latitude` double DEFAULT NULL,
	`query_location_langtitude` double DEFAULT NULL,
	`is_real_location` varchar(10) DEFAULT '0',
	`hashtags` mediumtext,
	`replay_to` varchar(45) DEFAULT NULL,
	`lang` varchar(45) DEFAULT NULL,
	`message_from` varchar(45) DEFAULT NULL,
	PRIMARY KEY (`num_id`),
	UNIQUE KEY `record_id_UNIQUE` (`num_id`),
	UNIQUE KEY `raw_id_str_UNIQUE` (`raw_id_str`),
	KEY `times_index` (`creat_at`) USING BTREE,
	KEY `geo_index` (`query_location_latitude`,`query_location_langtitude`) USING BTREE,
	KEY `time_and_geo_index` (`creat_at`,`query_location_latitude`,`query_location_langtitude`) USING BTREE,
	FULLTEXT KEY `text` (`text`)
) ENGINE=InnoDB AUTO_INCREMENT=10019 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `msgsaving`.`full_message_ig_realtime` (
	`num_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
	`raw_id_str` varchar(45) DEFAULT NULL,
	`user_name` varchar(100) DEFAULT NULL,
	`profile_img` varchar(255) DEFAULT NULL,
	`creat_at` bigint(20) DEFAULT NULL,
	`text` mediumtext,
	`emotion_text` varchar(255) DEFAULT NULL,
	`media_types` varchar(255) DEFAULT NULL,
	`media_urls` varchar(255) DEFAULT NULL,
	`media_urls_local` varchar(255) DEFAULT NULL,
	`emotion_medias` varchar(255) DEFAULT NULL,
	`emotion_all` varchar(255) DEFAULT NULL,
	`place_type` varchar(45) DEFAULT NULL,
	`place_name` varchar(45) DEFAULT NULL,
	`place_fullname` varchar(100) DEFAULT NULL,
	`country` varchar(45) DEFAULT NULL,
	`province` varchar(45) DEFAULT NULL,
	`city` varchar(45) DEFAULT NULL,
	`query_location_latitude` double DEFAULT NULL,
	`query_location_langtitude` double DEFAULT NULL,
	`is_real_location` varchar(10) DEFAULT '0',
	`hashtags` mediumtext,
	`replay_to` varchar(45) DEFAULT NULL,
	`lang` varchar(45) DEFAULT NULL,
	`message_from` varchar(45) DEFAULT NULL,
	PRIMARY KEY (`num_id`),
	UNIQUE KEY `record_id_UNIQUE` (`num_id`),
	UNIQUE KEY `raw_id_str_UNIQUE` (`raw_id_str`),
	KEY `times_index` (`creat_at`) USING BTREE,
	KEY `geo_index` (`query_location_latitude`,`query_location_langtitude`) USING BTREE,
	KEY `time_and_geo_index` (`creat_at`,`query_location_latitude`,`query_location_langtitude`) USING BTREE,
	FULLTEXT KEY `text` (`text`)
) ENGINE=InnoDB AUTO_INCREMENT=9826 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `searchcontroller`.`asking` (
	`num_id` int(25) unsigned NOT NULL AUTO_INCREMENT,
	`city_name` char(45) DEFAULT NULL,
	`latitude` double(9,6) DEFAULT NULL,
	`longitude` double(9,6) DEFAULT NULL,
	`radius` bigint(45) DEFAULT NULL,
	`start_date` varchar(45) DEFAULT NULL,
	`end_date` varchar(45) DEFAULT NULL,
	`in_what_lan` varchar(5) DEFAULT NULL,
	`required_by` varchar(25) DEFAULT NULL,
	`required_at` varchar(25) DEFAULT NULL,
	`message_from` varchar(45) DEFAULT NULL,
	`getting_status` int(5) DEFAULT NULL,
	PRIMARY KEY (`num_id`),
	UNIQUE KEY `record_id_UNIQUE` (`num_id`)
) ENGINE=InnoDB AUTO_INCREMENT=831 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `searchcontroller`.`asking_ig_realtime` (
	`num_id` int(25) unsigned NOT NULL AUTO_INCREMENT,
	`city_name` char(45) DEFAULT NULL,
	`latitude` double(9,6) DEFAULT NULL,
	`longitude` double(9,6) DEFAULT NULL,
	`radius` bigint(45) DEFAULT NULL,
	`in_what_lan` varchar(5) DEFAULT NULL,
	`required_by` varchar(25) DEFAULT NULL,
	`required_at` varchar(25) DEFAULT NULL,
	`message_from` varchar(45) DEFAULT NULL,
	`getting_status` int(5) DEFAULT NULL,
	PRIMARY KEY (`num_id`),
	UNIQUE KEY `record_id_UNIQUE` (`num_id`)
) ENGINE=InnoDB AUTO_INCREMENT=906 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `searchcontroller`.`placeidinfor` (
	`num_id` int(25) unsigned NOT NULL AUTO_INCREMENT,
	`placeid` varchar(50) DEFAULT NULL,
	`latitude` double(9,6) DEFAULT NULL,
	`longitude` double(9,6) DEFAULT NULL,
	PRIMARY KEY (`num_id`),
	UNIQUE KEY `record_id_UNIQUE` (`num_id`,`placeid`)
) ENGINE=InnoDB AUTO_INCREMENT=319 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `searchcontroller`.`queuing` (
	`num_id` int(25) unsigned NOT NULL AUTO_INCREMENT,
	`task_id` int(25) DEFAULT NULL,
	`name` varchar(100) DEFAULT NULL,
	`streetAddress` varchar(100) DEFAULT NULL,
	`country` varchar(100) DEFAULT NULL,
	`placeType` varchar(100) DEFAULT NULL,
	`fullName` varchar(100) DEFAULT NULL,
	`boundingBoxType` varchar(100) DEFAULT NULL,
	`boundingBoxCoordinatesLatitude` double(9,6) DEFAULT NULL,
	`boundingBoxCoordinatesLongitude` double(9,6) DEFAULT NULL,
	`place_id` varchar(100) DEFAULT NULL,
	`start_date` varchar(25) DEFAULT NULL,
	`end_date` varchar(25) DEFAULT NULL,
	`in_what_lan` varchar(5) DEFAULT NULL,
	`message_from` varchar(45) DEFAULT NULL,
	`status` int(5) DEFAULT NULL,
	PRIMARY KEY (`num_id`),
	UNIQUE KEY `place_id` (`place_id`,`start_date`,`end_date`,`in_what_lan`)
) ENGINE=InnoDB AUTO_INCREMENT=15272 DEFAULT CHARSET=utf8mb4;

INSERT INTO `searchcontroller`.`asking` VALUES (0,'toronto,ON',43.708215,-79.354983,5,'2016-04-01','2016-04-02','en','user','user','Instagram',0),(1,'mississauga,ON',43.625708,-79.665695,15,'2016-04-01','2016-04-01','en','user','user','Twitter',0),(2,'vancouver,BC',49.255216,-123.168950,15,'2016-04-01','2016-04-03','en','user','user','Twitter',0);

INSERT INTO `searchcontroller`.`asking_ig_realtime` VALUES (1,'toronto',43.781213,-79.208885,5000,'en','user','user','instagram',0),(2,'toronto',43.781213,-79.361103,5000,'en','user','user','instagram',0),(3,'toronto',43.671213,-79.208885,5000,'en','user','user','instagram',0),(4,'toronto',43.671213,-79.361103,5000,'en','user','user','instagram',0),(5,'toronto',43.736052,-79.361587,5000,'en','user','user','instagram',0),(6,'toronto',43.736052,-79.488358,5000,'en','user','user','instagram',0),(7,'toronto',43.644385,-79.361587,5000,'en','user','user','instagram',0),(8,'toronto',43.644385,-79.488358,5000,'en','user','user','instagram',0);