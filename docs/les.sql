/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50621
 Source Host           : localhost
 Source Database       : les

 Target Server Type    : MySQL
 Target Server Version : 50621
 File Encoding         : utf-8

 Date: 10/13/2014 18:02:11 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `userbase`
-- ----------------------------
DROP TABLE IF EXISTS `userbase`;
CREATE TABLE `userbase` (
  `USER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `PWD` varchar(255) DEFAULT NULL,
  `PHONE_NUM` varchar(255) NOT NULL,
  `REG_TIME` datetime DEFAULT NULL,
  `LAST_LOGIN_TIME` datetime DEFAULT NULL COMMENT '最后登录时间',
  `LNG` varchar(255) DEFAULT NULL,
  `LAT` varchar(255) DEFAULT NULL,
  `DEVICE_TOKEN` varchar(255) DEFAULT NULL,
  `SYSTEM_TYPE` varchar(255) DEFAULT NULL,
  `OS_VERSION` varchar(255) DEFAULT NULL,
  `PHONE_MODEL` varchar(255) DEFAULT NULL,
  `PKEY` varchar(255) DEFAULT NULL COMMENT '用户登陆token',
  `NICKNAME` varchar(255) DEFAULT NULL COMMENT '用户昵称',
  `AVATAR` varchar(255) DEFAULT NULL COMMENT '头像url',
  `AGE` varchar(255) DEFAULT NULL COMMENT '年龄',
  `HORO_SCOPE` varchar(255) DEFAULT NULL COMMENT '星座',
  `HEIGHT` varchar(255) DEFAULT NULL COMMENT '身高',
  `WEIGHT` varchar(255) DEFAULT NULL COMMENT '体重',
  `ROLENAME` varchar(1) DEFAULT NULL COMMENT '角色',
  `AFFECTION` varchar(1) DEFAULT NULL COMMENT '感情状态',
  `PURPOSE` varchar(1) DEFAULT NULL COMMENT '交友目的',
  `ETHNICITY` varchar(1) DEFAULT NULL COMMENT '种族',
  `OCCUPATION` varchar(255) DEFAULT NULL COMMENT '职业',
  `LIVECITY` varchar(255) DEFAULT NULL COMMENT '居住地',
  `TRAVELCITY` varchar(255) DEFAULT NULL COMMENT '旅行地',
  `LOCATION` varchar(255) DEFAULT NULL COMMENT '活动范围',
  `MOVIE` varchar(255) DEFAULT NULL COMMENT '影视剧',
  `MUSIC` varchar(255) DEFAULT NULL COMMENT '音乐',
  `BOOKS` varchar(255) DEFAULT NULL COMMENT '书籍',
  `FOOD` varchar(255) DEFAULT NULL COMMENT '食物',
  `OTHERS` varchar(255) DEFAULT NULL COMMENT '其他爱好',
  `INTRO` varchar(255) DEFAULT NULL COMMENT '自我介绍',
  `MESSAGE_PWD` varchar(255) DEFAULT NULL COMMENT '消息系统的密码',
  `MESSAGE_USER` varchar(255) DEFAULT NULL COMMENT '消息系统账号',
  PRIMARY KEY (`USER_ID`,`PHONE_NUM`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `userbase`
-- ----------------------------
BEGIN;
INSERT INTO `userbase` VALUES ('1', 'e10adc3949ba59abbe56e057f20f883e', 'ywang', '2014-10-13 17:52:53', null, '117.157954', '31.873432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', '', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
