package com.mycompany.faceedge.facesyncservice.MQ;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mycompany.faceedge.facesyncservice.FaceRecognition.FaceRecognitionService;
import com.mycompany.faceedge.facesyncservice.Order.Order;
import com.mycompany.faceedge.facesyncservice.Order.OrderService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Component
public class Consumer implements MessageListenerConcurrently{

    @Value("${FaceCloud.MQNameServer}")
    private String mqNameServer;

    @Autowired
    OrderService orderService;

    @Autowired
    FaceRecognitionService faceRecognitionService;

    DefaultMQPushConsumer consumer;


    @PostConstruct
    public void init() {
        consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");


        consumer.setNamesrvAddr(mqNameServer);

        try {
            consumer.subscribe("StationEnter_1", "*");

            consumer.registerMessageListener(this);

            System.out.println("consumer begin starting...");
            consumer.start();

            System.out.printf("Consumer is running.");

        } catch (MQClientException e) {
            e.printStackTrace();
        }


    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);

        for (MessageExt msg : msgs) {
            if (msg.getTopic().equals("StationEnter_1")) {



                Order order = (Order) JSON.parseObject(msg.getBody(), Order.class);

                if (orderService.isOrderExist(order)) {
                        // 幂等性检查，订单存在
                    // 丢弃
                } else {
                    if (orderService.insert(order) == 1) {
                        System.out.println("注册人脸：成功");
                    } else {
                        System.out.println("注册人脸：成功");
                    }

                    // 向人脸库注册人脸
                    if (faceRecognitionService.addFace(order.getOrderID(), order.getImage())) {
                        System.out.println("注册人脸：成功");
                    } else {
                        System.out.println("注册人脸：失败");
                    }
                }




            }
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    @PreDestroy
    public void stop() {
        if (consumer != null) {
            consumer.shutdown();
            System.out.println("consumer shutdown");
        }
    }
}
