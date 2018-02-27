package io.iabc.tsdb.opentsdb.client;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Dispatcher;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.internal.Util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-06 13:49
 */
public class TsdbConnectionFactory {
    private final static Logger logger = LoggerFactory.getLogger(TsdbConnectionFactory.class);
    private final static int DEFAULT_MAX_IDLE_CONNECTIONS = 30;
    private final static long DEFAULT_KEEP_ALIVE_DURATION_MS = 3600 * 1000;//1小时
    private final static long DEFAULT_CONNECT_TIMEOUT_MS = 3_000, DEFAULT_READ_TIMEOUT_MS = 2_000, DEFAULT_WRITE_TIMEOUT_MS = 2_000;

    private static Map<String, TsdbService> serviceMap = Maps.newConcurrentMap();

    public static TsdbService getConnection(final String url, String username, String password) {
        final String uniqueTsdbDBKey = url + username + password;
        //TODO 负载均衡 & failover
        TsdbService tsdbService = serviceMap.get(uniqueTsdbDBKey);
        if (tsdbService == null) {
            tsdbService = init(url, username, password);
            serviceMap.put(uniqueTsdbDBKey, tsdbService);
        }

        return tsdbService;
        //        return serviceMap.computeIfAbsent(uniqueTsdbDBKey, key -> init(url, username, password));
    }

    private static TsdbService init(String url, String username, String password) {
        final String credentials = username + ":" + password;
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient
            .setConnectionPool(new ConnectionPool(DEFAULT_MAX_IDLE_CONNECTIONS, DEFAULT_KEEP_ALIVE_DURATION_MS));
        okHttpClient.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(DEFAULT_READ_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(DEFAULT_WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        //        ExecutorService executorService = new ThreadPoolExecutor(30, 30, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5000));
        ExecutorService executorService = new ThreadPoolExecutor(30, 30, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<Runnable>(5000), Util.threadFactory("OkHttp Dispatcher", false));
        Dispatcher dispatcher = new Dispatcher(executorService);
        okHttpClient.setDispatcher(dispatcher);

        RestAdapter restAdapter = new RestAdapter.Builder()

            .setEndpoint(url).setLogLevel(RestAdapter.LogLevel.NONE).setLog(new RestAdapter.Log() {
                @Override
                public void log(String message) {
                    logger.debug(message);
                }
            }).setErrorHandler(new ErrorHandler() {
                @Override
                public Throwable handleError(RetrofitError cause) {
                    Response r = cause.getResponse();
                    if (r != null && r.getStatus() >= 400) {
                        try (InputStreamReader reader = new InputStreamReader(r.getBody().in(), Charsets.UTF_8)) {
                            String msg = CharStreams.toString(reader);
                            return new TsdbException(r.getStatus(), msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return cause;
                }
            }).setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    // create Base64 encodet string
                    String string = "Basic " + DatatypeConverter.printBase64Binary(credentials.getBytes());
                    request.addHeader("Accept", "application/json");
                    request.addHeader("Authorization", string);
                }
            }).setClient(new OkClient(okHttpClient)

            ).build();

        TsdbService tsdbService = restAdapter.create(TsdbService.class);
        return tsdbService;
    }
}
