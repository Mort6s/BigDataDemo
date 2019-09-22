package org.opentsdb.client;

import org.opentsdb.client.util.Aggregator;

import java.util.HashMap;
import java.util.Map;

public class OpentsdbClientTest {
    public static void main(String[] args) {
        try {
            OpentsdbClient client = new OpentsdbClient("http://192.168.1.201:4242");
            Map<String, String> tagMap = new HashMap<String, String>();
            tagMap.put("tag1", "test");
            tagMap.put("tag2", "mort");

            // 获取当前时间的Unix时间戳
            long millisecond = System.currentTimeMillis();
            long millisecond2 = DateTimeUtil.stringLongToMillisecond("2019/09/05 17:28:29:879");
            long millisecond3 = DateTimeUtil.stringLongToMillisecond("2019/09/05 17:28:29:880");
            System.out.println(millisecond + "\t" + millisecond2 + "\t" + millisecond3);

            //插入测试
//            //client.putData("metric1",millisecond,20,tagMap);
//            client.putData("metric1",millisecond2,21,tagMap);
//            client.putData("metric1",millisecond3,22,tagMap);
//            System.out.println("插入成功！");

            //读取测试
            String agg = Aggregator.zimsum.toString();
            String out = client.getData("metric1", tagMap, agg, "1s", "2019/09/05 17:20:28", "2019/09/05 17:30:35");
            String msout = client.getMsData("metric1", tagMap, "2019/09/05 17:20:28", "2019/09/05 17:30:35");
            System.out.println("秒级查询结果：" + out);
            System.out.println("毫秒级查询结果：" + msout);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
