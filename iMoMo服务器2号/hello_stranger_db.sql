/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50133
Source Host           : localhost:3306
Source Database       : hello_stranger_db

Target Server Type    : MYSQL
Target Server Version : 50133
File Encoding         : 65001

Date: 2015-06-12 16:43:29
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `allocation_id`
-- ----------------------------
DROP TABLE IF EXISTS `allocation_id`;
CREATE TABLE `allocation_id` (
  `allocate_id` int(8) NOT NULL,
  `flag` tinyint(2) NOT NULL,
  PRIMARY KEY (`allocate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

-- ----------------------------
-- Records of allocation_id
-- ----------------------------
INSERT INTO `allocation_id` VALUES ('9090', '1');
INSERT INTO `allocation_id` VALUES ('9091', '1');
INSERT INTO `allocation_id` VALUES ('9092', '1');
INSERT INTO `allocation_id` VALUES ('9093', '1');
INSERT INTO `allocation_id` VALUES ('9094', '1');
INSERT INTO `allocation_id` VALUES ('9095', '1');
INSERT INTO `allocation_id` VALUES ('9096', '1');
INSERT INTO `allocation_id` VALUES ('9097', '0');

-- ----------------------------
-- Table structure for `friend_list`
-- ----------------------------
DROP TABLE IF EXISTS `friend_list`;
CREATE TABLE `friend_list` (
  `userId` varchar(15) NOT NULL,
  `friendList` text,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

-- ----------------------------
-- Records of friend_list
-- ----------------------------
INSERT INTO `friend_list` VALUES ('9091', '9095,9096');
INSERT INTO `friend_list` VALUES ('9093', '9091');
INSERT INTO `friend_list` VALUES ('9096', '9091');
INSERT INTO `friend_list` VALUES ('9099', '9091,9098');

-- ----------------------------
-- Table structure for `imomo_clients`
-- ----------------------------
DROP TABLE IF EXISTS `imomo_clients`;
CREATE TABLE `imomo_clients` (
  `userId` varchar(15) NOT NULL,
  `userEmail` varchar(20) NOT NULL,
  `userName` varchar(20) NOT NULL,
  `userPasswd` text NOT NULL,
  `userHeadPath` text NOT NULL,
  `userSex` char(6) NOT NULL,
  `userBirthday` char(10) NOT NULL,
  `personSignature` text,
  `vitalityValue` int(4) DEFAULT NULL,
  PRIMARY KEY (`userEmail`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

-- ----------------------------
-- Records of imomo_clients
-- ----------------------------
INSERT INTO `imomo_clients` VALUES ('9093', '1005345@163.com', 'test1', 'e1eba9bbbb04da14e0f0b1cec9eea708', 'E:\\iMoMoServer\\ClientsHead\\9093.png', '女', '2015.4.25', '乐乐', '15');
INSERT INTO `imomo_clients` VALUES ('9095', '100596@163.com', '都灵', '4875b3210ffa9463be42c307d7e97c07', 'E:\\iMoMoServer\\ClientsHead\\9095.png', '男', '2013-4-24', '你好，我叫陈', '999965');
INSERT INTO `imomo_clients` VALUES ('9094', '10323645@163.com', 'test', '4875b3210ffa9463be42c307d7e97c07', 'E:\\iMoMoServer\\ClientsHead\\9094.png', '女', '2015.4.25', '', '0');
INSERT INTO `imomo_clients` VALUES ('9091', '1101587382@qq.com', '皇子', 'f460cf88343668720ed4191fadeae605', 'E:\\iMoMoServer\\ClientsHead\\9091.png', '男', '2015-5-18', 'day day up', '10000290');
INSERT INTO `imomo_clients` VALUES ('9090', '15764230067@163.com', 'isRunning', 'f460cf88343668720ed4191fadeae605', 'E:\\iMoMoServer\\ClientsHead\\9090.png', 'male', '2015-5-18', 'good boy', '1000060');
INSERT INTO `imomo_clients` VALUES ('9096', '18355313686@163.com', '菲菲', 'f460cf88343668720ed4191fadeae605', 'E:\\iMoMoServer\\ClientsHead\\9096.png', '女', '1992.10.7', '', '0');
INSERT INTO `imomo_clients` VALUES ('9092', '9999@qq.com', '无双剑姬', 'f460cf88343668720ed4191fadeae605', 'E:\\iMoMoServer\\ClientsHead\\9091.png', '女', '2015-5-18', null, '15');
