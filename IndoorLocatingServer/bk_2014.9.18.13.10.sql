# Host: localhost  (Version: 5.6.11)
# Date: 2014-09-18 13:10:08
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
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8;

#
# Data for table "location"
#

INSERT INTO `location` VALUES (44,'112_2_3');

#
# Structure for table "wifi"
#

DROP TABLE IF EXISTS `wifi`;
CREATE TABLE `wifi` (
  `wid` int(11) NOT NULL AUTO_INCREMENT,
  `bssid` varchar(255) DEFAULT '',
  `freq` int(11) DEFAULT NULL,
  PRIMARY KEY (`wid`)
) ENGINE=InnoDB AUTO_INCREMENT=610 DEFAULT CHARSET=utf8;

#
# Data for table "wifi"
#

INSERT INTO `wifi` VALUES (594,'2c:d0:5a:32:89:f2',2412),(595,'1c:7e:e5:57:22:e7',2462),(596,'44:e4:d9:85:22:05',2412),(597,'1c:7e:e5:56:b9:a2',2462),(598,'44:e4:d9:85:22:00',2412),(599,'44:e4:d9:85:1c:e5',2437),(600,'44:e4:d9:85:1c:e0',2437),(601,'04:c5:a4:08:f2:10',2437),(602,'04:c5:a4:08:f2:15',2437),(603,'1c:7e:e5:57:1e:67',2462),(604,'00:09:5b:d1:f8:c9',2462),(605,'44:e4:d9:3f:e7:d5',2462),(606,'00:87:46:18:04:95',2462),(607,'44:e4:d9:3f:e7:d0',2462),(608,'44:e4:d9:85:20:e0',2412),(609,'44:e4:d9:85:23:00',2412);

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
) ENGINE=InnoDB AUTO_INCREMENT=5595 DEFAULT CHARSET=utf8;

#
# Data for table "sample"
#

INSERT INTO `sample` VALUES (5579,44,594,-32,1411013810124),(5580,44,595,-49,1411013810234),(5581,44,596,-54,1411013810344),(5582,44,597,-64,1411013810474),(5583,44,598,-54,1411013810564),(5584,44,599,-68,1411013810654),(5585,44,600,-68,1411013810764),(5586,44,601,-86,1411013810854),(5587,44,602,-85,1411013810924),(5588,44,604,-89,1411013811014),(5589,44,605,-89,1411013811174),(5590,44,606,-93,1411013811466),(5591,44,603,-93,1411013811575),(5592,44,607,-92,1411013811731),(5593,44,608,-93,1411013811903),(5594,44,609,-91,1411013812059);
