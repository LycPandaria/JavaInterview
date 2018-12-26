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
<mapper namespace="org.fkit.mapper.CardMapper">
	<!-- 根据id查询Card，返回Card对象 -->
  <select id="selectCardById" parameterType="int" resultType="org.fkit.domain.Card">
  	SELECT * from tb_card where id = #{id}
  </select>
</mapper>
```
```xml
<mapper namespace="org.fkit.mapper.PersonMapper">

	<!-- 根据id查询Person，返回resultMap -->
  <select id="selectPersonById" parameterType="int"
  	resultMap="personMapper">
  	SELECT * from tb_person where id = #{id}
  </select>

  <!-- 映射Peson对象的resultMap -->
	<resultMap type="org.fkit.domain.Person" id="personMapper">
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
<mapper namespace="org.fkit.mapper.ClazzMapper">
	<!-- 根据id查询班级信息，返回resultMap -->
	  <select id="selectClazzById" parameterType="int" resultMap="clazzResultMap">
	  	SELECT * FROM tb_clazz  WHERE id = #{id}
	  </select>

	   <!-- 映射Clazz对象的resultMap -->
	<resultMap type="org.fkit.domain.Clazz" id="clazzResultMap">
		<id property="id" column="id"/>
		<result property="code" column="code"/>
		<result property="name" column="name"/>
		<!-- 一对多关联映射:collection fetchType="lazy"表示懒加载  -->
		<collection property="students" javaType="ArrayList"
	  column="id" ofType="org.fkit.domain.Student"
	  select="org.fkit.mapper.StudentMapper.selectStudentByClazzId"
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
<mapper namespace="org.fkit.mapper.StudentMapper">
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
	<resultMap type="org.fkit.domain.Student" id="studentResultMap">
		<id property="id" column="id"/>
	  	<result property="name" column="name"/>
	  	<result property="sex" column="sex"/>
	  	<result property="age" column="age"/>
		<!-- 多对一关联映射:association   -->
		<association property="clazz" javaType="org.fkit.domain.Clazz">
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
<mapper namespace="org.fkit.mapper.UserMapper">

	<resultMap type="org.fkit.domain.User" id="userResultMap">
		<id property="id" column="id"/>
		<result property="username" column="username"/>
		<result property="loginname" column="loginname"/>
		<result property="password" column="password"/>
		<result property="phone" column="phone"/>
		<result property="address" column="address"/>
		<!-- 一对多关联映射:collection   -->
		<collection property="orders" javaType="ArrayList"
	  column="id" ofType="org.fkit.domain.User"
	  select="org.fkit.mapper.OrderMapper.selectOrderByUserId"
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
<mapper namespace="org.fkit.mapper.OrderMapper">
	<resultMap type="org.fkit.domain.Order" id="orderResultMap">
		<id property="id" column="oid"/>
	  	<result property="code" column="code"/>
	  	<result property="total" column="total"/>
		<!-- 多对一关联映射:association   -->
		<association property="user" javaType="org.fkit.domain.User">
			<id property="id" column="id"/>
			<result property="username" column="username"/>
			<result property="loginname" column="loginname"/>
			<result property="password" column="password"/>
			<result property="phone" column="phone"/>
			<result property="address" column="address"/>
		</association>
		<!-- 多对多映射的关键:collection   -->
		<collection property="articles" javaType="ArrayList"
	  column="oid" ofType="org.fkit.domain.Article"
	  select="org.fkit.mapper.ArticleMapper.selectArticleByOrderId"
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
  <select id="selectOrderByUserId" parameterType="int" resultType="org.fkit.domain.Order">
  	SELECT * FROM tb_order WHERE user_id = #{id}
  </select>

</mapper>
```
在这其中：
1. 定义了一个 <select id="selectOrderByUserId".../> 已供 Usermapper.xml 中的查询使用
2. 定义了一个 <select id="selectOrderById".../> 返回一个 Order 对象，参考 Order 类的定义，这个查找中除了简单的 id，code，total 属性外，需要关联对应的 user 属性，**因为订单和用户是多对一的关系，多对一的关系一般都是及时加载的，所以我们可以在语句中发现它用了关联语句直接查找除了 user 的信息，resultMap 中的 <association../> 只是简单的封装 user 信息成 User 类。** 因为一个订单可能有多个物品，所以 collection 元素便来完成对于物品信息的管理查询，select 属性 表示会使用 column 属性的 oid （order表的 id）值作为参数执行 selectArticleByOrderId 方法来查找对应的物品信息，查询到的信息再封装到 property 代表的 articles 对象中。

最后看 ArticleMapper.xml
```xml
<mapper namespace="org.fkit.mapper.ArticleMapper">
  <select id="selectArticleByOrderId" parameterType="int"
  resultType="org.fkit.domain.Article">
  	SELECT * FROM tb_article WHERE id IN (
		SELECT article_id FROM tb_item WHERE order_id = #{id}
	)
  </select>
</mapper>
```
里面就根据传入的order id 进行物品查询，而且是从中间表差的，所以 SQL 语句中有一个 IN 子查询。
