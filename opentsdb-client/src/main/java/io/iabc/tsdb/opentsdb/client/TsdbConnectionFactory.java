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

import org.apache.commons.lang3.StringUtils;
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
    private final static String TSDB_CLIENT_MAX_IDLE_CONNECTIONS = "tsdb.client.max.idle.connections";
    private final static String TSDB_CLIENT_KEEP_ALIVE_DURATION = "tsdb.client.keep.alive.duration";
    private final static String TSDB_CLIENT_CONNECT_TIMEOUT = "tsdb.client.connect.timeout";
    private final static String TSDB_CLIENT_READ_TIMEOUT = "tsdb.client.read.timeout";
    private final static String TSDB_CLIENT_WRITE_TIMEOUT = "tsdb.client.write.timeout";
    private final static String TSDB_CLIENT_THREAD_POOL_CORE_POOL_SIZE = "tsdb.client.thread.pool.core.pool.size";
    private final static String TSDB_CLIENT_THREAD_POOL_MAX_POOL_SIZE = "tsdb.client.thread.pool.max.pool.size";
    private final static String TSDB_CLIENT_THREAD_POOL_KEEP_ALIVE_TIME = "tsdb.client.thread.pool.keep.alive.time";
    private final static String TSDB_CLIENT_THREAD_POOL_QUEUE_SIZE = "tsdb.client.thread.pool.queue.size";

    private final static int DEFAULT_MAX_IDLE_CONNECTIONS = 30;
    private final static long DEFAULT_KEEP_ALIVE_DURATION_MS = 600 * 1000;
    private final static long DEFAULT_CONNECT_TIMEOUT_MS = 6_000;
    private final static long DEFAULT_READ_TIMEOUT_MS = 6_000;
    private final static long DEFAULT_WRITE_TIMEOUT_MS = 6_000;
    private final static int DEFAULT_THREAD_POOL_CORE_POOL_SIZE = 50;
    private final static int DEFAULT_THREAD_POOL_MAX_POOL_SIZE = 50;
    private final static int DEFAULT_THREAD_POOL_KEEP_ALIVE_TIME = 0;
    private final static int DEFAULT_THREAD_POOL_QUEUE_SIZE = 5000;

    private static int maxIdleConnections = DEFAULT_MAX_IDLE_CONNECTIONS;
    private static long keepAliveDurationMs = DEFAULT_KEEP_ALIVE_DURATION_MS;
    private static long connectTimeoutMs = DEFAULT_CONNECT_TIMEOUT_MS;
    private static long readTimeoutMs = DEFAULT_READ_TIMEOUT_MS;
    private static long writeTimeoutMs = DEFAULT_WRITE_TIMEOUT_MS;
    private static int threadPoolCorePoolSize = DEFAULT_THREAD_POOL_CORE_POOL_SIZE;
    private static int threadPoolMaxPoolSize = DEFAULT_THREAD_POOL_MAX_POOL_SIZE;
    private static long threadPoolKeepAliveTimeMs = DEFAULT_THREAD_POOL_KEEP_ALIVE_TIME;
    private static int threadPoolQueueSize = DEFAULT_THREAD_POOL_QUEUE_SIZE;

    private static final Map<String, TsdbService> serviceMap = Maps.newConcurrentMap();

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

    private static void initConfig() {
        final String maxIdleConnectionsConfig = System.getProperty(TSDB_CLIENT_MAX_IDLE_CONNECTIONS);
        if (!StringUtils.isEmpty(maxIdleConnectionsConfig)) {
            maxIdleConnections = Long.valueOf(maxIdleConnectionsConfig).intValue();
        } else {
            System.setProperty(TSDB_CLIENT_MAX_IDLE_CONNECTIONS, String.valueOf(maxIdleConnections));
        }

        final String keepAliveDurationConfig = System.getProperty(TSDB_CLIENT_KEEP_ALIVE_DURATION);
        if (!StringUtils.isEmpty(keepAliveDurationConfig)) {
            keepAliveDurationMs = Long.valueOf(keepAliveDurationConfig).longValue();
        } else {
            System.setProperty(TSDB_CLIENT_KEEP_ALIVE_DURATION, String.valueOf(keepAliveDurationMs));
        }

        final String connectTimeoutConfig = System.getProperty(TSDB_CLIENT_CONNECT_TIMEOUT);
        if (!StringUtils.isEmpty(connectTimeoutConfig)) {
            connectTimeoutMs = Long.valueOf(connectTimeoutConfig).longValue();
        } else {
            System.setProperty(TSDB_CLIENT_CONNECT_TIMEOUT, String.valueOf(connectTimeoutMs));
        }

        final String readTimeoutConfig = System.getProperty(TSDB_CLIENT_READ_TIMEOUT);
        if (!StringUtils.isEmpty(readTimeoutConfig)) {
            readTimeoutMs = Long.valueOf(readTimeoutConfig).longValue();
        } else {
            System.setProperty(TSDB_CLIENT_READ_TIMEOUT, String.valueOf(readTimeoutMs));
        }

        final String writeTimeoutConfig = System.getProperty(TSDB_CLIENT_WRITE_TIMEOUT);
        if (!StringUtils.isEmpty(writeTimeoutConfig)) {
            writeTimeoutMs = Long.valueOf(writeTimeoutConfig).longValue();
        } else {
            System.setProperty(TSDB_CLIENT_WRITE_TIMEOUT, String.valueOf(writeTimeoutMs));
        }

        final String threadPoolCorePoolSizeConfig = System.getProperty(TSDB_CLIENT_THREAD_POOL_CORE_POOL_SIZE);
        if (!StringUtils.isEmpty(threadPoolCorePoolSizeConfig)) {
            threadPoolCorePoolSize = Long.valueOf(threadPoolCorePoolSizeConfig).intValue();
        } else {
            System.setProperty(TSDB_CLIENT_THREAD_POOL_CORE_POOL_SIZE, String.valueOf(threadPoolCorePoolSize));
        }

        final String threadPoolMaxPoolSizeConfig = System.getProperty(TSDB_CLIENT_THREAD_POOL_MAX_POOL_SIZE);
        if (!StringUtils.isEmpty(threadPoolMaxPoolSizeConfig)) {
            threadPoolMaxPoolSize = Long.valueOf(threadPoolMaxPoolSizeConfig).intValue();
        } else {
            System.setProperty(TSDB_CLIENT_THREAD_POOL_MAX_POOL_SIZE, String.valueOf(threadPoolMaxPoolSize));
        }

        final String threadPoolKeepAliveTimeMsConfig = System.getProperty(TSDB_CLIENT_THREAD_POOL_KEEP_ALIVE_TIME);
        if (!StringUtils.isEmpty(threadPoolKeepAliveTimeMsConfig)) {
            threadPoolKeepAliveTimeMs = Long.valueOf(threadPoolKeepAliveTimeMsConfig).longValue();
        } else {
            System.setProperty(TSDB_CLIENT_THREAD_POOL_KEEP_ALIVE_TIME, String.valueOf(threadPoolKeepAliveTimeMs));
        }

        final String threadPoolQueueSizeConfig = System.getProperty(TSDB_CLIENT_THREAD_POOL_QUEUE_SIZE);
        if (!StringUtils.isEmpty(threadPoolQueueSizeConfig)) {
            threadPoolQueueSize = Long.valueOf(threadPoolQueueSizeConfig).intValue();
        } else {
            System.setProperty(TSDB_CLIENT_THREAD_POOL_QUEUE_SIZE, String.valueOf(threadPoolQueueSize));
        }

    }

    private static TsdbService init(String url, String username, String password) {
        final String credentials = username + ":" + password;

        initConfig();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectionPool(new ConnectionPool(maxIdleConnections, keepAliveDurationMs));
        okHttpClient.setConnectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(readTimeoutMs, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(writeTimeoutMs, TimeUnit.MILLISECONDS);
        ExecutorService executorService = new ThreadPoolExecutor(threadPoolCorePoolSize, threadPoolMaxPoolSize,
            threadPoolKeepAliveTimeMs, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(threadPoolQueueSize),
            Util.threadFactory("OkHttp Dispatcher", false));
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
