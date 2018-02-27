package io.iabc.tsdb.opentsdb.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.iabc.tsdb.opentsdb.client.domain.Callback;
import io.iabc.tsdb.opentsdb.client.domain.MultiPoint;
import io.iabc.tsdb.opentsdb.client.domain.Point;
import io.iabc.tsdb.opentsdb.client.domain.TsdbQuery;
import io.iabc.tsdb.opentsdb.client.domain.TsdbQueryResult;
import io.iabc.tsdb.opentsdb.client.domain.TsdbWriteResult;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-27 13:05
 */
public class Demo {
    private final static Logger logger = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        writeTest();
        queryTest();
    }

    private static void writeTest() {
        final String url = "http://tsdb.iabc.io:4242";
        Tsdb tsdb = TsdbFactory.getTsdb(url);
        Point point = Point.metric("dubbo.invoke.success").timestamp(1517992834).value(13)
            .tag("application", "hrs-std-service").tag("invokeType", "provider").tag("consumer", "192.168.3.45")
            .tag("provider", "192.168.1.64").tag("service", "io.iabc.hrs.std.rpc.share.ExpertRpcService")
            .tag("method", "list").build();
        TsdbWriteResult result = tsdb.write(point);
        System.out.println(result.toString());
    }

    private static void writeMultiTest() {
        final String url = "http://tsdb.iabc.io:4242";
        Tsdb tsdb = TsdbFactory.getTsdb(url);
        MultiPoint multiPoint = MultiPoint.getBuilder().timestamp(1517992834).field("dubbo.invoke.success", 10)
            .field("dubbo.invoke.failure", 2).tag("application", "hrs-std-service").tag("invokeType", "provider")
            .tag("consumer", "192.168.3.45").tag("provider", "192.168.1.64")
            .tag("service", "io.iabc.hrs.std.rpc.share.ExpertRpcService").tag("method", "list").build();

        TsdbWriteResult result = tsdb.write(multiPoint);
        System.out.println(result.toString());
    }

    private static void queryTest() {
        final String url = "http://tsdb.iabc.io:4242";
        Tsdb tsdb = TsdbFactory.getTsdb(url);
        //        Point point = Point.metric("dubbo.invoke.success").timestamp(1517992834).value(13)
        //            .tag("application", "hrs-std-service").tag("invokeType", "provider").tag("consumer", "192.168.3.43")
        //            .tag("provider", "192.168.1.64").tag("serviceName", "io.iabc.hrs.std.rpc.share.ExpertRpcService")
        //            .tag("method", "list").build();

        TsdbQuery.Query query = TsdbQuery.Query.metric("dubbo.invoke.success").aggregator("sum")
            .downsample("5m-sum-zero").filter(TsdbQuery.Filter.iliteralOr("invokeType", "provider").build())
            .filter(TsdbQuery.Filter.iliteralOr("application", "hrs-std-service").build()).build();

        TsdbQuery tsdbQuery = TsdbQuery.range("1517992800", "1517992835").useCalendar(false).query(query).build();
        System.out.println(tsdbQuery.jsonProtocol());

        String result = tsdb.queryAsJsonString(tsdbQuery);
        System.out.println(result);

        List<TsdbQueryResult> result2 = tsdb.query(tsdbQuery);
        System.out.println(result2.toString());

        Callback<List<TsdbQueryResult>> callback = new Callback<List<TsdbQueryResult>>() {
            @Override
            public void success(List<TsdbQueryResult> queryResults) {
                System.out.println(queryResults);
            }

            @Override
            public void failure(List<TsdbQueryResult> error) {
                System.out.println(error);
            }
        };
        tsdb.query(tsdbQuery, callback);
        System.out.println("wait for result:");
        int i = 10;
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        } while (--i > 0);
    }
}
