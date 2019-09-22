package com.mort.kafka.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class CallBackProducer {

    public static void main(String[] args) {
        // 1.配置属性值
        Properties props = new Properties();
        // Kafka服务端的主机名和端口号
        props.put("bootstrap.servers", "hadoop201:9092");
        // 等待所有副本节点应答后再发送
        props.put("acks", "all");
        // 消息发送最大尝试次数
        props.put("retries", 0);
        // 一批消息处理大小，无作用可删掉
        props.put("batch.size", 16384);
        // 请求延时
        props.put("linger.ms", 1);
        // 发送缓存区内存大小
        props.put("buffer.memory", 33554432);
        // key序列化
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // 自定义分区
        props.put("partitioner.class", "com.mort.kafka.producer.CustomPartition");

        // 2.定义kafka生产者
        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        // 3.发送消息
        for (int i = 0; i < 50; i++) {
            // 参数1：发送消息到哪个topic
            // 参数2：发送消息的key（可省略）
            // 参数3：发送消息的value
            producer.send(new ProducerRecord<String, String>("first", Integer.toString(i), "callback first-" + i), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if(recordMetadata != null){
                        System.out.println(recordMetadata.offset()+"\t"+recordMetadata.partition());
                    }
                }
            });
        }

        // 4.关闭资源
        producer.close();
    }
}
