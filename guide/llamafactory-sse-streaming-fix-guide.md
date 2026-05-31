# LlamaFactoryClient streamEvents 真流式修复说明

## 结论
`streamEvents` 之前没有达到实时流式效果的根因是：
- 使用了 `exchange(..., Resource.class)`
- Spring 在 `Resource.class` 场景下通常会先把响应体读完再转换（缓冲），导致业务层只在响应结束后一次性拿到数据

现在已改为 `execute + ResponseExtractor`，直接消费 HTTP 响应输入流，可逐行读取 SSE 事件。

## 改动位置
- 文件：`ai-model-lifecycle/src/main/java/com/cetc/aimodel/client/LlamaFactoryClient.java`
- 方法：`streamEvents`

## 关键实现
1. 使用 `streamRestTemplate.execute(url, HttpMethod.GET, null, responseExtractor)` 发起请求。
2. 在 `ResponseExtractor` 中直接获取 `response.getBody()`。
3. 用 `BufferedReader` 按行读取并实时处理 `event:` / `data:`。
4. `event: complete` 时调用 `onComplete`；`event: error` 时调用 `onError`。
5. 对 IO 断流场景按“自然结束”处理，调用 `onComplete`。

## 已验证
在工作区根目录执行：

```bash
mvn -pl ai-model-lifecycle -am -DskipTests compile
```

结果：`BUILD SUCCESS`

## 上手联调指南
1. 启动后端并触发训练/评估等会产生 SSE 的接口。
2. 在 `onLine` 回调中记录接收时间戳，确认日志是分批到达而非尾部一次性到达。
3. 如果前端仍表现为“最后一次性渲染”，检查前端消费逻辑是否做了本地缓冲（如定时批量 flush）。
4. 如果上游网关存在代理缓冲，关闭对应缓冲选项（如 Nginx `proxy_buffering off`）后再复测。
