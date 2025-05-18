/*
 Navicat Premium Dump SQL

 Source Server         : i道i_记录
 Source Server Type    : MySQL
 Source Server Version : 90100 (9.1.0)
 Source Host           : localhost:3306
 Source Schema         : i道i_记录

 Target Server Type    : MySQL
 Target Server Version : 90100 (9.1.0)
 File Encoding         : 65001

 Date: 18/05/2025 17:04:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for 令牌表
-- ----------------------------
DROP TABLE IF EXISTS `令牌表`;
CREATE TABLE `令牌表`  (
  `令牌值` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '令牌具体数据',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '令牌是哪个ip的，ip变化了令牌失效',
  `账号` int NULL DEFAULT NULL COMMENT '令牌属于哪个账号，为-1表明为注册账号的令牌',
  `失效时间` timestamp NULL DEFAULT NULL COMMENT '令牌失效的时间',
  PRIMARY KEY (`令牌值`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 所有用户表
-- ----------------------------
DROP TABLE IF EXISTS `所有用户表`;
CREATE TABLE `所有用户表`  (
  `账号` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '账号，自增int',
  `用户名` tinyblob NOT NULL COMMENT '用户的用户名，tinyblob最大255字节，单机不需要这东西，废弃，以后存别的。',
  `密码` tinyblob NOT NULL COMMENT '哈希后的密码，tinyblob最大255字节',
  `其他值` blob NULL COMMENT '假如获取了微信qq授权，可以使用微信或者qq登录。不过我没买域名，没打算上qq或者微信开发者',
  `密码盐值` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码的随机盐值，打算用uid。',
  PRIMARY KEY (`账号`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 文件表
-- ----------------------------
DROP TABLE IF EXISTS `文件表`;
CREATE TABLE `文件表`  (
  `文件id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `文件名` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户设置的文件名',
  `文件类型` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '其他' COMMENT '文件的类型',
  `文件内容` longblob NOT NULL COMMENT '文件本体,最大可存约4G，经过性能和使用考虑，文件废弃加密功能。',
  `账号` int UNSIGNED NOT NULL COMMENT '标记归属用户',
  `所属随笔` int UNSIGNED NULL DEFAULT NULL COMMENT '标记属于哪篇随笔',
  PRIMARY KEY (`文件id`) USING BTREE,
  INDEX `账号`(`账号` ASC) USING BTREE,
  INDEX `所属随笔`(`所属随笔` ASC) USING BTREE,
  CONSTRAINT `文件表_ibfk_1` FOREIGN KEY (`账号`) REFERENCES `所有用户表` (`账号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `文件表_ibfk_2` FOREIGN KEY (`所属随笔`) REFERENCES `随笔索引表` (`随笔号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 标签表
-- ----------------------------
DROP TABLE IF EXISTS `标签表`;
CREATE TABLE `标签表`  (
  `标签号` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签号，主键',
  `账号` int UNSIGNED NULL DEFAULT NULL COMMENT '外键',
  `标签名` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签的名字',
  PRIMARY KEY (`标签号`) USING BTREE,
  INDEX `标签表_ibfk_1`(`账号` ASC) USING BTREE,
  INDEX `标签名`(`标签名` ASC) USING BTREE,
  INDEX `标签号`(`标签号` ASC, `账号` ASC) USING BTREE,
  CONSTRAINT `标签表_ibfk_1` FOREIGN KEY (`账号`) REFERENCES `所有用户表` (`账号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 用户信息表
-- ----------------------------
DROP TABLE IF EXISTS `用户信息表`;
CREATE TABLE `用户信息表`  (
  `账号` int UNSIGNED NOT NULL COMMENT '用户的id',
  `标签` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户自定义的标签组,现在新开了标签表存储标签，该项现在为预备役',
  `附加值1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '扩展用1，这里用来存用户的默认AES密钥',
  `附加值2` blob NULL COMMENT '扩展用2',
  `头像` blob NULL COMMENT '头像最大16MB，注意。',
  `字数` int NULL DEFAULT NULL COMMENT '用户写过的总字数,如果用户输入了8GB的纯文本有可能会爆。',
  `篇数` int NULL DEFAULT NULL COMMENT '用户写过的总篇数',
  `次数` int NULL DEFAULT NULL COMMENT '用户登录的总次数',
  `时间` int NULL DEFAULT NULL COMMENT '用户使用的分钟数',
  PRIMARY KEY (`账号`) USING BTREE,
  CONSTRAINT `用户信息表_ibfk_1` FOREIGN KEY (`账号`) REFERENCES `所有用户表` (`账号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 登录表
-- ----------------------------
DROP TABLE IF EXISTS `登录表`;
CREATE TABLE `登录表`  (
  `账号` int UNSIGNED NOT NULL COMMENT '用户账号',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录的ip',
  `登录时间` timestamp NOT NULL COMMENT '登录开始的时间',
  `退出时间` timestamp NULL DEFAULT NULL COMMENT '退出的时间（打算心跳检测判断退出，跳的慢点）',
  `附加值` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '其他附加值，现在用来存客户端公钥',
  `保持登录密钥` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登陆后，每次受到信息都会和用户比对这个密钥。这个是用客户端公钥加密后的值',
  `实际保持登录密钥` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '这个是未加密的密钥，是此次访问的AES加密密钥。',
  `保持链接` int NOT NULL COMMENT '每3min服务器就会将链接标记即为未链接0，客户端每分钟发心跳检测设置为链接1。如果服务器设置未链接0时，这个值本来就是未链接0，记录退出时间。',
  PRIMARY KEY (`账号`) USING BTREE,
  CONSTRAINT `登录表_ibfk_1` FOREIGN KEY (`账号`) REFERENCES `所有用户表` (`账号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 随笔内容表
-- ----------------------------
DROP TABLE IF EXISTS `随笔内容表`;
CREATE TABLE `随笔内容表`  (
  `随笔索引号` int UNSIGNED NOT NULL COMMENT '随笔属于哪一个索引',
  `随笔内容` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '日记具体内容,注意类型，最大存16MB，如果超了保存时会向附加值中记录延续号。',
  `附加值` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '日记附加内容，用于扩展',
  `账号` int UNSIGNED NULL DEFAULT NULL COMMENT '标记表属于哪个用户',
  PRIMARY KEY (`随笔索引号`) USING BTREE,
  INDEX `账号`(`账号` ASC) USING BTREE,
  CONSTRAINT `随笔内容表_ibfk_1` FOREIGN KEY (`随笔索引号`) REFERENCES `随笔索引表` (`随笔号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `随笔内容表_ibfk_2` FOREIGN KEY (`账号`) REFERENCES `所有用户表` (`账号`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for 随笔索引表
-- ----------------------------
DROP TABLE IF EXISTS `随笔索引表`;
CREATE TABLE `随笔索引表`  (
  `随笔号` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录唯一id标识',
  `随笔名` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '随笔名称',
  `第一次编辑时间` timestamp NOT NULL COMMENT '第一次编辑时间',
  `最后编辑时间` timestamp NOT NULL COMMENT '最后编辑时间',
  `标签` int UNSIGNED NULL DEFAULT NULL COMMENT '源于标签表',
  `加密` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '为空表示默认加密，不为空则是用户设置的密码加密的数据。解密出来了和加密内容相同说明用户输入密码对了',
  `加密内容` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加密的内容',
  `附加值` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '附加内容，用于扩展，比如加个颜色，加点功能',
  `账号` int UNSIGNED NULL DEFAULT NULL COMMENT '账号id',
  PRIMARY KEY (`随笔号`) USING BTREE,
  INDEX `账号`(`账号` ASC) USING BTREE,
  INDEX `随笔索引表_ibfk_3`(`标签` ASC) USING BTREE,
  CONSTRAINT `随笔索引表_ibfk_1` FOREIGN KEY (`账号`) REFERENCES `所有用户表` (`账号`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `随笔索引表_ibfk_3` FOREIGN KEY (`标签`) REFERENCES `标签表` (`标签号`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
