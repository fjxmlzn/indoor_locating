# Host: localhost  (Version: 5.6.11)
# Date: 2014-09-14 19:33:49
# Generator: MySQL-Front 5.3  (Build 4.155)

/*!40101 SET NAMES utf8 */;

#
# Structure for table "wifi_info"
#

DROP TABLE IF EXISTS `wifi_info`;
CREATE TABLE `wifi_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `label` text,
  `vector` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

#
# Data for table "wifi_info"
#

INSERT INTO `wifi_info` VALUES (1,'test','{\"data\":{[\"BSSID\":\"AA-AA\",\"level\":-50]}}'),(2,'classroom001','[{\"BSSID\":\"AA\",\"level\":-50}]');
