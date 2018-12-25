package com.mycompany.faceedge.facesyncservice.MQ;

import com.alibaba.fastjson.JSON;
import com.mycompany.faceedge.facesyncservice.FaceRecognition.FaceRecognitionService;
import com.mycompany.faceedge.facesyncservice.Order.Order;
import com.mycompany.faceedge.facesyncservice.Order.OrderService;
import org.apache.commons.codec.binary.Base64;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class ExitConsumer implements MessageListenerConcurrently{

    @Value("${FaceCloud.MQNameServer}")
    private String mqNameServer;

    @Value("${FaceCloud.ExitConsumerGroupName}")
    private String consumerGroupName;


    @Value("${FaceCloud.DeploymentArch}")
    private String deploymentArch;

    @Value("${Nginx.ImageDirExit}")
    private String imageDirExit;



    @Value("${FaceRecognition.single_exit_groupid}")
    private String groupID;


    @Autowired
    private OrderService orderService;

    @Autowired
    private FaceRecognitionService faceRecognitionService;


    private DefaultMQPushConsumer consumer;
    private String topic = "";


    @PostConstruct
    public void init() {
        consumer = new DefaultMQPushConsumer(consumerGroupName);


        consumer.setNamesrvAddr(mqNameServer);



        try {


            if (deploymentArch.equals("ACC")) {
                topic = "ACC_EXIT";
            }
            consumer.subscribe(topic, "*");

            consumer.registerMessageListener(this);

            System.out.println("消费者（出站）开始运行 ...");
            consumer.start();

            System.out.println("消费者（出站）正在运行 ...");

        } catch (MQClientException e) {
            e.printStackTrace();
        }


    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);

        for (MessageExt msg : msgs) {
            if (msg.getTopic().equals(topic)) {



                Order order = (Order) JSON.parseObject(msg.getBody(), Order.class);

                // ACC部署架构，本地会存在已进站订单

                if (orderService.isOrderExist(order)) {
                        // 幂等性检查，订单存在
                    // 丢弃
                    System.out.println("出站消费者：订单已存在" + order.getOrderID() + ", 订单状态" + order.getStatus());


//是不是拆成两张订单表比较合适， 进站订单表， 出站订单表

                    // 保存文件图像到nginx
                    byte[] image = Base64.decodeBase64(order.getImage());
                    String filename = order.getTenantID() + "_" + order.getUserType() + "_" + order.getUserID() + ".jpg";
                    Path path = Paths.get(imageDirExit + System.getProperty("file.separator") + filename);
                    try {

                        Files.write(path, image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    // 向人脸库注册人脸
                    if (faceRecognitionService.addFace(groupID, order.getOrderID(), order.getImage())) {
                        System.out.println("出站消费者：注册人脸成功" + groupID + ", " + order.getOrderID());
                    } else {
                        System.out.println("出站消费者：注册人脸失败" + groupID + ", " + order.getOrderID());
                    }
                } else {
                    // 新增订单
                    if (orderService.insert(order) == 1) {
                        System.out.println("出站消费者：新增出站订单成功" + order.getOrderID());
                    } else {
                        System.out.println("出站消费者：新增出站订单失败" + order.getOrderID());
                    }

                    // 保存文件图像到nginx
                    byte[] image = Base64.decodeBase64(order.getImage());
                    String filename = order.getTenantID() + "_" + order.getUserType() + "_" + order.getUserID() + ".jpg";
                    Path path = Paths.get(imageDirExit + System.getProperty("file.separator") + filename);
                    try {

                        Files.write(path, image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    // 向人脸库注册人脸
                    if (faceRecognitionService.addFace(groupID, order.getOrderID(), order.getImage())) {
                        System.out.println("出站消费者：注册人脸成功" + groupID + ", " + order.getOrderID());
                    } else {
                        System.out.println("出站消费者：注册人脸失败" + groupID + ", " + order.getOrderID());
                    }
                }
            } else {
                System.out.println("出站消费者：topic不相符");
            }
        }//end for

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    @PreDestroy
    public void stop() {
        if (consumer != null) {
            consumer.shutdown();
            System.out.println("消费者（出站）退出");
        }
    }
}
