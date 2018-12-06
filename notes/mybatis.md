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
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
				.build(inputStream);
		// 创建Session实例
		SqlSession session = sqlSessionFactory.openSession();
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
