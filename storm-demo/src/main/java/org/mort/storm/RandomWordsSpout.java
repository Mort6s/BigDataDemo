package org.mort.storm;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;

public class RandomWordsSpout extends BaseRichSpout {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(RandomWordsSpout.class);

    private SpoutOutputCollector collector;

    // 模拟一些数据
    String[] words = { "iphone", "xiaomi", "mate", "sony", "sumsung", "moto", "meizu" };

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        // 可以从kafka消息队列中拿到数据,简便起见，我们从words数组中随机挑选一个商品名发送出去
        Random random = new Random();
        int index = random.nextInt(words.length);

        // 通过随机数拿到一个商品名
        String godName = words[index];

        // 将商品名封装成tuple，发送消息给下一个组件
        collector.emit(new Values(godName));

        // 每发送一个消息，休眠500ms
        Utils.sleep(500);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("orignname"));
    }

}
