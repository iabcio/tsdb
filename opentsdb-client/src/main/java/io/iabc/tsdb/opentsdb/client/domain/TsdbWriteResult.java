package io.iabc.tsdb.opentsdb.client.domain;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-05 13:52
 */
public class TsdbWriteResult implements Serializable {
    ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private static final long serialVersionUID = -3344474976761889135L;
    

    //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private int status;
    private String reason;
    private int success;
    private int failed;
    private List<Object> errors;

    /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //------------------------ Implements: 

    //------------------------ Overrides:

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    //---------------------------- Abstract Methods -----------------------------

    //---------------------------- Utility Methods ------------------------------

    //---------------------------- Property Methods -----------------------------

    /**
     * 获取 {@link #status}
     *
     * @return 返回 {@link #status}
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置 {@link #status}
     *
     * @param status 新的{@link #status}
     */
    public TsdbWriteResult setStatus(int status) {
        this.status = status;
        return this;
    }

    /**
     * 获取 {@link #reason}
     *
     * @return 返回 {@link #reason}
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置 {@link #reason}
     *
     * @param reason 新的{@link #reason}
     */
    public TsdbWriteResult setReason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * 获取 {@link #success}
     *
     * @return 返回 {@link #success}
     */
    public int getSuccess() {
        return success;
    }

    /**
     * 设置 {@link #success}
     *
     * @param success 新的{@link #success}
     */
    public TsdbWriteResult setSuccess(int success) {
        this.success = success;
        return this;
    }

    /**
     * 获取 {@link #failed}
     *
     * @return 返回 {@link #failed}
     */
    public int getFailed() {
        return failed;
    }

    /**
     * 设置 {@link #failed}
     *
     * @param failed 新的{@link #failed}
     */
    public TsdbWriteResult setFailed(int failed) {
        this.failed = failed;
        return this;
    }

    /**
     * 获取 {@link #errors}
     *
     * @return 返回 {@link #errors}
     */
    public List<Object> getErrors() {
        return errors;
    }

    /**
     * 设置 {@link #errors}
     *
     * @param errors 新的{@link #errors}
     */
    public TsdbWriteResult setErrors(List<Object> errors) {
        this.errors = errors;
        return this;
    }
}
