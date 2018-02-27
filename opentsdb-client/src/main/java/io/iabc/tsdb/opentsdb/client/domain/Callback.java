package io.iabc.tsdb.opentsdb.client.domain;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-06 10:58
 */
public interface Callback<T> {

    void success(T t);

    void failure(T error);
}
