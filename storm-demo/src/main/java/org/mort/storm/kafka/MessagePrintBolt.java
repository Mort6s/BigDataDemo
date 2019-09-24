package org.mort.storm.kafka;

import com.alibaba.fastjson.JSONObject;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.mort.storm.model.MessageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于打印输出MessageData的Bolt
 */
public class MessagePrintBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(PrinterBolt.class);

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        // get message data from the tuple and print it
        MessageData data = JSONObject.parseObject(input.getString(0), MessageData.class);

        String mess = "versionCode="+data.getVersionCode()+"\tdestination="+data.getDestination()+"\treturnData="+data.getReturnData();

        logger.info("Received message: " + mess);
        System.out.println("Received message: " + mess);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // we don't emit anything
    }
}
