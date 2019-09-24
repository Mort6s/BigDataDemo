package org.mort.storm.kafka;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于打印输出Sentence的Bolt
 */
public class PrinterBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(PrinterBolt.class);

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        // get the sentence from the tuple and print it
        String sentence = input.getString(0);
        logger.info("Received Sentence: " + sentence);
        System.out.println("Received Sentence: " + sentence);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // we don't emit anything
    }
}
