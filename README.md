## 前后端分离项目模板

    包含基本的登录、注册、密码重置等功能，可于此基础进行二次开发应用程序。

* 登录功能（支持用户名，邮箱登录）
* 注册用户（通过邮箱注册）
* 重置密码（通过邮箱重置密码）


### application.yaml文件配置
    需要填写mysql数据库地址
    邮件服务器配置
    redis服务器配置
````yaml
    spring:
    datasource:
      url: jdbc:mysql://localhost:3306/project?useUnicode=true&characterEncoding=utf-8
      username: root
      password: 20011118
      driver-class-name: com.mysql.cj.jdbc.Driver
    mail:
      host: smtp.163.com
      password: WPEUJPCKOTXJHWKM
      username: redis_validate_007@163.com
      port: 465
      properties:
        from: redis_validate_007@163.com
        mail:
          smtp:
            socketFactory:
              class: javax.net.ssl.SSLSocketFactory
    data:
      redis:
        host: 192.168.75.128
        password: 20011118
        port: 6379
        client-type: lettuce

    server:
      port: 45372
````


### mysql脚本
````mysql
    SET NAMES utf8mb4;
    SET FOREIGN_KEY_CHECKS = 0;
    
    -- ----------------------------
    -- Table structure for db_account
    -- ----------------------------
    DROP TABLE IF EXISTS `db_account`;
    CREATE TABLE `db_account`  (
      `id` int NOT NULL AUTO_INCREMENT,
      `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
      `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
      `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
      PRIMARY KEY (`id`) USING BTREE,
      UNIQUE INDEX `unique_name`(`username` ASC) USING BTREE,
      UNIQUE INDEX `unique_email`(`email` ASC) USING BTREE
    ) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;
    
    -- ----------------------------
    -- Records of db_account
    -- ----------------------------
    INSERT INTO `db_account` VALUES (1, NULL, 'admin', '$2a$10$VEltwCR8xiasT5zYjg/bi.lKEt.clCN7tJB1EEK2bMrAcz6.TpN6a');
    
    -- ----------------------------
    -- Table structure for persistent_logins
    -- ----------------------------
    DROP TABLE IF EXISTS `persistent_logins`;
    CREATE TABLE `persistent_logins`  (
      `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      `series` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      `token` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      `last_used` timestamp NOT NULL,
      PRIMARY KEY (`series`) USING BTREE
    ) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;
    
    -- ----------------------------
    -- Records of persistent_logins
    -- ----------------------------
    
    SET FOREIGN_KEY_CHECKS = 1;
````



  