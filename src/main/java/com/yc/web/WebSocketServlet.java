package com.yc.web;

import com.yc.bean.WebSocket;
import com.yc.utils.JsonModel;
import com.yc.utils.adminEchoServer;
import com.yc.utils.userEchoServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
@WebServlet(value = "/websocket.action")
public class WebSocketServlet extends BaseServlet{
    ////获取初始化信息  建立连接
    protected void getServerInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> serverInfo = new HashMap<>();
        String protocol = request.isSecure() ? "wss" : "ws";
        String host = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();

        serverInfo.put("protocol", protocol);
        serverInfo.put("host", host);
        serverInfo.put("port", String.valueOf(port));
        serverInfo.put("contextPath", contextPath);

        JsonModel jm = new JsonModel();
        jm.setCode(1);
        jm.setObj(serverInfo);
        writeJson(jm,response);
    }
    //  发送消息 存到  redis数据库中
    public void setMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm = new JsonModel();
        // name 表示 用户/客服  user/admin
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        Jedis jedis = new Jedis("localhost", 6379);
        // 当前时间戳
        long currentTimestamp = System.currentTimeMillis() / 1000;
        //  存  redis  数据库
        // 添加新成员到有序集合Sorted Set  格式: content_websocket   时间戳   用户姓名:内容
        jedis.zadd("content_websocket", currentTimestamp, name+":"+content);
        jm.setCode(1);
        jm.setObj("发送成功");
        super.writeJson(jm,response);
        //**** 通知 用户端  或者  客服端  刷新消息了
        if ("user".equals(name)){
            userEchoServer userEchoServer = new userEchoServer();
            userEchoServer.send("我是用户端，你好");
        }else {
            adminEchoServer adminEchoServer = new adminEchoServer();
            adminEchoServer.send("我是客服端，你好");
        }
    }
    //从redis数据库 中  获取聊天内容
    protected void getMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm = new JsonModel();
        Jedis jedis = new Jedis("localhost", 6379);
        // 获取有序集合的全部成员
        Set<Tuple> sortedSetMembers  =  jedis.zrangeWithScores("content_websocket",0,-1);
        // 打印输出每个成员及其分数
        List<WebSocket> list = new ArrayList<>();
        for (Tuple member : sortedSetMembers) {
            String memberValue = member.getElement(); // 内容
            Long score = (long) member.getScore();    // 时间戳
            String[] parts = memberValue.split(":");
            WebSocket webSocket = new WebSocket();
            if (parts[0]==null || parts[1]==null){
                continue;
            }
            webSocket.setName(parts[0]);//姓名
            webSocket.setContent(parts[1]);//内容
            Date date = new Date(score*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(date);
            webSocket.setTimestamp(timestamp);//时间
            list.add(webSocket);
        }
        jm.setCode(1);
        jm.setObj(list);
        super.writeJson(jm,response);
    }
}
