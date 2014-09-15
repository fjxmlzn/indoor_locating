# Host: localhost  (Version: 5.6.11)
# Date: 2014-09-16 00:27:05
# Generator: MySQL-Front 5.3  (Build 4.155)

/*!40101 SET NAMES utf8 */;

#
# Structure for table "location"
#

DROP TABLE IF EXISTS `location`;
CREATE TABLE `location` (
  `lid` int(11) NOT NULL AUTO_INCREMENT,
  `label` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`lid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

#
# Data for table "location"
#

INSERT INTO `location` VALUES (7,'classroom1'),(8,'classroom2');

#
# Structure for table "wifi"
#

DROP TABLE IF EXISTS `wifi`;
CREATE TABLE `wifi` (
  `wid` int(11) NOT NULL AUTO_INCREMENT,
  `bssid` varchar(255) DEFAULT '',
  PRIMARY KEY (`wid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

#
# Data for table "wifi"
#

INSERT INTO `wifi` VALUES (4,'AA-AA'),(5,'BB_BB');

#
# Structure for table "sample"
#

DROP TABLE IF EXISTS `sample`;
CREATE TABLE `sample` (
  `sid` int(11) NOT NULL AUTO_INCREMENT,
  `lid` int(11) NOT NULL,
  `wid` int(11) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`sid`),
  KEY `lid` (`lid`),
  KEY `wid` (`wid`),
  CONSTRAINT `sample_ibfk_1` FOREIGN KEY (`lid`) REFERENCES `location` (`lid`),
  CONSTRAINT `sample_ibfk_2` FOREIGN KEY (`wid`) REFERENCES `wifi` (`wid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "sample"
#

INSERT INTO `sample` VALUES (5,7,4,-50,1410798140679),(6,7,5,-60,1410798141147),(7,8,4,-50,1410798141147),(8,8,5,-70,1410798141147);
