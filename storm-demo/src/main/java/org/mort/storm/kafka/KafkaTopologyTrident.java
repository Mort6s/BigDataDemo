package org.mort.storm.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.Func;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.trident.KafkaTridentSpoutOpaque;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.testing.Split;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.storm.kafka.spout.KafkaSpoutConfig.FirstPollOffsetStrategy.EARLIEST;

/**
 * 使用Storm消费Kafka数据，构建Storm拓扑（使用TridentTopology）
 * 实现SentenceBolt、PrinterBolt
 */
public class KafkaTopologyTrident {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTopologyTrident.class);

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
        return KafkaSpoutConfig.builder("192.168.1.201:9092", "test").setProp(ConsumerConfig.GROUP_ID_CONFIG, "kafkaSpoutTestGroup")
                .setProp(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 200).setRecordTranslator(JUST_VALUE_FUNC, new Fields("str"))
                .setRetry(newRetryService()).setOffsetCommitPeriodMs(10000).setFirstPollOffsetStrategy(EARLIEST)
                .setMaxUncommittedOffsets(250).build();
    }

    /**
     * 创建KafkaSpout
     * @return
     */
    private KafkaTridentSpoutOpaque<String, String> newKafkaTridentSpoutOpaque() {
        return new KafkaTridentSpoutOpaque<String, String>(newKafkaSpoutConfig());
    }

    /**
     * 将上述bolt和spout以及配置类组合，配置topology
     * 构建Storm拓扑（使用TridentTopology）
     * @return
     */
    public StormTopology buildTopology() {
        TridentTopology tridentTopology = new TridentTopology();
        final Stream spoutStream = tridentTopology.newStream("spout1", newKafkaTridentSpoutOpaque()).parallelismHint(2);
        final Stream countStream = spoutStream.each(new Fields("str"), new Split(), new Fields("word"));
        return tridentTopology.build();
    }

    public static void main(String[] args) {
        KafkaTopologyTrident topoM = new KafkaTopologyTrident();
        StormTopology topology = topoM.buildTopology();

    }
}
