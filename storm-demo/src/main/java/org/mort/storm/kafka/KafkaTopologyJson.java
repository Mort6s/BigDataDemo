package org.mort.storm.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.*;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.storm.kafka.spout.KafkaSpoutConfig.FirstPollOffsetStrategy.LATEST;

/**
 * 使用Storm消费Kafka数据（Json格式：MessageData），构建Storm拓扑（使用TopologyBuilder）
 * 实现SentenceBolt、PrinterBolt
 */
public class KafkaTopologyJson {

    /**
     * JUST_VALUE_FUNC为kafka消息翻译函数
     * 此处简单的将其输出
     */
    private static Func<ConsumerRecord<String, String>, List<Object>> JUST_VALUE_FUNC = new Func<ConsumerRecord<String, String>, List<Object>>() {
        @Override
        public List<Object> apply(ConsumerRecord<String, String> record) {
            return new Values(record.value());
        }
    };

    /**
     * KafkaSpout重试策略
     * @return
     */
    protected KafkaSpoutRetryService newRetryService() {
        return new KafkaSpoutRetryExponentialBackoff(new TimeInterval(500L, TimeUnit.MICROSECONDS), TimeInterval.milliSeconds(2),
                Integer.MAX_VALUE, TimeInterval.seconds(10));
    }

    /**
     * KafkaSpout配置
     * 新版本的KafkaSpout通过KafkaSpoutConfig类进行配置，KafkaSpoutConfig定义了kafka相关的环境、主题、重试策略、消费的初始偏移量等等参数。
     * @return
     */
    protected KafkaSpoutConfig<String, String> newKafkaSpoutConfig() {
        return KafkaSpoutConfig.builder("192.168.1.201:9092", "first").setProp(ConsumerConfig.GROUP_ID_CONFIG, "kafkaSpoutTestGroup")
                .setProp(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 200).setRecordTranslator(JUST_VALUE_FUNC, new Fields("str"))
                .setRetry(newRetryService()).setOffsetCommitPeriodMs(10000).setFirstPollOffsetStrategy(LATEST)
                .setMaxUncommittedOffsets(250).build();
    }

    /**
     * 将上述bolt和spout以及配置类组合，配置topology
     * 构建Storm拓扑（使用TopologyBuilder）
     * @return
     */
    public StormTopology buildTopology() {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("KafkaSpout", new KafkaSpout<String, String>(newKafkaSpoutConfig()), 1);
        //builder.setBolt("SentenceBolt", new SentenceBolt(), 1).globalGrouping("KafkaSpout");
        builder.setBolt("MessagePrintBolt", new MessagePrintBolt(), 1).globalGrouping("KafkaSpout");
        return builder.createTopology();
    }

    public static void main(String[] args) {
        // 1 创建拓扑
        KafkaTopologyJson kb = new KafkaTopologyJson();
        StormTopology topology = kb.buildTopology();

        // 2 创建配置信息对象
        Config conf = new Config();
        // 配置Worker开启个数
        conf.setNumWorkers(4);

        // 3 提交程序
        if(args != null && args.length > 0){
            try {
                // 分布式提交
                StormSubmitter.submitTopology(args[0], conf, topology);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            // 本地提交
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("KafkaToplogy", conf, topology);
            try {
                // Wait for some time before exiting
                System.out.println("Waiting to consume from kafka");
                Thread.sleep(300000);
            } catch (Exception exception) {
                System.out.println("Thread interrupted exception : " + exception);
            }
            // kill the KafkaTopology
            cluster.killTopology("KafkaToplogy");
            // shut down the storm test cluster
            cluster.shutdown();
        }
    }
}
