# common

公共模块：全局返回值 `Resp`、常量等，**无 Spring 依赖**，各业务模块按需引用。

## 依赖

```xml
<dependency>
  <groupId>com.cetc</groupId>
  <artifactId>common</artifactId>
</dependency>
```

## 统一返回值 Resp

Controller 返回数据用 `Resp<T>` 封装：

```java
return Resp.ok(data);
return Resp.ok();
return Resp.fail("失败原因");
return Resp.fail(RespCode.NOT_FOUND);
```
