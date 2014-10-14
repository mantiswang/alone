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

 Date: 10/14/2014 17:55:48 PM
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
  `REG_TIME` bigint(20) DEFAULT NULL,
  `LAST_LOGIN_TIME` bigint(20) DEFAULT NULL COMMENT '最后登录时间',
  `ONLINE` int(11) DEFAULT NULL COMMENT '在线',
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `userbase`
-- ----------------------------
BEGIN;
INSERT INTO `userbase` VALUES ('1', 'e10adc3949ba59abbe56e057f20f883e', '15256551134', '1413275397171', '1413275483231', '1', '117.157954', '31.873432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', '51a7f79b7528da0cf2fc92e0750148da', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('2', 'e10adc3949ba59abbe56e057f20f883e', '18018956659', '1413275397222', null, null, '117.557954', '31.103432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 4s', 'e7001949fed4fd028878a6dc518a2ed2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('3', 'e10adc3949ba59abbe56e057f20f883e', '18018256907', '1413275397225', null, null, '117.668041', '32.068562', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '7.0.2', 'iPhone 4', '0156bb1a2a27d922f29103594c0352be', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('4', 'e10adc3949ba59abbe56e057f20f883e', '13856796230', '1413275397230', null, null, '116.812721', '33.303466', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', '642cdc1eef7dbfc1bbe89667359b123b', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('5', 'e10adc3949ba59abbe56e057f20f883e', '15256554567', '1413275397236', null, null, '117.454687', '33.816950', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', '7f3055abb6fb438f4ba2ed21185b32a1', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('6', 'e10adc3949ba59abbe56e057f20f883e', '15967565765', '1413275397240', null, null, '117.000954', '31.173432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', 'fc8a270595a7b60a9be1300fa6a9d1b4', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('7', 'e10adc3949ba59abbe56e057f20f883e', '15973485765', '1413275397243', null, null, '117.000954', '31.173432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', '669da17e0e96dfa54331fd70e947c5f3', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('8', 'e10adc3949ba59abbe56e057f20f883e', '15945678798', '1413275397280', null, null, '117.000954', '31.173432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', 'f77e1d524692e4d46aaad4c2a51a8524', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('9', 'e10adc3949ba59abbe56e057f20f883e', '18783675836', '1413275397284', null, null, '117.123954', '31.173432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', 'f82e25c68d809bb156438d75781b048e', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('10', 'e10adc3949ba59abbe56e057f20f883e', '18753764584', '1413275397286', null, null, '117.102954', '31.173432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', 'e11dc3efe59847bc7763c0983ecbc8b2', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('11', 'e10adc3949ba59abbe56e057f20f883e', '18746146342', '1413275397289', null, null, '117.030954', '31.173432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', '6e9708962433d1dc0cd0ef8b499cd83a', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('12', 'e10adc3949ba59abbe56e057f20f883e', '18957265846', '1413275397292', null, null, '117.085954', '31.173432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', 'b63491a69ce1c298100e632a74615d7f', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null), ('13', 'e10adc3949ba59abbe56e057f20f883e', '18076236598', '1413275397295', null, null, '117.573954', '31.173432', '8a2597aa1d37d432a88a446d82b6561e', 'iOS', '8.0', 'iPhone 5s', '9716995626c116440908bdadd52497c1', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
