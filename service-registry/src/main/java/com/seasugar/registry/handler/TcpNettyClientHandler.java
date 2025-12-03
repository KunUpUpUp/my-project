package com.seasugar.registry.handler;

import com.seasugar.registry.coder.TcpMsgDecoder;
import com.seasugar.registry.coder.TcpMsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import com.seasugar.registry.coder.TcpMsg;
import com.seasugar.registry.constants.RegistryConstants;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class TcpNettyClientHandler extends SimpleChannelInboundHandler<TcpMsg> {

    private Channel channel;
    private final ConcurrentHashMap<Integer, ResponseCallback> pendingRequests = new ConcurrentHashMap<>();
    private int requestId = 0;

    // 响应回调接口
    public interface ResponseCallback {
        void onResponse(TcpMsg response);

        void onError(Throwable cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpMsg msg) {
        // 处理服务器响应
        System.out.println("Received response: " + new String(msg.getBody()));

        // 根据消息代码处理不同类型的响应
        switch (msg.getCode()) {
            case RegistryConstants.SERVICE_REGISTER_RESPONSE:
                handleRegisterResponse(msg);
                break;
            case RegistryConstants.SERVICE_DISCOVER_RESPONSE:
                handleDiscoverResponse(msg);
                break;
            case RegistryConstants.HEARTBEAT_RESPONSE:
                handleHeartbeatResponse(msg);
                break;
            default:
                System.out.println("Unknown response type: " + msg.getCode());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
        System.out.println("Client connected to server");

        // 连接建立后可以发送初始化消息
        // sendHeartbeat(); // 示例：发送心跳
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("Client disconnected from server");
        this.channel = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Client exception: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    // ========== 写操作方法 ==========

    /**
     * 发送服务注册请求
     */
    public void registerService(String serviceName, String serviceInfo) {
        String requestBody = serviceName + ":" + serviceInfo;
        TcpMsg request = createRequest(RegistryConstants.SERVICE_REGISTER_REQUEST, requestBody);
        sendRequest(request);
    }

    /**
     * 发送服务发现请求
     */
    public void discoverService(String serviceName, ResponseCallback callback) {
        String requestBody = serviceName;
        TcpMsg request = createRequest(RegistryConstants.SERVICE_DISCOVER_REQUEST, requestBody);
        sendRequestWithCallback(request, callback);
    }

    /**
     * 发送心跳
     */
    public void sendHeartbeat() {
        TcpMsg heartbeat = createRequest(RegistryConstants.HEARTBEAT_REQUEST, "heartbeat");
        sendRequest(heartbeat);
    }

    /**
     * 通用发送请求方法（无需回调）
     */
    public void sendRequest(TcpMsg request) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(request).addListener(future -> {
                if (!future.isSuccess()) {
                    System.err.println("Failed to send request: " + future.cause().getMessage());
                }
            });
        } else {
            System.err.println("Channel is not active, cannot send request");
        }
    }

    /**
     * 发送请求并等待响应（同步方式）
     */
    public TcpMsg sendRequestSync(TcpMsg request, long timeoutMs) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TcpMsg[] responseHolder = new TcpMsg[1];

        ResponseCallback callback = new ResponseCallback() {
            @Override
            public void onResponse(TcpMsg response) {
                responseHolder[0] = response;
                latch.countDown();
            }

            @Override
            public void onError(Throwable cause) {
                latch.countDown();
            }
        };

        sendRequestWithCallback(request, callback);

        if (latch.await(timeoutMs, TimeUnit.MILLISECONDS)) {
            return responseHolder[0];
        } else {
            throw new RuntimeException("Request timeout");
        }
    }

    /**
     * 通用发送请求方法（带回调）
     */
    private void sendRequestWithCallback(TcpMsg request, ResponseCallback callback) {
        int currentRequestId = ++requestId;
        pendingRequests.put(currentRequestId, callback);

        sendRequest(request);

        // 设置超时清理
        scheduleTimeoutCleanup(currentRequestId, 5000); // 5秒超时
    }

    // ========== 辅助方法 ==========

    /**
     * 创建请求消息
     */
    private TcpMsg createRequest(int code, String body) {
        TcpMsg request = new TcpMsg();
        request.setMagic(RegistryConstants.MAGIC);
        request.setCode(code);
        request.setLength(body.getBytes().length);
        request.setBody(body.getBytes());
        return request;
    }

    /**
     * 处理注册响应
     */
    private void handleRegisterResponse(TcpMsg response) {
        String responseBody = new String(response.getBody());
        System.out.println("Register response: " + responseBody);
    }

    /**
     * 处理发现响应
     */
    private void handleDiscoverResponse(TcpMsg response) {
        String responseBody = new String(response.getBody());
        System.out.println("Discover response: " + responseBody);

        // 通知等待的回调
        ResponseCallback callback = pendingRequests.remove(response.getCode());
        if (callback != null) {
            callback.onResponse(response);
        }
    }

    /**
     * 处理心跳响应
     */
    private void handleHeartbeatResponse(TcpMsg response) {
        System.out.println("Heartbeat response received");
    }

    /**
     * 安排超时清理
     */
    private void scheduleTimeoutCleanup(int requestId, long delayMs) {
        // 这里可以使用定时任务来清理超时的请求
        // 简化实现，实际项目中建议使用 ScheduledExecutorService
        pendingRequests.remove(requestId);
    }

    /**
     * 检查连接是否活跃
     */
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
    }

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new TcpMsgDecoder());
                            ch.pipeline().addLast(new TcpMsgEncoder());
                            ch.pipeline().addLast(new TcpNettyClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
