package io.iabc.tsdb.opentsdb.client.impl;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.iabc.tsdb.opentsdb.client.Tsdb;
import io.iabc.tsdb.opentsdb.client.TsdbException;
import io.iabc.tsdb.opentsdb.client.TsdbService;
import io.iabc.tsdb.opentsdb.client.domain.Callback;
import io.iabc.tsdb.opentsdb.client.domain.MultiPoint;
import io.iabc.tsdb.opentsdb.client.domain.Point;
import io.iabc.tsdb.opentsdb.client.domain.TsdbQuery;
import io.iabc.tsdb.opentsdb.client.domain.TsdbQueryResult;
import io.iabc.tsdb.opentsdb.client.domain.TsdbWriteResult;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-06 13:56
 */
public class HttpBasedTsdbImpl implements Tsdb {
    ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private final static Logger logger = LoggerFactory.getLogger(HttpBasedTsdbImpl.class);

    //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private TsdbService tsdbService;
    /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public HttpBasedTsdbImpl(TsdbService tsdbService) {
        this.tsdbService = tsdbService;
    }

    ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //------------------------ Implements: 

    @Override
    public void write(Point point, Callback<TsdbWriteResult> callback) {
        MultiPoint multiPoint = MultiPoint.getBuilder().point(point).build();
        this.write(multiPoint, callback);
    }

    @Override
    public void write(Point[] points, Callback<TsdbWriteResult> callback) {
        MultiPoint multiPoint = MultiPoint.getBuilder().points(points).build();
        this.write(multiPoint, callback);
    }

    @Override
    public void write(MultiPoint multiPoint, Callback<TsdbWriteResult> callback) {
        TypedString jsonProtocol = new TypedString(multiPoint.jsonProtocol());
        this.tsdbService.writePoints(jsonProtocol, new retrofit.Callback<TsdbWriteResult>() {
            @Override
            public void success(TsdbWriteResult writeResult, Response response) {
                callback.success(writeResult);
            }

            @Override
            public void failure(RetrofitError error) {
                logger.error("write tsdb error with reason:{}", error);
                TsdbWriteResult result = new TsdbWriteResult();
                if (error.getCause() instanceof TsdbException) {
                    TsdbException e = (TsdbException) error.getCause();
                    result.setStatus(e.getStatus());
                    result.setReason(e.getMsg());
                    callback.failure(result);
                    return;
                }
            }
        });

    }

    @Override
    public TsdbWriteResult write(Point point) {
        MultiPoint multiPoint = MultiPoint.getBuilder().point(point).build();
        return this.write(multiPoint);
    }

    @Override
    public TsdbWriteResult write(MultiPoint multiPoint) {
        TypedString jsonProtocol = new TypedString(multiPoint.jsonProtocol());
        TsdbWriteResult writeResult = new TsdbWriteResult();
        String responseBody = null;
        try {
            Response response = this.tsdbService.writePoints(jsonProtocol);
            responseBody = this.getBodyAsJson(response);

            writeResult = JSON.parseObject(responseBody, TsdbWriteResult.class);
            writeResult.setStatus(response.getStatus());
            writeResult.setReason("ok");
        } catch (TsdbException e) {
            writeResult.setStatus(e.getStatus());
            writeResult.setReason(e.getMsg());
        } catch (Exception e) {
            writeResult.setStatus(213);
            if (responseBody != null) {
                writeResult.setReason("write points error,result is " + responseBody);
            } else {
                writeResult.setReason("write points error!");
                writeResult.setFailed(multiPoint.getPoints().size());
            }
        }

        return writeResult;
    }

    @Override
    public Response write(Point... points) {
        MultiPoint multiPoint = MultiPoint.getBuilder().points(points).build();
        return this.writeRaw(multiPoint);
    }

    @Override
    public Response writeRaw(MultiPoint multiPoint) {
        TypedString jsonProtocol = new TypedString(multiPoint.jsonProtocol());
        Response response = null;
        try {
            response = this.tsdbService.writePoints(jsonProtocol);
        } catch (TsdbException e) {
            logger.error("write points error with reason:{}", e.getMsg());
        } catch (Exception e) {
            logger.error("write points error with reason:{}", e.getMessage());
        }

        return response;
    }

    @Override
    public void query(TsdbQuery query, Callback<List<TsdbQueryResult>> callback) {
        this.query(query.jsonProtocol(), callback);
    }

    @Override
    public void query(String jsonQuery, Callback<List<TsdbQueryResult>> callback) {
        TypedString jsonProtocol = new TypedString(jsonQuery);
        this.tsdbService.query(jsonProtocol, new retrofit.Callback<List<TsdbQueryResult>>() {
            @Override
            public void success(List<TsdbQueryResult> queryResults, Response response) {
                callback.success(queryResults);
            }

            @Override
            public void failure(RetrofitError error) {
                logger.error("query tsdb error with reason:{}", error);
                List<TsdbQueryResult> results = new ArrayList<>();
                if (error.getCause() instanceof TsdbException) {
                    TsdbException e = (TsdbException) error.getCause();
                    //                    result.setStatus(e.getStatus());
                    //                    result.setReason(e.getMsg());
                    callback.failure(results);
                    return;
                }
            }
        });
    }

    @Override
    public List<TsdbQueryResult> query(TsdbQuery query) {
        return this.query(query.jsonProtocol());
    }

    @Override
    public List<TsdbQueryResult> query(String jsonQuery) {
        String responseBody = this.queryAsJsonString(jsonQuery);
        List<TsdbQueryResult> queryResult = JSON.parseArray(responseBody, TsdbQueryResult.class);
        return queryResult;
    }

    @Override
    public String queryAsJsonString(TsdbQuery query) {
        return this.queryAsJsonString(query.jsonProtocol());
    }

    @Override
    public String queryAsJsonString(String jsonQuery) {
        TypedString jsonProtocol = new TypedString(jsonQuery);
        Response response = this.tsdbService.query(jsonProtocol);
        String responseBody = this.getBodyAsJson(response);
        return responseBody;
    }

    //------------------------ Overrides:

    //---------------------------- Abstract Methods -----------------------------

    //---------------------------- Utility Methods ------------------------------

    //---------------------------- Property Methods -----------------------------
}
