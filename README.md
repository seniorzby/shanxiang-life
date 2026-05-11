# 闪享生活志 (Flash Buy & AI Agent System)

## 🚀 项目简介

**闪享生活志** 是一款基于 Spring Boot 生态构建的高并发秒杀与智能生活管家系统。项目深度集成了 AI Agent 能力，并针对秒杀场景下的超卖问题、分布式一致性挑战以及恶意流量攻击，设计了一套工业级的物理防御体系。

## 🛠️ 技术栈

* **核心框架**: Spring Boot 3.x, Spring AOP
* **中间件**: Redis (ZSet, Lua, Stream), Kafka (Decoupling, DLQ)
* **AI 引擎**: LangChain4j + 阿里百炼 (Qwen-Max)
* **数据库**: MySQL 8.x
* **持久层**: MyBatis
* **通信与缓存**: Redis Chat Memory, Sliding Window Rate Limiter

## ✨ 核心亮点

### 1. 🤖 基于 Function Calling 的 AI 智能管家

* **语义识别与参数提取**: 集成 LangChain4j，利用大模型对非结构化自然语言进行意图识别。
* **业务逻辑闭环**: 通过 `@Tool` 注解将 Java 本地方法暴露给大模型。模型在识别到预约、查询意图后，自动提取参数并触发本地 `BookingTools` 逻辑，实现“查、定、改”的一体化交互。
* **会话状态管理**: 基于 **Redis 存储对话上下文**，解决了模型 API 无状态的缺陷，支持跨轮次的数据推断（如：根据上一轮查询的商家自动执行本轮预约）。

### 2. ⚡ 高并发秒杀防御体系 (Redis + Kafka)

* **抗压预扣逻辑**: 放弃昂贵的 MySQL 行锁，采用 **Lua 脚本** 在 Redis 内存层面完成“库存扣减”与“一人一单资格校验”，实现微秒级响应。
* **异步削峰填谷**: 利用 **Kafka** 对下单请求进行异步解耦。前端请求在 Redis 校验通过后立即返回“排队中”，后端消费者根据数据库承载能力平滑执行落库操作。
* **最终一致性保障**:
* **异常回滚**: 消费端落库失败时，主动回滚 Redis 库存并清除购买标识。
* **死信队列 (DLQ)**: 针对无法自动修复的异常消息，转入死信队列进行人工追溯与补单。
* **夜间对账系统**: 每日定时拉齐 MySQL（唯一真理源）与 Redis 数据，修复由于物理宕机导致的库存蒸发。



### 3. 🛡️ 细粒度滑动窗口限流

* **动态窗口算法**: 采用 Redis **ZSet** 存储用户请求时间戳。
* **物理防御**: 相比传统固定窗口，有效拦截了时间边界处的两倍突发流量。通过 `zremrangebyscore` 实时滑动剔除过期记录，精准防御恶意刷券与爬虫攻击。
* **TTL 缓冲保护**: 设置 Key 过期时间略大于业务窗口长度，预留物理耗时缓冲带，防止由于网络延迟导致的限流失效。

## 📂 项目结构

```text
├── src/main/java/com/shanxiang
│   ├── config          # 基础配置 (Redis, Kafka, AiConfig)
│   ├── controller      # 接口层 (AgentController, OrderController)
│   ├── service         # 业务逻辑层
│   ├── agent           # AI Agent 核心逻辑 (Assistant, Tools)
│   ├── aspect          # AOP 切面 (RateLimitAspect)
│   ├── consumer        # Kafka 消息消费者
│   └── util            # 工具类 (LuaScriptLoader, RedisKeyProvider)
└── src/main/resources
    ├── mapper          # MyBatis XML 映射
    └── lua             # 秒杀与限流 Lua 脚本文件

```
