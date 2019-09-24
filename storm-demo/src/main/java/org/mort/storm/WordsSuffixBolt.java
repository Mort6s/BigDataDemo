package org.mort.storm;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class WordsSuffixBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(WordsSuffixBolt.class);
    
    private FileWriter fileWriter = null;
    
  //在bolt组件运行过程中只会被调用一次
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        try {
            fileWriter = new FileWriter("D:\\develop\\apache-storm-1.2.2\\storm_data\\"+UUID.randomUUID());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    
    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        //先拿到上一个组件发送过来的商品名称
        String upper_name = input.getString(0);
        //为上一个组件发送过来的商品名称添加后缀
        String suffix_name = upper_name + "_itisok";
        try {
            fileWriter.write(suffix_name);
            fileWriter.write("\n");
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        
    }

}
