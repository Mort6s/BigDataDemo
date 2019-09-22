package org.opentsdb.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.opentsdb.client.builder.MetricBuilder;
import org.opentsdb.client.request.Query;
import org.opentsdb.client.request.QueryBuilder;
import org.opentsdb.client.request.SubQueries;
import org.opentsdb.client.response.Response;
import org.opentsdb.client.response.SimpleHttpResponse;
import org.opentsdb.client.util.Aggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Opentsdb读写工具类
 */
public class OpentsdbClient {
    private static Logger log = LoggerFactory.getLogger(OpentsdbClient.class);
    /**
     * 取平均值的聚合器
     */
    public static String AGGREGATOR_AVG = "avg";
    /**
     * 取累加值的聚合器
     */
    public static String AGGREGATOR_SUM = "sum";
    private HttpClient httpClient;

    public OpentsdbClient(String opentsdbUrl) {
        this.httpClient = new HttpClientImpl(opentsdbUrl);
    }

    /**
     * 写入数据
     *
     * @param metric    指标
     * @param timestamp 时间点
     * @param value
     * @param tagMap
     * @return
     * @throws Exception
     */
    public boolean putData(String metric, Date timestamp, long value, Map tagMap) throws Exception {
        long timsSecs = timestamp.getTime() / 1000;
        return this.putData(metric, timsSecs, value, tagMap);
    }

    /**
     * 写入数据
     *
     * @param metric    指标
     * @param timestamp 时间点
     * @param value
     * @param tagMap
     * @return
     * @throws Exception
     */
    public boolean putData(String metric, Date timestamp, double value, Map tagMap) throws Exception {
        long timsSecs = timestamp.getTime() / 1000;
        return this.putData(metric, timsSecs, value, tagMap);
    }

    /**
     * 写入数据
     *
     * @param metric    指标
     * @param timestamp 转化为秒的时间点
     * @param value
     * @param tagMap
     * @return
     * @throws Exception
     */
    public boolean putData(String metric, long timestamp, long value, Map tagMap) throws Exception {
        MetricBuilder builder = MetricBuilder.getInstance();
        builder.addMetric(metric).setDataPoint(timestamp, value).addTags(tagMap);
        try {
            log.debug("write quest：{}", builder.build());
            Response response = httpClient.pushMetrics(builder, ExpectResponse.SUMMARY);
            log.debug("response.statusCode: {}", response.getStatusCode());
            return response.isSuccess();
        } catch (Exception e) {
            log.error("put data to opentsdb error: ", e);
            throw e;
        }
    }

    /**
     * 写入数据
     *
     * @param metric    指标
     * @param timestamp 转化为秒的时间点
     * @param value
     * @param tagMap
     * @return
     * @throws Exception
     */
    public boolean putData(String metric, long timestamp, double value, Map tagMap) throws Exception {
        MetricBuilder builder = MetricBuilder.getInstance();
        builder.addMetric(metric).setDataPoint(timestamp, value).addTags(tagMap);
        try {
            log.debug("write quest：{}", builder.build());
            Response response = httpClient.pushMetrics(builder, ExpectResponse.SUMMARY);
            log.debug("response.statusCode: {}", response.getStatusCode());
            return response.isSuccess();
        } catch (Exception e) {
            log.error("put data to opentsdb error: ", e);
            throw e;
        }
    }

    /**
     * 批量写入数据
     *
     * @param jsonArr JSON数组：
     * metric    指标
     * timestamp 时间点
     * value
     * tagMap
     * @return
     * @throws Exception
     */
    public boolean putData(JSONArray jsonArr) throws Exception {
        return this.putDataBatch(jsonArr);
    }

    /**
     * 批量写入数据
     *
     * @param jsonArr JSON数组
     * metric    指标
     * timestamp 转化为秒的时间点
     * value
     * tagMap
     * @return
     * @throws Exception
     */
    public boolean putDataBatch(JSONArray jsonArr) throws Exception {
        MetricBuilder builder = MetricBuilder.getInstance();
        try {
            for (int i = 0; i < jsonArr.size(); i++) {
                Map tagMap = new HashMap();
                for (String key : jsonArr.getJSONObject(i).getJSONObject("tags").keySet()) {
                    tagMap.put(key, jsonArr.getJSONObject(i).getJSONObject("tags").get(key));
                }
                String metric = jsonArr.getJSONObject(i).getString("metric").toString();
                long timestamp = DateTimeUtil.parse(jsonArr.getJSONObject(i).getString("timestamp"), "yyyy/MM/dd HH:mm:ss").getTime() / 1000;
                double value = Double.valueOf(jsonArr.getJSONObject(i).getString("value"));
                builder.addMetric(metric).setDataPoint(timestamp, value).addTags(tagMap);
            }

            log.debug("write quest：{}", builder.build());
            Response response = httpClient.pushMetrics(builder, ExpectResponse.SUMMARY);
            log.debug("response.statusCode: {}", response.getStatusCode());
            return response.isSuccess();
        } catch (Exception e) {
            log.error("put data to opentsdb error: ", e);
            throw e;
        }
    }

    /**
     * 查询数据，返回的数据为json格式，结构为：
     * "[
     * " {
     * " metric: mysql.innodb.row_lock_time,
     * " tags: {
     * " host: web01,
     * " dc: beijing
     * " },
     * " aggregateTags: [],
     * " dps: {
     * " 1435716527: 1234,
     * " 1435716529: 2345
     * " }
     * " },
     * " {
     * " metric: mysql.innodb.row_lock_time,
     * " tags: {
     * " host: web02,
     * " dc: beijing
     * " },
     * " aggregateTags: [],
     * " dps: {
     * " 1435716627: 3456
     * " }
     * " }
     * "]";
     *
     * @param metric     要查询的指标
     * @param tagMap     查询的条件
     * @param aggregator 查询的聚合类型, 如: OpentsdbClient.AGGREGATOR_AVG, OpentsdbClient.AGGREGATOR_SUM
     * @param downsample 采样的时间粒度, 如: 1s,2m,1h,1d,2d
     * @param startTime  查询开始时间,时间格式为yyyy/MM/dd HH:mm:ss
     * @param endTime    查询结束时间,时间格式为yyyy/MM/dd HH:mm:ss
     */
    public String getData(String metric, Map tagMap, String aggregator, String downsample, String startTime, String endTime) throws IOException {
        QueryBuilder queryBuilder = QueryBuilder.getInstance();
        Query query = queryBuilder.getQuery();
        query.setStart(DateTimeUtil.parse(startTime, "yyyy/MM/dd HH:mm:ss").getTime() / 1000);
        query.setEnd(DateTimeUtil.parse(endTime, "yyyy/MM/dd HH:mm:ss").getTime() / 1000);
        List sqList = new ArrayList();
        SubQueries sq = new SubQueries();
        sq.addMetric(metric);
        sq.addTag(tagMap);
        sq.addAggregator(aggregator);
        sq.setDownsample(downsample + "-" + aggregator);
        sqList.add(sq);
        query.setQueries(sqList);
        try {
            log.debug("query request：{}", queryBuilder.build()); //这行起到校验作用
            SimpleHttpResponse spHttpResponse = httpClient.pushQueries(queryBuilder, ExpectResponse.DETAIL);
            log.debug("response.content: {}", spHttpResponse.getContent());
            if (spHttpResponse.isSuccess()) {
                return spHttpResponse.getContent();
            }
            return null;
        } catch (IOException e) {
            log.error("get data from opentsdb error: ", e);
            throw e;
        }
    }

    /**
     * 查询毫秒级数据，返回的数据为json格式，结构为：
     * "[
     * " {
     * " metric: mysql.innodb.row_lock_time,
     * " tags: {
     * " host: web01,
     * " dc: beijing
     * " },
     * " aggregateTags: [],
     * " dps: {
     * " 1435716527: 1234,
     * " 1435716529: 2345
     * " }
     * " },
     * " {
     * " metric: mysql.innodb.row_lock_time,
     * " tags: {
     * " host: web02,
     * " dc: beijing
     * " },
     * " aggregateTags: [],
     * " dps: {
     * " 1435716627: 3456
     * " }
     * " }
     * "]";
     *
     * @param metric     要查询的指标
     * @param tagMap     查询的条件
     * @param startTime  查询开始时间,时间格式为yyyy/MM/dd HH:mm:ss
     * @param endTime    查询结束时间,时间格式为yyyy/MM/dd HH:mm:ss
     */
    public String getMsData(String metric, Map tagMap, String startTime, String endTime) throws IOException {
        String downsample = "1ms";
        String aggregator = Aggregator.sum.toString();
        QueryBuilder queryBuilder = QueryBuilder.getInstance();
        Query query = queryBuilder.getQuery();
        query.setMsResolution(true);    // 返回的点的精度设置为毫秒级
        query.setStart(DateTimeUtil.parse(startTime, "yyyy/MM/dd HH:mm:ss").getTime());
        query.setEnd(DateTimeUtil.parse(endTime, "yyyy/MM/dd HH:mm:ss").getTime());
        List sqList = new ArrayList();
        SubQueries sq = new SubQueries();
        sq.addMetric(metric);
        sq.addTag(tagMap);
        sq.addAggregator(aggregator);
        sq.setDownsample(downsample + "-" + aggregator);
        sqList.add(sq);
        query.setQueries(sqList);
        try {
            log.debug("query request：{}", queryBuilder.build()); //这行起到校验作用
            SimpleHttpResponse spHttpResponse = httpClient.pushQueries(queryBuilder, ExpectResponse.DETAIL);
            log.debug("response.content: {}", spHttpResponse.getContent());
            if (spHttpResponse.isSuccess()) {
                return spHttpResponse.getContent();
            }
            return null;
        } catch (IOException e) {
            log.error("get data from opentsdb error: ", e);
            throw e;
        }
    }

    /**
     * 查询数据，返回tags与时序值的映射: Map>
     *
     * @param metric     要查询的指标
     * @param aggregator 查询的聚合类型, 如: OpentsdbClient.AGGREGATOR_AVG, OpentsdbClient.AGGREGATOR_SUM
     * @param tagMap     查询的条件
     * @param downsample 采样的时间粒度, 如: 1s,2m,1h,1d,2d
     * @param startTime  查询开始时间, 时间格式为yyyy/MM/dd HH:mm:ss
     * @param endTime    查询结束时间, 时间格式为yyyy/MM/dd HH:mm:ss
     * @param retTimeFmt 返回的结果集中，时间点的格式, 如：yyyy/MM/dd HH:mm:ss 或 yyyyMMddHH 等
     * @return Map>
     */
    public Map getData(String metric, Map tagMap, String aggregator, String downsample, String startTime, String endTime, String retTimeFmt) throws IOException {
        String resContent = this.getData(metric, tagMap, aggregator, downsample, startTime, endTime);
        return this.convertContentToMap(resContent, retTimeFmt);
    }

    public Map convertContentToMap(String resContent, String retTimeFmt) {
        Map tagsValuesMap = new HashMap();
        if (resContent == null || "".equals(resContent.trim())) {
            return tagsValuesMap;
        }
        JSONArray array = (JSONArray) JSONObject.parse(resContent);
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = (JSONObject) array.get(i);
                JSONObject tags = (JSONObject) obj.get("tags");
                JSONObject dps = (JSONObject) obj.get("dps"); //timeValueMap.putAll(dps); Map timeValueMap = new HashMap(); for (Iterator it = dps.keySet().iterator(); it.hasNext(); ) { String timstamp = it.next(); Date datetime = new Date(Long.parseLong(timstamp)*1000); timeValueMap.put(DateTimeUtil.format(datetime, retTimeFmt), dps.get(timstamp)); } tagsValuesMap.put(tags.toString(), timeValueMap); } } return tagsValuesMap; }
            }
        }
        return tagsValuesMap;
    }
}

