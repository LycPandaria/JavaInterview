<!-- TOC START min:1 max:3 link:true update:true -->
- [mybatis 入门](#mybatis-入门)
  - [初看](#初看)
  - [初看配置文件](#初看配置文件)
  - [初看代码](#初看代码)
- [Mybatis 基本用法](#mybatis-基本用法)
  - [Mybatis 体系架构](#mybatis-体系架构)
    - [SqlSessionFactory](#sqlsessionfactory)
    - [SqlSession](#sqlsession)
  - [深入 Mybatis 配置文件](#深入-mybatis-配置文件)
    - [MyBatis配置文件结构](#mybatis配置文件结构)
    - [properties 属性](#properties-属性)
    - [settings 设置](#settings-设置)
    - [typeAliases 类型别名](#typealiases-类型别名)
    - [typeHandlers 类型处理器](#typehandlers-类型处理器)

<!-- TOC END -->

# mybatis 入门
## 初看
```xml
<!-- namespace指用户自定义的命名空间。 -->
<mapper namespace="org.fkit.mapper.UserMapper">
<!--
	id="save"是唯一的标示符
	parameterType属性指明插入时使用的参数类型
	useGeneratedKeys="true"表示使用数据库的自动增长策略
 -->
  <insert id="save" parameterType="org.fkit.domain.User" useGeneratedKeys="true">
  	INSERT INTO TB_USER(name,sex,age)
  	VALUES(#{name},#{sex},#{age})
  </insert>
</mapper>
```
上面的xml定义了一条insert语句：
1. <mapper namespace="org.fkit.mapper.UserMapper"> 为这个 mapper 指定了一个唯一的 namespace，namespace 的值习惯上设置成包名+sql 映射文件名来保证唯一性。
2. 在 insert 标签中编写了插入 sql 预计，设置 insert 标签的 id 属性为 save，这个 id 属性必须是唯一的。
3. 使用 parameterType 属性指明插入时候使用的参数类型
4. 使用 useGeneratedKeys="true" 表示使用数据库自动增长策略，需要底层数据库支持。

## 初看配置文件
```xml
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <!--  XML 配置文件包含对 MyBatis 系统的核心设置 -->
<configuration>
	<!-- 指定 MyBatis 所用日志的具体实现 -->
	<settings>
		<setting name="logImpl" value="LOG4J"/>
	</settings>
	<environments default="mysql">
	<!-- 环境配置，即连接的数据库。 -->
    <environment id="mysql">
    <!--  指定事务管理类型，type="JDBC"指直接简单使用了JDBC的提交和回滚设置 -->
      <transactionManager type="JDBC"/>
      <!--  dataSource指数据源配置，POOLED是JDBC连接对象的数据源连接池的实现。 -->
      <dataSource type="POOLED">
        <property name="driver" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://127.0.0.1:3306/mybatis"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
      </dataSource>
    </environment>
  </environments>
  <!-- mappers告诉了MyBatis去哪里找持久化类的映射文件 -->
  <mappers>
  	<mapper resource="org/fkit/mapper/UserMapper.xml"/>
  </mappers>
</configuration>
```
1. mybatis 配置文件默认命名为 Mybatis-congif.xml，应用程序运行时需要先加载该文件
2. <environment> 子元素用来配置 mybatis 的环境，即连接的数据库
3. <transactionManager> 元素用来配置 mybatis 中的事务管理，JDBC 表示直接简单使用 JDBC 的提交和回滚设置。
4. <dataSource> 用来配置数据源，mybatis 并不推荐采用 DriverManager 来连接数据库而是推荐使用数据源来保证最好的性能。

## 初看代码
```java
public class MyBatisTest {
	public static void main(String[] args) throws Exception {
		// 读取mybatis-config.xml文件
		InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
		// 初始化mybatis，创建SqlSessionFactory类的实例
		SqlSessionFactory SqlSessionFactory = new SqlSessionFactoryBuilder()
				.build(inputStream);
		// 创建Session实例
		SqlSession session = SqlSessionFactory.openSession();
		// 创建User对象
		User user = new User("admin", "男", 26);
		// 插入数据
		session.insert("org.fkit.mapper.UserMapper.save", user);
		// 提交事务
		session.commit();
		// 关闭Session
		session.close();
	}
}
```
正如上面的代码所示，在执行 session.insert("org.fkit.mapper.UserMapper.save", user); 之前，需要获取 SqlSession 对象，PO 只有在 SqlSession 的管理下才能够完成数据库的访问。使用 Mybatis 持久化操作通常如下：
1. 开发持久化类 PO 和编写持久化 Mapper.xml文件
2. 获取 SqlSessionFactory
3. 获得 SqlSession
4. 用面向对象方式操作数据库
5. 关闭事务，关闭 SqlSession

# Mybatis 基本用法
## Mybatis 体系架构
### SqlSessionFactory
SqlSessionFactory 是单个数据库映射关系经过编译后的内存镜像。SqlSessionFactory 对象的实例可以通过 SqlSessionFactoryBuilder 来获得，而 SqlSessionFactoryBuilder 则可以从 XML 配置文件或者一个预先定制的 Configuration 的实例构建出 SqlSessionFactory 对象。每一个 Mybatis 应用都以一个 SessionFactory 的实例为核心，其实线程安全的，一旦被创建，在应用执行期间都存在。

SqlSessionFactory 常用方法：SqlSession openSession() 创建 SqlSession 对象

### SqlSession
SqlSession 是 MyBatis 执行持久化操作的对象，类似于 JDBC 中的 Connection. 它是应用程序与持久存储层之间进行交互操作的一个单线程对象，包含以数据库为背景的所有执行 SQL 操作的方法，底层封装了 JDBC 连接。每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不能共享，也是线程不安全的，绝对不能将 SqlSession 的实例的引用放在一个静态字段甚至实例字段中。

[SqlSession 常见方法](http://www.cnblogs.com/linked5233/articles/4264391.html)
或见书本[Spring + MyBatis 企业应用实践](https://book.douban.com/subject/30197337/) 145页

## 深入 Mybatis 配置文件
```java
// 读取mybatis-config.xml文件
InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
// 初始化mybatis，创建SqlSessionFactory类的实例
SqlSessionFactory SqlSessionFactory = new SqlSessionFactoryBuilder()
    .build(inputStream);
```
结合上述代码，MyBatis 的初始化主要经过了以下几步：
1. 调用 SqlSessionFactoryBuilder 对象的 build(inputStream) 方法
2. SqlSessionFactoryBuilder 会根据输入流 inputStream 等信息创建 XMLConfigBuilder 对象。
3. SqlSessionFactoryBuilder 会调用 XMLConfigBuilder 的 parse() 方法
4. XMLConfigBuilder 对象解析 XML 配置文件并返回 Configuration 对象
5. SqlSessionFactoryBuilder 根据 Configuration 对象创建一个 DefaultSessionFactory 对象
6. SqlSessionFactory 返回 DefaultSessionFactory 给客户端使用。

### MyBatis配置文件结构
顶层 configuration 配置
- [properties 属性](#properties-属性)
- [settings 设置](#settings-设置)
- [typeAliases 类型别名](#[typeAliases-类型别名])
- typeHandlers 类型处理器
- objectFactory 对象工厂
- plugins 创建
- environments 环境
  - environment 环境变量
  - transactionManager 事务管理器
  - dataSource 数据源
- databaseIdProvider 数据库厂商标识
- mapper 映射器

### properties 属性
这些属性都是可外部配置且动态替换的。例如添加一个 db.properties
```
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://127.0.0.1/xx
username=xx
password=xx
```
在配置文件中配置 properties 属性
```xml
<properties resource="db.properties"/>
```
在配置文件中就可以使用：
```xml
<dataSource type="POOLED">
<property name="driver" value="${driver}"/>
<property name="url" value="${url}"/>
</dataSource>
```

### settings 设置

[settings 全部参数](#https://blog.csdn.net/fageweiketang/article/details/80767532)

一个配置完整的 settings 元素实例如下：
```xml

<settings>
    <setting name="cacheEnabled" value="true"/>
    <setting name="lazyLoadingEnabled" value="true"/>
    <setting name="multipleResultSetsEnabled" value="true"/>
    <setting name="useColumnLabel" value="true"/>
    <setting name="useGeneratedKeys" value="false"/>
    <setting name="autoMappingBehavior" value="PARTIAL"/>
    <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
    <setting name="defaultExecutorType" value="SIMPLE"/>
    <setting name="defaultStatementTimeout" value="30"/>
    <setting name="defaultFetchSize" value="200"/>
    <setting name="safeRowBoundsEnabled" value="false"/>
    <setting name="mapUnderscoreToCamelCase" value="false"/>
    <setting name="localCacheScope" value="SESSION"/>
    <setting name="jdbcTypeForNull" value="OTHER"/>
    <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
</settings>
```

### typeAliases 类型别名
类型别名是为 Java 类型设置一个短的名称，它只和 XML 配置有关，存在的意义仅在于用来减少类完全限定名的冗余
```xml
<typeAliases>
		<typeAlias alias="Page" type="com.thinkgem.jeesite.common.persistence.Page" /><!--分页  -->
	</typeAliases>
```

### typeHandlers 类型处理器
