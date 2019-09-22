package org.opentsdb.client;

import org.junit.Test;
import org.opentsdb.client.builder.MetricBuilder;
import org.opentsdb.client.response.Response;

import java.io.IOException;

public class HttpClientTest {

	@Test
	public void test_pushMetrics_DefaultRetries() {
		HttpClientImpl client = new HttpClientImpl("http://192.168.1.201:4242");

		MetricBuilder builder = MetricBuilder.getInstance();

		builder.addMetric("metric1").setDataPoint(30L)
				.addTag("tag1", "test").addTag("tag2", "mort");

//		builder.addMetric("metric2").setDataPoint(232.34)
//				.addTag("tag3", "tab3value");

		try {
			Response response = client.pushMetrics(builder,
					ExpectResponse.SUMMARY);
			System.out.println(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}