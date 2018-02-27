package io.iabc.tsdb.opentsdb.client;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import io.iabc.tsdb.opentsdb.client.domain.Callback;
import io.iabc.tsdb.opentsdb.client.domain.MultiPoint;
import io.iabc.tsdb.opentsdb.client.domain.Point;
import io.iabc.tsdb.opentsdb.client.domain.TsdbQuery;
import io.iabc.tsdb.opentsdb.client.domain.TsdbQueryResult;
import io.iabc.tsdb.opentsdb.client.domain.TsdbWriteResult;
import retrofit.client.Response;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-05 16:42
 */
public interface Tsdb {
    /**
     * 异步写入一个点数据到Tsdb
     *
     * @param database        数据库名称
     * @param retentionPolicy 数据保持策略
     * @param point           时间序列数据点
     * @param callback        获取写入结果的回调对象
     */
    void write(final Point point, final Callback<TsdbWriteResult> callback);

    /**
     * 异步写入多个点数据到Tsdb
     *
     * @param points   时间序列数据点数组
     * @param callback 获取写入结果的回调对象
     */
    void write(Point points[], final Callback<TsdbWriteResult> callback);

    /**
     * 异步写入多个点数据到Tsdb
     *
     * @param multiPoint 时间序列数据点集合
     * @param callback   获取写入结果的回调对象
     */
    void write(MultiPoint multiPoint, final Callback<TsdbWriteResult> callback);

    /**
     * 同步写入一个点数据到Tsdb
     *
     * @param point 时间序列数据点
     * @return 写入结果
     */
    TsdbWriteResult write(Point point);

    /**
     * 同步写入多个点数据到Tsdb
     *
     * @param multiPoint 时间序列数据点
     * @return 写入结果
     */
    TsdbWriteResult write(MultiPoint multiPoint);

    /**
     * 同步写入多个点数据到Tsdb
     *
     * @param points 时间序列数据点数组
     * @return 写入结果
     */
    Response write(Point... points);

    /**
     * 同步写入多个点数据到Tsdb
     *
     * @param multiPoint 时间序列数据点数组
     * @return 写入结果
     */
    Response writeRaw(MultiPoint multiPoint);

    /**
     * 异步查询时间序列数据
     *
     * @param query    查询对象
     * @param callback 查询结果回调对象
     */
    void query(final TsdbQuery query, Callback<List<TsdbQueryResult>> callback);

    /**
     * 异步查询时间序列数据
     *
     * @param jsonQuery 结构化查询体
     * @param callback  查询结果回调对象
     */
    void query(final String jsonQuery, Callback<List<TsdbQueryResult>> callback);

    /**
     * 同步查询时间序列数据
     *
     * @param query 查询对象
     * @return 查询结果
     */
    List<TsdbQueryResult> query(final TsdbQuery query);

    /**
     * 同步查询时间序列数据
     *
     * @param jsonQuery 结构化查询体
     * @return 查询结果
     */
    List<TsdbQueryResult> query(final String jsonQuery);

    /**
     * 同步查询时间序列数据
     *
     * @param query 查询对象
     * @return 查询结果
     */
    String queryAsJsonString(TsdbQuery query);

    /**
     * 同步查询时间序列数据
     *
     * @param jsonQuery 结构化查询体
     * @return 查询结果
     */
    String queryAsJsonString(String jsonQuery);

    default String getBodyAsJson(Response response) {
        String json = null;
        try (InputStreamReader reader = new InputStreamReader(response.getBody().in(), Charsets.UTF_8)) {
            json = CharStreams.toString(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

}
