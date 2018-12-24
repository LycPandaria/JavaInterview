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
    - [objectFactory 对象工厂](#objectfactory-对象工厂)
    - [environments 环境配置](#environments-环境配置)
    - [Mapper 映射器](#mapper-映射器)
  - [深入 Mapper XML 映射文件](#深入-mapper-xml-映射文件)
    - [select](#select)

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
无论是 Mybatis 在预处理语句中设置一个参数，还是从结果集中取出一个值，都会用类型处理器将获取的值以合适的方式转换成 Java 类型。

[Mybatis 默认的类型处理器详解](#https://blog.csdn.net/orzxxx01/article/details/50889532)

### objectFactory 对象工厂
Mybatis 每次创建结果对象的新实例时，它都会使用一个对象工厂实例来完成。默认的对象工厂需要做的仅仅是实例化目标类，要么通过默认构造方法，要么在参数映射存在的时候通过参数构造方法来实例化。如果想覆盖对象工厂的默认行为，则可以通过创建自己的对象工厂来实现。

[MyBatis之ObjectFactory](https://www.cnblogs.com/yulinfeng/p/5995200.html)

### environments 环境配置
Mybatis 的环境配置其实就是数据源的配置。Mybatis 可以配置多种环境，这种机制使得 Mybatis 可以将 SQL 映射应用于多种数据库中。

**注意：可以配置多个环境，每个 SqlSessionFactory 实例只能选择其一**。如果想连接两个数据库，就需要创建两个SQlSessionFactory实例，每个数据库对应一个。而如果是三个数据库，就需要三个实例，以此类推。

环境配置实例如下：
```xml

<environments default="development">
   <environment id="development">
       <transactionManager type="JDBC" />
       <!-- 配置数据库连接信息 -->
       <dataSource type="POOLED">
           <!-- value属性值引用db.properties配置文件中配置的值 -->
           <property name="driver" value="${driver}" />
           <property name="url" value="${url}" />
           <property name="username" value="${name}" />
           <property name="password" value="${password}" />
       </dataSource>
   </environment>
</environments>
```

- 默认的环境ID（比如：default:"development"）development : 开发模式    work : 工作模式
- 每个 environment 元素定义的环境 ID（比如:id=”development”）。
- 事务管理器的配置（比如:type=”JDBC”）
- 数据源的配置（比如:type=”POOLED”）。

**事务管理器**

事务管理器有两种：type="[ JDBC | MANAGED ]":

JDBC:这个配置就是直接使用了JDBC 的提交和回滚设置，它依赖于从数据源得到的连接来管理事务范围。

MANAGED ：这个配置从来都不提交和回滚一个连接，而是让容器来管理事务的整个生命周期（比如JEE应用服务的上下文）。默认情况下他会关闭连接，然而一些容器并不希望这样，因此需要将closeConnection属性设置为false来阻止它默认的关闭行为。

**如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器， 因为 Spring 模块会使用自带的管理器来覆盖前面的配置。**

**dataSource--数据源**

dataSource元素使用标准的JDBC数据源接口来配置JDBC连接对象的资源。

三种内建的数据源类型：type=[ UNPOOLED | POOLED | JNDI ]
- UNPOOLED - 这个数据源的实现只是每次请求时打开和关闭连接。虽然一点慢，他对在及时可用连接方面没有性能要求的简单应用程序是一个很好的选择，不同的数据库在这方面表现也是不一样的，所以对某些数据库来说使用连接池并不重要，这个配置也是理想的。UNPOOLED类型的数据源仅仅需要配置一下5种属性：

  - driver：JDBC驱动的java类的完全限定名
  - url：数据库的JDBC URL地址
  - userName： 登录数据库的用户名
  - password ： 登录数据库的密码
  - dedaultTransactionIsolationLevel– 默认的连接事务隔离级别。

作为可选项，你也可以传递属性给数据库驱动，要这样做，属性的前缀为“driver.”,例如：driver.encoding=UTF8.这将通过DriverManager.getConnection(url,driverProperties) 方法传递值为UTF8的encoding 属性给数据库驱动。

- POOLED - 这种数据源的实现利用“池”的概念将JDBC连接对象组织起来，避免了创建先的连接实例时所必须的初始化和认证时间。这是一种使得并发WEb应用快速响应请求的流行的处理方式。除了上述提到UNPOOLED下的属性外，还有以下属性来配置POOLED的数据源：
  - poolMaximumActiveConnections-在任意时间可以存在的活动（也就是正在使用）连接数量，默认值：10
  - poolMaximumIdleConnections - 任意时间可能存在的空闲连接数。
  - poolMaximumCheckoutTime - 再被强制返回之前，池中连接被检出时间，默认值2W毫秒  即20s
  - poolTimeToWait - 这是一个底层设置，如果获取连接花费的相当长的时间，它会给连接池打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直安静的失败），默认值：2W毫秒即 20 s
  - PoolPingQuery - 发送到数据库的侦测查询，用来检验连接是否处在正常工作秩序中并准备接受请求。默认是“NO PING QUERY SET”，这会导致多数数据库驱动失败时带有一个恰当的错误消息
  - PoolPingConnectionsNotUsedFor -配置 poolPingQuery 的使用频度。这可以被设置成匹配具体的数据库连接超时时间，来避免不必要的侦测，默认值：0（即所有连接每一时刻都被侦测 — 当然仅当 poolPingEnabled 为 true 时适用）。

### Mapper 映射器
Mapper 映射器告诉 Mybatis 去哪里找映射文件
```xml
<!-- 使用类路径查找资源 -->
<mappers>
  <mapper resource="org/.../UserMapper.xml"/>
</mappers>
<!-- 使用本地文件 -->
<mappers>
  <mapper url="file:///D:/UserMapper.xml"/>
</mappers>
<!-- 使用接口类 -->
<mappers>
  <mapper class="org.UserMapper"/>
</mappers>
<!-- 使用包名 -->
<mappers>
  <package name="org.mapper"/>
</mappers>
```

## 深入 Mapper XML 映射文件
SQL 映射文件常用的元素如下：
- select 映射查询语句
- insert 插入语句
- update 更新
- delete 删除
- sql 可被引用的可重复语句块
- cache 给定命名空间的缓存配置
- cache-ref 其他命名空间缓存配置的引用
- resultMap 描述如何从数据库结果集中加载对象

### select
