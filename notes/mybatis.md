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
    - [insert, update 和 delete](#insert-update-和-delete)
    - [sql](#sql)
    - [ResultMap](#resultmap)
- [MyBatis 的关系映射和动态 SQL](#mybatis-的关系映射和动态-sql)
  - [MyBatis 的关系映射](#mybatis-的关系映射)
    - [一对一](#一对一)
    - [一对多](#一对多)
    - [多对多](#多对多)
  - [动态 SQL](#动态-sql)
    - [if](#if)
    - [choose(when,otherwise)](#choosewhenotherwise)
    - [where](#where)
    - [set](#set)
    - [foreach](#foreach)
    - [bind](#bind)
- [Mybatis 的事务管理和缓存机制](#mybatis-的事务管理和缓存机制)
  - [MyBatis 的事务管理](#mybatis-的事务管理)
    - [事务的概念](#事务的概念)
    - [Transaction 接口](#transaction-接口)
    - [事务的配置创建和使用](#事务的配置创建和使用)
    - [事务工厂的创建](#事务工厂的创建)
    - [事务工厂 TransactionFactory](#事务工厂-transactionfactory)
    - [JdbcTransaction](#jdbctransaction)
  - [Mybatis 缓存机制](#mybatis-缓存机制)
    - [一级缓存](#一级缓存)
    - [二级缓存 (Mapper 级别)](#二级缓存-mapper-级别)

<!-- TOC END -->

# mybatis 入门
## 初看
```xml
<!-- namespace指用户自定义的命名空间。 -->
<mapper namespace="org.mapper.UserMapper">
<!--
	id="save"是唯一的标示符
	parameterType属性指明插入时使用的参数类型
	useGeneratedKeys="true"表示使用数据库的自动增长策略
 -->
  <insert id="save" parameterType="org.domain.User" useGeneratedKeys="true">
  	INSERT INTO TB_USER(name,sex,age)
  	VALUES(#{name},#{sex},#{age})
  </insert>
</mapper>
```
上面的xml定义了一条insert语句：
1. <mapper namespace="org.mapper.UserMapper"> 为这个 mapper 指定了一个唯一的 namespace，namespace 的值习惯上设置成包名+sql 映射文件名来保证唯一性。
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
		session.insert("org.mapper.UserMapper.save", user);
		// 提交事务
		session.commit();
		// 关闭Session
		session.close();
	}
}
```
正如上面的代码所示，在执行 session.insert("org.mapper.UserMapper.save", user); 之前，需要获取 SqlSession 对象，PO 只有在 SqlSession 的管理下才能够完成数据库的访问。使用 Mybatis 持久化操作通常如下：
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
select 元素有很多属性可以配置，例如：
```xml
<select
　　<!--
　　　　1. id（必须配置）
　　　　id是命名空间中的唯一标识符，可被用来代表这条语句
　　　　一个命名空间（namespace）对应一个dao接口
　　　　这个id也应该对应dao里面的某个方法（sql相当于方法的实现），因此id应该与方法名一致
　　 -->
　　id="selectUser"

　　<!--
　　　　2. parapeterType（可选配置，默认由mybatis自动选择处理）
　　　　将要传入语句的参数的完全限定名或别名，如果不配置，mybatis会通过ParamterHandler根据参数类型默认选择合适的typeHandler进行处理
　　　　paramterType 主要指定参数类型，可以是int, short, long, string等类型，也可以是复杂类型（如对象）
　　 -->
　　parapeterType="int"

　　<!--
　　　　3. resultType（resultType 与 resultMap 二选一配置）
　　　　用来指定返回类型，指定的类型可以是基本类型，也可以是java容器，也可以是javabean
　　 -->
　　resultType="hashmap"
　　
　　<!--
　　　　4. resultMap（resultType 与 resultMap 二选一配置）
　　　　用于引用我们通过 resultMap 标签定义的映射类型，这也是mybatis组件高级复杂映射的关键
　　 -->
　　resultMap="USER_RESULT_MAP"
　　
　　<!--
　　　　5. flushCache（可选配置）
　　　　将其设置为true，任何时候语句被调用，都会导致本地缓存和二级缓存被清空，默认值：false
　　 -->
　　flushCache="false"

　　<!--
　　　　6. useCache（可选配置）
　　　　将其设置为true，会导致本条语句的结果被二级缓存，默认值：对select元素为true
　　 -->
　　useCache="true"

　　<!--
　　　　7. timeout（可选配置）
　　　　这个设置是在抛出异常之前，驱动程序等待数据库返回请求结果的秒数，默认值为：unset（依赖驱动）
　　 -->
　　timeout="10000"

　　<!--
　　　　8. fetchSize（可选配置）
　　　　这是尝试影响驱动程序每次批量返回的结果行数和这个设置值相等。默认值为：unset（依赖驱动）
　　 -->
　　fetchSize="256"

　　<!--
　　　　9. statementType（可选配置）
　　　　STATEMENT, PREPARED或CALLABLE的一种，这会让MyBatis使用选择Statement, PrearedStatement或CallableStatement，默认值：PREPARED
　　 -->
　　statementType="PREPARED"

　　<!--
　　　　10. resultSetType（可选配置）
　　　　FORWARD_ONLY，SCROLL_SENSITIVE 或 SCROLL_INSENSITIVE 中的一个，默认值为：unset（依赖驱动）
　　 -->
　　resultSetType="FORWORD_ONLY"
></select>
```

### insert, update 和 delete
他们的属性大多和 select 差不多，特有的属性描述如下：
```xml
<insert
　　<!--
　　　　同 select 标签
　　 -->
　　id="insertProject"

　　<!--
　　　　同 select 标签
　　 -->
　　paramterType="projectInfo"
　　
　　<!--
　　　　1. useGeneratedKeys（可选配置，与 keyProperty 相配合）
　　　　设置为true，并将 keyProperty 属性设为数据库主键对应的实体对象的属性名称
　　 -->
　　useGeneratedKeys="true"

　　<!--
　　　　2. keyProperty（可选配置，与 useGeneratedKeys 相配合）
　　　　用于获取数据库自动生成的主键
　　 -->
　　keyProperty="projectId"
>
```

### sql
sql 元素可以被用来定义可重用的 SQL 代码段，可以包含其他语句中，他可以静态地参数化。例如：
```xml
<!-- 我们在其中定义类似于表的结构 -->
<sql id="trunkItemColumns">
		a.id AS "id",
		a.trunk_type AS "trunkType",
		a.item_code AS "itemCode",
		a.item_name AS "itemName",
		a.display_name AS "displayName",
		a.create_by AS "createBy.id",
		a.create_date AS "createDate",
		a.update_date AS "updateDate"
	</sql>
```
这个 SQL 片段就可以被包含咋其他语句中，例如：
```xml
<select id="get" resultType="TrunkItem">
		SELECT
			<include refid="trunkItemColumns"/>
		FROM trunk_item a
		WHERE a.id = #{id}
	</select>
```

### ResultMap
ResultMap 告诉 Mybatis 将结果集中取出 大数据转换成开发者若需要的对象。
```xml
<select id="selectUser" resultType="map">
  select * from sys_user
</select>
```
<select../>元素执行一条查询语句，查询 sys_user 表所有数据，resultType="map" 说明返回的数据是一个 Map 集合，列名为 key，列值是 value.

我们用一个 List<Map<String, Object>> 接受返回的结果，在 console 中显示为：
```text
{sex=男, name=jack, id=1, age=22}
{sex=男, name=jack1, id=2, age=18}
```
如果要对应到一个 POJO 类，可以使用 <resultMap> 元素来定义一个 resultMap 完成映射。例如：
```xml
<resultMap id="userResultMap" type="User">
  <id property="id" column="user_id"/>
  <result property="name" column="user_name"/>
  ···
</resultMap>
<select id="selectUser" resultType="userResultMap">
  select * from sys_user
</select>
```
配置文件的详细解释如下：
```xml
<!--
　　1. type 对应的返回类型，可以是javabean, 也可以是其它
　　2. id 必须唯一， 用于标示这个resultMap的唯一性，在使用resultMap的时候，就是通过id引用
　　3. extends 继承其他resultMap标签
 -->
<resultMap type="" id="" extends="">　　
　　<!--
　　　　1. id 唯一性，注意啦，这个id用于标示这个javabean对象的唯一性， 不一定会是数据库的主键（不要把它理解为数据库对应表的主键）
　　　　2. property 属性对应javabean的属性名
　　　　3. column 对应数据库表的列名
       （这样，当javabean的属性与数据库对应表的列名不一致的时候，就能通过指定这个保持正常映射了）
　　 -->
　　<id property="" column=""/>

　　<!--
　　　　result 与id相比，对应普通属性
　　 -->    
　　<result property="" column=""/>

　　<!--
　　　　constructor 对应javabean中的构造方法
　　 -->
　　<constructor>
　　　　<!-- idArg 对应构造方法中的id参数 -->
       <idArg column=""/>
       <!-- arg 对应构造方法中的普通参数 -->
       <arg column=""/>
   </constructor>

   <!--
　　　　collection 为关联关系，是实现一对多的关键
　　　　1. property 为javabean中容器对应字段名
　　　　2. ofType 指定集合中元素的对象类型
　　　　3. select 使用另一个查询封装的结果
　　　　4. column 为数据库中的列名，与select配合使用
    -->
　　<collection property="" column="" ofType="" select="">
　　　　<!--
　　　　　　当使用select属性时，无需下面的配置
　　　　 -->
　　　　<id property="" column=""/>
　　　　<result property="" column=""/>
　　</collection>

　　<!--
　　　　association 为关联关系，是实现一对一的关键
　　　　1. property 为javabean中容器对应字段名
　　　　2. javaType 指定关联的类型，当使用select属性时，无需指定关联的类型
　　　　3. select 使用另一个select查询封装的结果
　　　　4. column 为数据库中的列名，与select配合使用
　　 -->
　　<association property="" column="" javaType="" select="">
　　　　<!--
　　　　　　使用select属性时，无需下面的配置
　　　　 -->
　　　　<id property="" column=""/>
　　　　<result property="" column=""/>
　　</association>
</resultMap>
```

# MyBatis 的关系映射和动态 SQL
## MyBatis 的关系映射
### 一对一
我们先假设有这样一个关系，每个人都有一张对应的身份证，对应的 Java 类可以这样定义：
```sql
create table tb_person(
  id INT PRIMARY KEY,
  NAME VARCHAR(60),
  card_id INT UNIQUE
)
create table tb_card(
  id INT PRIMARY KEY,
  code CHAR(18)
)
```
```java
public class Card{
  private Integer id;
  private String code;
  // 省略 getter 和 setter
}
```
```java
public class Persone{
  private Integer id;
  private String name;
  private Card card;
  // 忽略 getter 和 setter
}
```
在 XML 中：
```xml
<mapper namespace="org.mapper.CardMapper">
	<!-- 根据id查询Card，返回Card对象 -->
  <select id="selectCardById" parameterType="int" resultType="org.domain.Card">
  	SELECT * from tb_card where id = #{id}
  </select>
</mapper>
```
```xml
<mapper namespace="org.mapper.PersonMapper">

	<!-- 根据id查询Person，返回resultMap -->
  <select id="selectPersonById" parameterType="int"
  	resultMap="personMapper">
  	SELECT * from tb_person where id = #{id}
  </select>

  <!-- 映射Peson对象的resultMap -->
	<resultMap type="org.domain.Person" id="personMapper">
		<id property="id" column="id"/>
		<result property="name" column="name"/>
		<result property="sex" column="sex"/>
		<result property="age" column="age"/>
		<!-- 一对一关联映射:association   -->
	  <association property="card" column="card_id"
      select="org.mapper.CardMapper.selectCardById"
      javaType="org.domain.Card"/>
	</resultMap>
</mapper>
```
**PersonMapper 中定义的 mapper.xml 中，使用了 <association> 元素映射一对一的关系，select 属性表示会使用 tb_person 中的 card_id 的值作为参数执行 CardMapper 中定义的 selectCardById 查询对应的 Card，并封装到 property 表示的 card 对象中。**

### 一对多
我们先假设一个关系：班级和学生，很明显的存在一对多的关系。对应的 POJO 类如下：
```java
public class Clazz implements Serializable {

	private Integer id; // 班级id，主键
	private String code; // 班级编号
	private String name; // 班级名称

	// 班级和学生是一对多的关系，即一个班级可以有多个学生
	private List<Student> students;
}

public class Student implements Serializable {

	private Integer id; // 学生id，主键
	private String name; // 姓名
	private String sex;  // 性别
	private Integer age; // 年龄

	// 学生和班级是多对一的关系，即一个学生只属于一个班级
	private Clazz clazz;
}
```

Clazz XML 映射文件:
```xml
<mapper namespace="org.mapper.ClazzMapper">
	<!-- 根据id查询班级信息，返回resultMap -->
	  <select id="selectClazzById" parameterType="int" resultMap="clazzResultMap">
	  	SELECT * FROM tb_clazz  WHERE id = #{id}
	  </select>

	   <!-- 映射Clazz对象的resultMap -->
	<resultMap type="org.domain.Clazz" id="clazzResultMap">
		<id property="id" column="id"/>
		<result property="code" column="code"/>
		<result property="name" column="name"/>
		<!-- 一对多关联映射:collection fetchType="lazy"表示懒加载  -->
		<collection property="students" javaType="ArrayList"
	  column="id" ofType="org.domain.Student"
	  select="org.mapper.StudentMapper.selectStudentByClazzId"
	  fetchType="lazy">
	  	<id property="id" column="id"/>
	  	<result property="name" column="name"/>
	  	<result property="sex" column="sex"/>
	  	<result property="age" column="age"/>
	  </collection>
	</resultMap>
</mapper>
```
XML 中定义了一个 clazzResultMap 来告诉数据库如何返回结果。里面除了简单的属性如 id,code,name 之外，还有一个关联对象 students。由于 students 是一个 List 集合，所以 clazzResultMap 中使用了 <collects/> 元素映射一对多关系，select 会使用 column 属性的 id值作为参数执行 selectStudentByClazzId 查询该班级对应的所有学生数据，查询到的数据将会被封装到 property 表示的 students 对象中。

fetchType="lazy"：表示使用延迟加载； lazy：延迟 eager：立即。

使用懒加载还需要在配置文件中加入配置：
```xml
<!-- 要使延迟加载生效必须配置下面两个属性 -->
<setting name="lazyLoadingEnabled" value="true"/>
<setting name="aggressiveLazyLoading" value="false"/>
```

学生类的 mapper 配置文件：
```xml
<mapper namespace="org.mapper.StudentMapper">
	<!-- 根据id查询学生信息，多表连接，返回resultMap -->
  <select id="selectStudentById" parameterType="int" resultMap="studentResultMap">
  	SELECT * FROM tb_clazz c,tb_student s
  	WHERE c.id = s.clazz_id
  	 AND s.id = #{id}
  </select>

  <!-- 根据班级id查询学生信息，返回resultMap -->
  <select id="selectStudentByClazzId" parameterType="int"
  resultMap="studentResultMap">
  	SELECT * FROM tb_student WHERE clazz_id = #{id}
  </select>

   <!-- 映射Student对象的resultMap -->
	<resultMap type="org.domain.Student" id="studentResultMap">
		<id property="id" column="id"/>
	  	<result property="name" column="name"/>
	  	<result property="sex" column="sex"/>
	  	<result property="age" column="age"/>
		<!-- 多对一关联映射:association   -->
		<association property="clazz" javaType="org.domain.Clazz">
			<id property="id" column="id"/>
			<result property="code" column="code"/>
			<result property="name" column="name"/>
		</association>
	</resultMap>
</mapper>
```
需要注意的是：因为 selectStudentById 方法使用了多表连结，所以 clazz 的信息已经被加载出来了，所以 <association/> 只是简单的封装了信息成为一个 Clazz 类。就不像之前的 一对一 中还配备了一个 select 元素在 <association/> 中。


### 多对多
我们先定义一个多对多关系：一个用户有多个订单，而且一个订单中可能有多种物品。所以订单和物品属于一种多对多的关系。**对于数据库中多对多关系建议使用一个中间表来维护关系。**

先看我们定义的 POJO 类：
```java
public class User implements Serializable{

	private Integer id;  // 用户id，主键
	private String username;  // 用户名
	private String loginname; // 登录名
	private String password;  // 密码
	private String phone;    // 联系电话
	private String address;  // 收货地址

	// 用户和订单是一对多的关系，即一个用户可以有多个订单
	private List<Order> orders;
}

public class Order implements Serializable {

	private Integer id;  // 订单id，主键
	private String code;  // 订单编号
	private Double total; // 订单总金额

	// 订单和用户是多对一的关系，即一个订单只属于一个用户
	private User user;

	// 订单和商品是多对多的关系，即一个订单可以包含多种商品
	private List<Article> articles;
}

public class Article implements Serializable {

	private Integer id;		// 商品id，主键
	private String name;	// 商品名称
	private Double price;	// 商品价格
	private String remark;	// 商品描述

	// 商品和订单是多对多的关系，即一种商品可以包含在多个订单中
	private List<Order> orders;
}
```

同时为了处理多对多的关系，我们有一个中间表 tb_item:
```sql
create table(
  order_id INT,
  article_id INT,
  amount INT,
  PRIMARY KEY (order_id, article_id)
)
```

接下来是 Mapper.xml文件
```xml
<mapper namespace="org.mapper.UserMapper">

	<resultMap type="org.domain.User" id="userResultMap">
		<id property="id" column="id"/>
		<result property="username" column="username"/>
		<result property="loginname" column="loginname"/>
		<result property="password" column="password"/>
		<result property="phone" column="phone"/>
		<result property="address" column="address"/>
		<!-- 一对多关联映射:collection   -->
		<collection property="orders" javaType="ArrayList"
	  column="id" ofType="org.domain.User"
	  select="org.mapper.OrderMapper.selectOrderByUserId"
	  fetchType="lazy">
	  	<id property="id" column="id"/>
	  	<result property="code" column="code"/>
	  	<result property="total" column="total"/>
	  </collection>
	</resultMap>

  <select id="selectUserById" parameterType="int" resultMap="userResultMap">
  	SELECT * FROM tb_user  WHERE id = #{id}
  </select>
</mapper>
```
resultMap 中定义了如何返回一个 userResultMap，由于 orders 是一个 List 集合对象，所以用 collection 元素对应一对多的关系，select 属性表示会使用 column 属性的 id 值（在这里既是 user 表的 id 值）传入 selectOrderByUserId 进行执行，查询出的数据封装到 property 表示的 orders 对象中。并使用懒加载。

下面是 OrderMapper.xml
```xml
<mapper namespace="org.mapper.OrderMapper">
	<resultMap type="org.domain.Order" id="orderResultMap">
		<id property="id" column="oid"/>
	  	<result property="code" column="code"/>
	  	<result property="total" column="total"/>
		<!-- 多对一关联映射:association   -->
		<association property="user" javaType="org.domain.User">
			<id property="id" column="id"/>
			<result property="username" column="username"/>
			<result property="loginname" column="loginname"/>
			<result property="password" column="password"/>
			<result property="phone" column="phone"/>
			<result property="address" column="address"/>
		</association>
		<!-- 多对多映射的关键:collection   -->
		<collection property="articles" javaType="ArrayList"
	  column="oid" ofType="org.domain.Article"
	  select="org.mapper.ArticleMapper.selectArticleByOrderId"
	  fetchType="lazy">
	  	<id property="id" column="id"/>
	  	<result property="name" column="name"/>
	  	<result property="price" column="price"/>
	  	<result property="remark" column="remark"/>
	  </collection>
	</resultMap>

	<!-- 注意，如果查询出来的列同名，例如tb_user表的id和tb_order表的id都是id，同名，需要使用别名区分 -->
  <select id="selectOrderById" parameterType="int" resultMap="orderResultMap">
  	SELECT u.*,o.id AS oid,CODE,total,user_id
  	 FROM tb_user u,tb_order o
  	WHERE u.id = o.user_id
  	 AND o.id = #{id}
  </select>

  <!-- 根据userid查询订单 -->
  <select id="selectOrderByUserId" parameterType="int" resultType="org.domain.Order">
  	SELECT * FROM tb_order WHERE user_id = #{id}
  </select>

</mapper>
```
在这其中：
1. 定义了一个 <select id="selectOrderByUserId".../> 已供 Usermapper.xml 中的查询使用
2. 定义了一个 <select id="selectOrderById".../> 返回一个 Order 对象，参考 Order 类的定义，这个查找中除了简单的 id，code，total 属性外，需要关联对应的 user 属性，**因为订单和用户是多对一的关系，多对一的关系一般都是及时加载的，所以我们可以在语句中发现它用了关联语句直接查找除了 user 的信息，resultMap 中的 <association../> 只是简单的封装 user 信息成 User 类。** 因为一个订单可能有多个物品，所以 collection 元素便来完成对于物品信息的管理查询，select 属性 表示会使用 column 属性的 oid （order表的 id）值作为参数执行 selectArticleByOrderId 方法来查找对应的物品信息，查询到的信息再封装到 property 代表的 articles 对象中。

最后看 ArticleMapper.xml
```xml
<mapper namespace="org.mapper.ArticleMapper">
  <select id="selectArticleByOrderId" parameterType="int"
  resultType="org.domain.Article">
  	SELECT * FROM tb_article WHERE id IN (
		SELECT article_id FROM tb_item WHERE order_id = #{id}
	)
  </select>
</mapper>
```
里面就根据传入的order id 进行物品查询，而且是从中间表差的，所以 SQL 语句中有一个 IN 子查询。

## 动态 SQL
常用的动态 SQL 元素包括：
- if
- choose(when, otherwise)
- where
- set
- foreach
- bind

示例 POJO 类：
```java
public class Employee implements Serializable {

	private Integer id;			 // 主键id
	private String loginname;	 // 登录名
	private String password;	 // 密码
	private String name;		 // 真实姓名
	private String sex;			 // 性别
	private Integer age;		 // 年龄
	private String phone;		 // 电话
	private Double sal;		     // 薪水
	private String state;	 	 // 状态
}
```

### if
有条件的加入一些 where 语句。例如：

```xml
<!-- if -->
  <select id="selectEmployeeByIdLike"
  	resultType="org.domain.Employee">
  	SELECT * FROM tb_employee WHERE state = 'ACTIVE'
  	<!-- 可选条件，如果传进来的参数有id属性，则加上id查询条件 -->
  	<if test="id != null ">
  		and id = #{id}
  	</if>
  </select>
```

在调用的时候我们可以有多种方法进行调用，因为这牵扯到 Mybatis 获取参数的方式
```java
List<Employee> selectEmployeeByIdLike(HashMap<String, Object> params);
```
上面的方式接受一个 HashMap 作为参数。在 Mybatis 中，#{id} 表达式获取参数的两种方式为：
1. 从 HashMap 集合中获取相应的 property
2. 从 javabean 中获取 property
所以当采用上图的方式调用时候，代码需要组建一个 HashMap 进行传参
```java
HahsMap<String, Obeject> params = new HashMap<>();
params.put("id", 1);
List<Employee> list = selectEmployeeByIdLike(params);
```

当然对于1个参数而言，也可以直接申明为:
```java
List<Employee> selectEmployeeByIdLike(Integer id);
```

如果参数大于1个，除了上面继续在 HashMap params 中添加参数的办法，我们可以用 @Param 注释：
```xml
<!-- if -->
<select id="selectEmployeeByLoginLike"
	resultType="org.domain.Employee">
	SELECT * FROM tb_employee WHERE state = 'ACTIVE'
	<!-- 两个可选条件，例如登录功能的登录名和密码查询 -->
	<if test="loginname != null and password != null">
		and loginname = #{loginname} and password = #{password}
	</if>
</select>
```
```java
List<Employee> selectEmployeeByLoginLike(@Param("loginname") String  loginname,
                                         @Param("password") String password);
```

### choose(when,otherwise)
有些时候，我们不想用所有的条件语句，而指向从中择其一二。Mybatis 提供了 choose 元素，就像 Java 中的 Switch 语句。
```xml
<!-- choose（when、otherwise） -->
<select id="selectEmployeeChoose"
	parameterType="hashmap"
	resultType="org.domain.Employee">
	SELECT * FROM tb_employee WHERE state = 'ACTIVE'
	<!-- 如果传入了id，就根据id查询，没有传入id就根据loginname和password查询，否则查询sex等于男的数据 -->
	<choose>
		<when test="id != null">
			and id = #{id}
		</when>
		<when test="loginname != null and password != null">
			and loginname = #{loginname} and password = #{password}
		</when>
		<otherwise>
			and sex = '男'
		</otherwise>
	</choose>
</select>
```
如果参数中 提供了 id 就按 id 查找，如果提供了 loginname 和 password 就按 loginname 和 password 查找。若两者都没有提供，就返回所以 sex='男' 的 Employee(otherwise 中)

### where
where 元素用于把 if, choose 这些元素包起来，这样可以避免当不传入任何参数时候，SQL 可以正确执行。
```xml
<!-- where -->
<select id="selectEmployeeLike"
	resultType="org.domain.Employee">
	SELECT * FROM tb_employee  
	<where>
		<if test="state != null ">
			state = #{state}
  	</if>
  	<if test="id != null ">
  		and id = #{id}
  	</if>
  	<if test="loginname != null and password != null">
  		and loginname = #{loginname} and password = #{password}
  	</if>
	</where>
</select>
```
where 元素只有在一个及以上的 if 条件有值的情况下就会插入 WHERE 语句。而且，若最后的内容是 "AND" 或
"OR" 开头，where 元素也知道是否需要将其去除

### set
set 元素可以被用于动态包含需要的列，而舍去其他的。
```xml
<!-- set -->
<update id="updateEmployeeIfNecessary"
  parameterType="org.domain.Employee">
  update tb_employee
    <set>
      <if test="loginname != null">loginname=#{loginname},</if> <!-- 注意每一行后面的逗号 -->
      <if test="password != null">password=#{password},</if>
      <if test="name != null">name=#{name},</if>
      <if test="sex != null">sex=#{sex},</if>
      <if test="age != null">age=#{age},</if>
      <if test="phone != null">phone=#{phone},</if>
      <if test="sal != null">sal=#{sal},</if>
      <if test="state != null">state=#{state}</if>
    </set>
  where id=#{id}
</update>
```
set 元素会动态前置 SET 关键字，同时也会消除无关的逗号，因为使用了条件语句之后很可能会在生产的赋值语句的后面留下这些逗号。

### foreach
关于动态 SQL 另外一个常用的操作就是需要对一个集合进行遍历，通常发生在构建 IN 条件语句的时候
```xml
<!-- foreach -->
<select id="selectEmployeeIn" resultType="org.domain.Employee">
  SELECT *
  FROM tb_employee
  WHERE ID in
  <foreach item="item" index="index" collection="list"
      open="(" separator="," close=")">
        #{item}
  </foreach>
</select>
```
foreach 允许指定一个集合，声明可以用在元素体内的集合项和索引变量。它也允许指定开闭匹配的字符串以及
在迭代中间放置分隔符。而且并不会附加多余的分隔符。
```java
// 引用时候传入相应的集合便可以
List<Employee> selectEmployeeIn(List<Integer> ids)
```

### bind
bind元素可以从 OGNL 表达式中创建一个变量并将其绑定到上下文中
```xml
<!-- bind -->
<select id="selectEmployeeLikeName"  resultType="org.domain.Employee">
  <bind name="pattern" value="'%' + _parameter.getName() + '%'" />
    SELECT * FROM tb_employee
    WHERE loginname LIKE #{pattern}
</select>
```

# Mybatis 的事务管理和缓存机制

## MyBatis 的事务管理

### 事务的概念
1. 原子性（Atomicity）
原子性是指事务包含的所有操作要么全部成功，要么全部失败回滚，这和前面两篇博客介绍事务的功能是一样的概念，因此事务的操作如果成功就必须要完全应用到数据库，如果操作失败则不能对数据库有任何影响。

2. 一致性（Consistency）
　　一致性是指事务必须使数据库从一个一致性状态变换到另一个一致性状态，也就是说一个事务执行之前和执行之后都必须处于一致性状态。

　　拿转账来说，假设用户A和用户B两者的钱加起来一共是5000，那么不管A和B之间如何转账，转几次账，事务结束后两个用户的钱相加起来应该还得是5000，这就是事务的一致性。

3. 隔离性（Isolation）
　　隔离性是当多个用户并发访问数据库时，比如操作同一张表时，数据库为每一个用户开启的事务，不能被其他事务的操作所干扰，多个并发事务之间要相互隔离。

　　即要达到这么一种效果：对于任意两个并发的事务T1和T2，在事务T1看来，T2要么在T1开始之前就已经结束，要么在T1结束之后才开始，这样每个事务都感觉不到有其他事务在并发地执行。

4. 持久性（Durability）
　　持久性是指一个事务一旦被提交了，那么对数据库中的数据的改变就是永久性的，即便是在数据库系统遇到故障的情况下也不会丢失提交事务的操作。

　　例如我们在使用JDBC操作数据库时，在提交事务方法后，提示用户事务操作完成，当我们程序执行完成直到看到提示后，就可以认定事务以及正确提交，即使这时候数据库出现了问题，也必须要将我们的事务完全执行完成，否则就会造成我们看到提示事务处理完毕，但是数据库因为故障而没有执行事务的重大错误。

### Transaction 接口
位于org.apache.ibatis.transaction包的Transaction和TransactionFactory都是接口类。

　　Transaction是事务接口，其中定义了四个方法：

　　　　　　commit()-事务提交

　　　　　　rollBack()-事务回滚

　　　　　　close()-关闭数据库连接

　　　　　　getConnection()-获取数据库连接

```java
package org.apache.ibatis.transaction;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * 事务，包装了一个Connection, 包含commit,rollback,close方法
 * 在 MyBatis 中有两种事务管理器类型(也就是 type=”[JDBC|MANAGED]”):  
 */
public interface Transaction {
  Connection getConnection() throws SQLException;
  void commit() throws SQLException;
  void rollback() throws SQLException;
  void close() throws SQLException;
}
```

### 事务的配置创建和使用
在使用 Mybatis  的时候，配置文件中配置事务管理机制：
```xml
<!-- type="MANAGED" 指让容器实现对事务的管理 -->
<transactionManager type="JDBC"/>
```
二者的不同之处在于：前者是直接使用JDK提供的JDBC来管理事务的各个环节：提交、回滚、关闭等操作，而后者则什么都不做，那么后者有什么意义呢，当然很重要。

当我们单独使用MyBatis来构建项目时，我们要在Configuration配置文件中进行环境（environment）配置，在其中要设置事务类型为JDBC，意思是说MyBatis被单独使用时就需要使用JDBC类型的事务模型，因为在这个模型中定义了事务的各个方面，使用它可以完成事务的各项操作。而MANAGED类型的事务模型其实是一个托管模型，也就是说它自身并不实现任何事务功能，而是托管出去由其他框架来实现，你可能还不明白，这个事务的具体实现就交由如Spring之类的框架来实现，而且在使用SSM整合框架后已经不再需要单独配置环境信息（包括事务配置与数据源配置），因为在在整合jar包（mybatis-spring.jar）中拥有覆盖mybatis里面的这部分逻辑的代码，实际情况是即使你显式设置了相关配置信息，系统也会视而不见......

### 事务工厂的创建
MyBatis事务的创建是交给TransactionFactory 事务工厂来创建的，如果我们将<transactionManager>的type 配置为"JDBC",那么，在MyBatis初始化解析<environment>节点时，会根据type="JDBC"创建一个JdbcTransactionFactory工厂，其源码如下：
```java
/**
     * 解析<transactionManager>节点，创建对应的TransactionFactory
     * @param context
     * @return
     * @throws Exception
     */
  private TransactionFactory transactionManagerElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      Properties props = context.getChildrenAsProperties();
      /*
            在Configuration初始化的时候，会通过以下语句，给JDBC和MANAGED对应的工厂类
            typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
            typeAliasRegistry.registerAlias("MANAGED", ManagedTransactionFactory.class);
            下述的resolveClass(type).newInstance()会创建对应的工厂实例
       */
      TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a TransactionFactory.");
  }
```
如上述代码所示，如果type = "JDBC",则MyBatis会创建一个JdbcTransactionFactory.class 实例；如果type="MANAGED"，则MyBatis会创建一个MangedTransactionFactory.class实例。

### 事务工厂 TransactionFactory
通过事务工厂TransactionFactory很容易获取到Transaction对象实例。我们以JdbcTransaction为例，看一下JdbcTransactionFactory是怎样生成JdbcTransaction的，代码如下：
```java
public class JdbcTransactionFactory implements TransactionFactory {

  public void setProperties(Properties props) {
  }

    /**
     * 根据给定的数据库连接Connection创建Transaction
     * @param conn Existing database connection
     * @return
     */
  public Transaction newTransaction(Connection conn) {
    return new JdbcTransaction(conn);
  }

    /**
     * 根据DataSource、隔离级别和是否自动提交创建Transacion
     *
     * @param ds
     * @param level Desired isolation level
     * @param autoCommit Desired autocommit
     * @return
     */
  public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {
    return new JdbcTransaction(ds, level, autoCommit);
  }
}
```

### JdbcTransaction
JdbcTransaction直接使用JDBC的提交和回滚事务管理机制 。它依赖与从dataSource中取得的连接connection 来管理transaction 的作用域，connection对象的获取被延迟到调用getConnection()方法。如果autocommit设置为on，开启状态的话，它会忽略commit和rollback。

直观地讲，就是JdbcTransaction是使用的java.sql.Connection 上的commit和rollback功能，JdbcTransaction只是相当于对java.sql.Connection事务处理进行了一次包装（wrapper），Transaction的事务管理都是通过java.sql.Connection实现的。JdbcTransaction的代码实现如下：

```java
/**
 * @see JdbcTransactionFactory
 */
/**
 * @author Clinton Begin
 */
public class JdbcTransaction implements Transaction {

  private static final Log log = LogFactory.getLog(JdbcTransaction.class);

  //数据库连接
  protected Connection connection;
  //数据源
  protected DataSource dataSource;
  //隔离级别
  protected TransactionIsolationLevel level;
  //是否为自动提交
  protected boolean autoCommmit;

  public JdbcTransaction(DataSource ds, TransactionIsolationLevel desiredLevel, boolean desiredAutoCommit) {
    dataSource = ds;
    level = desiredLevel;
    autoCommmit = desiredAutoCommit;
  }

  public JdbcTransaction(Connection connection) {
    this.connection = connection;
  }

  public Connection getConnection() throws SQLException {
    if (connection == null) {
      openConnection();
    }
    return connection;
  }

    /**
     * commit()功能 使用connection的commit()
     * @throws SQLException
     */
  public void commit() throws SQLException {
    if (connection != null && !connection.getAutoCommit()) {
      if (log.isDebugEnabled()) {
        log.debug("Committing JDBC Connection [" + connection + "]");
      }
      connection.commit();
    }
  }

    /**
     * rollback()功能 使用connection的rollback()
     * @throws SQLException
     */
  public void rollback() throws SQLException {
    if (connection != null && !connection.getAutoCommit()) {
      if (log.isDebugEnabled()) {
        log.debug("Rolling back JDBC Connection [" + connection + "]");
      }
      connection.rollback();
    }
  }

    /**
     * close()功能 使用connection的close()
     * @throws SQLException
     */
  public void close() throws SQLException {
    if (connection != null) {
      resetAutoCommit();
      if (log.isDebugEnabled()) {
        log.debug("Closing JDBC Connection [" + connection + "]");
      }
      connection.close();
    }
  }

  protected void setDesiredAutoCommit(boolean desiredAutoCommit) {
    try {
      if (connection.getAutoCommit() != desiredAutoCommit) {
        if (log.isDebugEnabled()) {
          log.debug("Setting autocommit to " + desiredAutoCommit + " on JDBC Connection [" + connection + "]");
        }
        connection.setAutoCommit(desiredAutoCommit);
      }
    } catch (SQLException e) {
      // Only a very poorly implemented driver would fail here,
      // and there's not much we can do about that.
      throw new TransactionException("Error configuring AutoCommit.  "
          + "Your driver may not support getAutoCommit() or setAutoCommit(). "
          + "Requested setting: " + desiredAutoCommit + ".  Cause: " + e, e);
    }
  }

  protected void resetAutoCommit() {
    try {
      if (!connection.getAutoCommit()) {
        // MyBatis does not call commit/rollback on a connection if just selects were performed.
        // Some databases start transactions with select statements
        // and they mandate a commit/rollback before closing the connection.
        // A workaround is setting the autocommit to true before closing the connection.
        // Sybase throws an exception here.
        if (log.isDebugEnabled()) {
          log.debug("Resetting autocommit to true on JDBC Connection [" + connection + "]");
        }
        connection.setAutoCommit(true);
      }
    } catch (SQLException e) {
      log.debug("Error resetting autocommit to true "
          + "before closing the connection.  Cause: " + e);
    }
  }

  protected void openConnection() throws SQLException {
    if (log.isDebugEnabled()) {
      log.debug("Opening JDBC Connection");
    }
    connection = dataSource.getConnection();
    if (level != null) {
      connection.setTransactionIsolation(level.getLevel());
    }
    setDesiredAutoCommit(autoCommmit);
  }

}
```

## Mybatis 缓存机制
mybatis提供了缓存机制减轻数据库压力，提高数据库性能。mybatis的缓存分为两级：一级缓存、二级缓存

一级缓存是SqlSession级别的缓存，缓存的数据只在SqlSession内有效。

二级缓存是mapper级别的缓存，同一个namespace公用这一个缓存，所以对SqlSession是共享的

[一级缓存和二级缓存例子](https://blog.csdn.net/zouxucong/article/details/68947052)

### 一级缓存
SqlSession 级别的缓存。在操作数据库时需要构造 SqlSession 对象，在对象中有一个 HashMap 用于存储缓存数据。不同的 SqlSession 之间的缓存数据区域(HashMap)互相不影响.

当同一个 SqlSession 中执行两次相同的 sql 的时候，第一次执行完毕会将结果写到缓存中，第二次查询时候会从缓存中获取。

需要注意的是，当执行的是 DML 操作(update,insert,delete)时候，并提交到数据库后， MyBatis 会情况 SqlSession 中的一级缓存，避免出现脏读的现象。当一个 SqlSession 结束后一级缓存也不存在了，Mybatis 默认开启一级缓存。

### 二级缓存 (Mapper 级别)
使用二级缓存的时候，多个 SqlSession 使用同一个 Mapper 的 sql 语句去操作数据库时候，得到的数据会存在二级缓存区域中，它同样是使用 HashMap 进行数据存储，相比一级缓存，二级的缓存的范围更大，多个 SqlSession 可以共用二级缓存。

二级缓存是多个 SqlSession 共享的，其作用域时 Mapper 的同一个 namespace。不同的 SqlSession 两次执行相同的 namespace 下的 sql 语句，且参数一样，则第一次执行后会将结果写到缓存中，第二次从缓存中取数。

Mybatis 默认没有开启二级缓存，需要在 setting 中设置开启

mybatis-config.xml
```xml
<settings>
        <setting name="cacheEnabled" value="true"/>默认是false：关闭二级缓存
<settings>
```

在userMapper.xml中配置：
```xml
<cache eviction="LRU" flushInterval="60000" size="512" readOnly="true"/>
```

以上配置创建了一个LRU缓存，并每隔60秒刷新，最大存储512个对象，而且返回的对象被认为是只读。
cache元素用来开启当前mapper的namespace下的二级缓存，该元素的属性设置如下：
  - flushInterval：刷新间隔，可以被设置为任意的正整数，而且它们代表一个合理的毫秒形式的时间段，默认情况下是不设置的，也就是没有刷新间隔，缓存仅仅调用语句时刷新。
  - size：缓存数目，可以被设置为任意正整数，要记住你的缓存对象数目和你运行环境可用内存资源数目，默认值是1024.
  - readOnly：只读，属性可以被设置为true或false，只读的缓存会给所有调用者返回缓存对象的相同实例，因此这些对象不能被修改。这提供了很重要的性能优势，可读写的缓存会返回缓存对象的拷贝（通过序列化），这会慢一些，但是安全，因此默认是false。
  - eviction：收回策略，默认为LRU，有如下几种：
    - LRU：最近最少使用的策略，移除最长时间不被使用的对象。
    - FIFO：先进先出策略，按对象进入缓存的顺序来移除它们。
    - SOFT：软引用策略，移除基于垃圾回收器状态和软引用规则的对象。
    - WEAK：弱引用策略，更积极地移除基于垃圾收集器状态和弱引用规则的对象。

**注意**：使用二级缓存时，与查询结果映射的java对象必须实现java.io.Serializable接口的序列化和反序列化操作，如果存在父类，其成员都需要实现序列化接口，实现序列化接口是为了对缓存数据进行序列化和反序列化操作，因为二级缓存数据存储介质多种多样，不一定在内存，有可能是硬盘或者远程服务器。

若禁用当前 select 语句的二级缓存，需要在 select 设置 "useCache=false"

刷新缓存（就是清空缓存）：二级缓存默认会在insert、update、delete操作后刷新缓存，可以手动配置不更新缓存，如下：
```xml
  <update id="updateById" parameterType="User" flushCache="false" />
```
