package org.example.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ClassName: WebSocketServer
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/6/20 17:01
 **/
@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}")  // 接口路径 ws://localhost:8087/websocket/userId;
public class WebSocketServer {

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    /**
     * 用户ID
     */
    private String userId;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    //  注：底下WebSocket是当前类名
    private static final CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<>();
    // 用来存在线连接用户信息
    private static final Map<String, Set<Session>> sessionPool = new ConcurrentHashMap<>();

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        try {
            this.session = session;
            this.userId = userId;
            webSockets.add(this);
            sessionPool.computeIfAbsent(userId, s -> new CopyOnWriteArraySet<>()).add(session);
            log.info("【websocket消息】有新的连接 {}, 总数为: {}", userId, webSockets.size());
        } catch (Exception e) {
            log.error("exception message", e);
        }
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam(value = "userId") String userId) {
        if (!ObjectUtils.isEmpty(sessionPool.get(userId))) {
            //与该用户存在会话 移除该用户的指定连接
            Set<Session> sessions = sessionPool.get(userId);
            sessions.remove(session);
            webSockets.remove(this);
            log.info("【websocket消息】连接断开 {}, 总数为: {}", userId, webSockets.size());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("【websocket消息】收到客户端消息:" + session.getRequestURI() + "  " + message);
        log.info("【websocket消息】收到客户端消息:" + session.getPathParameters().get("userId") + "  " + message);
    }

    /**
     * 发送错误时的处理
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误, 原因: {}", error.getMessage());
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
        log.info("【websocket消息】广播消息:" + message);
        for (WebSocketServer webSocket : webSockets) {
            try {
                if (webSocket.session.isOpen()) {
                    webSocket.session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("exception message", e);
            }
        }
    }

    // 此为单点消息
    public void sendMessage(String userId, String message) {
        if (ObjectUtils.isEmpty(sessionPool.get(userId))) {
            log.info("【websocket消息】未建立webSocket连接: {}", userId);
            return;
        }
        synchronized (this) {
            try {
                sessionPool.get(userId).forEach(session -> {
                    log.info("【websocket消息】 {} 单点消息: {}", userId, message);
                    session.getAsyncRemote().sendText(message);
                });
            } catch (Exception e) {
                log.error("exception message", e);
            }
        }
    }

}

