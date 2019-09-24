package org.mort.storm;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordsUperBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(WordsUperBolt.class);
    
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        // 先获取到上一个组件传递过来的数据,数据在tuple里面
        String godName = input.getString(0);

        // 将商品名转换成大写
        String godName_upper = godName.toUpperCase();

        // 将转换完成的商品名发送出去
        collector.emit(new Values(godName_upper));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("uppername"));
    }

}
