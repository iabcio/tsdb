package io.iabc.tsdb.opentsdb.client;

import java.util.List;

import io.iabc.tsdb.opentsdb.client.domain.TsdbQueryResult;
import io.iabc.tsdb.opentsdb.client.domain.TsdbWriteResult;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedString;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-05 14:02
 */
public interface TsdbService {

    @Headers("Content-Type: application/json")
    @POST("/api/put?summary")
    Response writePoints(@Body TypedString points);

    @Headers("Content-Type: application/json")
    @POST("/api/put?summary")
    void writePoints(@Body TypedString points, retrofit.Callback<TsdbWriteResult> callback);

    @Headers("Content-Type: application/json")
    @POST("/api/query")
    Response query(@Body TypedString query);

    @Headers("Content-Type: application/json")
    @POST("/api/query")
    void query(@Body TypedString points, retrofit.Callback<List<TsdbQueryResult>> callback);

}
