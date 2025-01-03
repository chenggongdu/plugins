### 插件名称 / Plugin Name
### 优路 DCG - MyBatis 测试文件生成插件
### YL DCG - MyBatis Test File Generator Plugin

#### 简介 / Introduction
##### 中文
本插件由 优路 DCG 编写，旨在显著提升 MyBatis 项目的测试开发效率。它为 IntelliJ IDEA 用户提供了一键生成测试文件的功能，是开发工具中的高效利器。

##### 英文
This plugin, developed by Youlu DCG, is designed to significantly enhance the testing development efficiency of MyBatis projects. It empowers IntelliJ IDEA users with one-click test file generation, making it an indispensable tool in your development arsenal.

#### 功能亮点 / Features
##### 中文

自动解析 MyBatis 的 Mapper XML 配置文件。
自动生成测试配置文件（位于 test/resources/mybatis 目录）。
自动生成 Java 测试类（位于 test/java/mapper 目录）。
简化测试用例编写流程，助力快速验证业务逻辑，提高团队开发效率。
##### 英文

Automatically parses MyBatis Mapper XML configuration files.
Generates test configuration files in the test/resources/mybatis directory.
Generates Java test classes in the test/java/mapper directory.
Simplifies the test case writing process, enabling quick business logic validation and enhancing team efficiency.


#### 必备条件 / Prerequisites
##### 中文

在 src/test/resources 目录中添加 database.config.properties 文件，并填写您的数据库配置信息（格式如下）：
```properties
database.url = jdbc:mysql://localhost:3306/test
database.username = root
database.password = 123456
```
##### 英文

Add a database.config.properties file to the src/test/resources directory with your database configuration details as follows:
```properties
database.url = jdbc:mysql://localhost:3306/test
database.username = root
database.password = 123456
```

#### 目标 / Purpose
##### 中文
本插件专为开发者设计，旨在简化测试用例编写流程，支持快速验证业务逻辑，从而提高团队整体开发效率。通过支持 MyBatis Mapper XML 文件的一键测试文件生成功能，让测试开发更高效、更智能。

##### 英文
This plugin is designed for developers, simplifying the process of writing test cases and enabling quick validation of business logic. By supporting one-click test file generation for MyBatis Mapper XML files, it makes test development more efficient and intelligent.

#### 使用方法 / How to Use
##### 中文

1. 定位到 MyBatis 的 Mapper XML 文件。
2. 右键点击文件。
3. 选择 “YL Gen MyBatis Test File”。
##### 英文

1. Navigate to the MyBatis Mapper XML file.
2. Right-click on the file.
3. Select "YL Gen MyBatis Test File".