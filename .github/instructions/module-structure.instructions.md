---
description: "模块结构规范 — 新建业务模块或在已有模块中新增功能时的目录布局、文件组织和 pom.xml 配置约定"
applyTo: "**/pom.xml"
---

# 模块结构规范

## 1. Maven 模块结构

### 聚合工程约定

- 父 POM（`x-wxwh/pom.xml`）统一管理所有依赖版本（`<dependencyManagement>`）
- 子模块 POM **不写版本号**，版本由父 POM 的 `dependencyManagement` 管控
- Spring Boot 版本：`2.7.18`
- MyBatis-Plus 版本：`3.5.6`
- Knife4j 版本：`4.4.0`
- 数据库驱动：`org.postgresql:postgresql:42.7.3`

### 典型业务模块 pom.xml

```xml
<project>
    <parent>
        <groupId>com.cetc</groupId>
        <artifactId>x-wxwh</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>

    <artifactId>{模块artifactId}</artifactId>
    <packaging>jar</packaging>
    <name>{模块中文名}</name>

    <dependencies>
        <!-- 公共模块（Resp、分页、异常等） -->
        <dependency>
            <groupId>com.cetc</groupId>
            <artifactId>common</artifactId>
        </dependency>
        <!-- MyBatis-Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
        </dependency>
        <!-- 数据库驱动（人大金仓 PG 兼容） -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        <!-- API 文档 -->
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 2. 源码目录布局

每个业务模块遵循如下标准目录结构：

```
{module}/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/cetc/{pkg}/
        │       ├── {ModuleName}Application.java      # 启动类
        │       ├── config/
        │       │   └── Knife4jConfig.java            # Swagger 文档配置
        │       ├── controller/
        │       │   └── XxxController.java
        │       ├── service/
        │       │   ├── XxxService.java               # 接口
        │       │   └── impl/
        │       │       └── XxxServiceImpl.java       # 实现
        │       ├── mapper/
        │       │   └── XxxMapper.java
        │       ├── entity/
        │       │   └── Xxx.java                      # 数据库实体
        │       ├── dto/
        │       │   ├── XxxCreateDTO.java             # 创建入参
        │       │   ├── XxxUpdateDTO.java             # 更新入参
        │       │   └── query/
        │       │       └── XxxQueryDTO.java          # 分页/查询入参
        │       └── vo/
        │           ├── XxxVO.java                    # 列表展示 VO
        │           └── XxxDetailVO.java              # 详情 VO
        └── resources/
            ├── application.yml                       # 配置文件
            └── mapper/
                └── XxxMapper.xml                     # 复杂 SQL
```

---

## 3. 各层文件约定

### 启动类

```java
@SpringBootApplication
public class XxxApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxxApplication.class, args);
    }
}
```

### Entity

```java
@TableName("t_xxx")
public class Xxx extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    // 业务字段...
    // getter / setter
}
```

### Mapper

```java
@Mapper
public interface XxxMapper extends BaseMapper<Xxx> {
    // 简单查询使用 MyBatis-Plus LambdaQuery，无需写在这里
    // 复杂查询在 XML 中定义方法签名
    IPage<XxxVO> pageXxx(Page<XxxVO> page, @Param("query") XxxQueryDTO query);
}
```

### Service 接口

```java
public interface XxxService extends IService<Xxx> {
    IPage<XxxVO> page(XxxQueryDTO query);
    XxxDetailVO getDetailById(Long id);
    Long create(XxxCreateDTO dto);
    void update(Long id, XxxUpdateDTO dto);
    void delete(Long id);
}
```

### ServiceImpl

```java
@Service
public class XxxServiceImpl extends ServiceImpl<XxxMapper, Xxx> implements XxxService {

    @Override
    public IPage<XxxVO> page(XxxQueryDTO query) {
        Page<XxxVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return baseMapper.pageXxx(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(XxxCreateDTO dto) {
        Xxx entity = new Xxx();
        // BeanUtils.copyProperties(dto, entity); 或手动赋值
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, XxxUpdateDTO dto) {
        Xxx entity = getById(id);
        if (entity == null) throw new BizException("记录不存在");
        // 更新字段...
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
    }
}
```

---

## 4. application.yml 约定

```yaml
server:
  port: 8080   # 各模块使用不同端口，避免冲突

spring:
  application:
    name: {module-name}
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://{host}:{port}/{db}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 开发环境开启SQL日志

knife4j:
  enable: true
  setting:
    language: zh_CN
```

---

## 5. 新增模块检查清单

- [ ] 在父 POM `<modules>` 中注册新模块
- [ ] 子模块 POM 继承父 POM，不重复定义版本
- [ ] 启动类位于 `com.cetc.{pkg}` 根包下
- [ ] `application.yml` 配置正确，端口不与其他模块冲突
- [ ] 已依赖 `common` 模块（Resp、BizException 等）
- [ ] 已添加 `Knife4jConfig` 配置接口文档分组
- [ ] 数据库表名以 `t_` 为前缀（或遵循已有命名规范）
- [ ] Mapper XML 放在 `resources/mapper/` 目录下
