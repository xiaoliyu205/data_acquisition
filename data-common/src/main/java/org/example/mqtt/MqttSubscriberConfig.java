//package org.example.mqtt;
//
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.IntegrationComponentScan;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.core.MessageProducer;
//import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
//import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
//import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
//import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
//import org.springframework.messaging.MessageChannel;
//
///**
// * @author: xiaoliyu
// * @description: TODO
// * @dateTime: 2024/10/15 17:49
// **/
//@Slf4j
//@Configuration
//@IntegrationComponentScan
//@Data
//public class MqttSubscriberConfig {
//    /**
//     * 订阅的bean名称
//     */
//    public static final String CHANNEL_NAME_IN = "mqttInboundChannel";
//
//    // 客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
//    private static final byte[] WILL_DATA;
//
//    static {
//        WILL_DATA = "offline".getBytes();
//    }
//
//    @Value("${mqtt.username}")
//    private String username;
//
//    @Value("${mqtt.password}")
//    private String password;
//
//    @Value("${mqtt.serverURIs}")
//    private String hostUrl;
//
//    @Value("${mqtt.client.id}")
//    private String clientId;
//
//    @Value("${mqtt.topic}")
//    private String defaultTopic;
//
//    /**
//     * MQTT连接器选项
//     */
//    @Bean
//    public MqttConnectOptions getReceiverMqttConnectOptions() {
//        MqttConnectOptions options = new MqttConnectOptions();
//        // 设置连接的用户名
//        if (!username.trim().isEmpty()) {
//            options.setUserName(username);
//        }
//        // 设置连接的密码
//        options.setPassword(password.toCharArray());
//        // 设置连接的地址
//        options.setServerURIs(new String[]{hostUrl});
//        // 设置超时时间 单位为秒
//        options.setConnectionTimeout(10);
//        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
//        // 但这个方法并没有重连的机制
//        options.setKeepAliveInterval(20);
//        return options;
//    }
//
//    /**
//     * MQTT客户端
//     */
//    @Bean
//    public MqttPahoClientFactory receiverMqttClientFactory() {
//        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//        factory.setConnectionOptions(getReceiverMqttConnectOptions());
//        return factory;
//    }
//
//    /**
//     * MQTT信息通道（消费者）
//     */
//    @Bean(name = CHANNEL_NAME_IN)
//    public MessageChannel mqttInboundChannel() {
//        return new DirectChannel();
//    }
//
//
//    /**
//     * MQTT消息订阅绑定（消费者）
//     */
//    @Bean
//    public MessageProducer inbound() {
//        // 可以同时消费（订阅）多个Topic
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter(clientId, receiverMqttClientFactory(),
//                        "DataPoint-Read/#");
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(2);
//        // 设置订阅通道
//        adapter.setOutputChannel(mqttInboundChannel());
//        return adapter;
//    }
//}
