package org.mort.storm.kafka;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 写一组将输入的单词拼成一句话的bolt类，每行输入一个单词，当输入符号“.”时，视为一句话结束。
 */
public class SentenceBolt extends BaseBasicBolt {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(SentenceBolt.class);

    private List<String> words = new ArrayList<String>();

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        // Get the word from the tuple
        String word = input.getString(0);
        if (StringUtils.isBlank(word)) {
            // ignore blank lines
            return;
        }
        logger.info("Received Word:" + word);
        System.out.println("Received Word:" + word);
        // add word to current list of words
        words.add(word);
        if (word.endsWith(".")) {
            // word ends with '.' which means this is the end
            // the SentenceBolt publishes a sentence tuple
            collector.emit(new Values(StringUtils.join(words, ' ')));
            // and reset the words list.
            words.clear();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("sentence"));
    }

}
