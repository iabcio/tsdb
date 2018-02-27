package io.iabc.tsdb.opentsdb.client;

import com.google.common.base.MoreObjects;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-06 13:50
 */
public class TsdbException extends RuntimeException {

    private final int status;
    private final String msg;

    public TsdbException(int status, String msg) {
        super();
        this.status = status;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("status", status).add("msg", msg).toString();
    }
}
