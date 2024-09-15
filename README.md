- 简易一对一聊天室

  这是一个基于 WebSocket 的简易一对一聊天室应用。该应用允许两个用户实时进行一对一聊天，支持基本的消息发送和接收功能。前端使用了 Vue.js，后端使用了 Java 和 Servlet，并利用 Redis 存储聊天记录。

  ## 功能

  - **实时消息传递**：使用 WebSocket 实现即时消息传输，聊天双方可以实时看到对方的消息。
  - **消息存储**：聊天记录被存储在 Redis 数据库中，使用 zset 格式存储。
    - **Key**: `content_websocket`
    - **Score**: 时间戳
    - **Member**: 用户姓名:内容

  ## 技术栈

  - **前端**: Vue.js, Axios
  - **后端**: Java, Servlet
  - **数据库**: Redis
  - **服务器**: Tomcat