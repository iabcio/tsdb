package io.iabc.tsdb.opentsdb.client.domain;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-05 13:52
 */
public class TsdbQueryResult {

    ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private String metric;
    private Map<String, String> tags = new HashMap<>();
    private List<String> aggregatorTags = new ArrayList<>();
    private Map<String, Double> dps = new HashMap<>();

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
     * 获取 {@link #metric}
     *
     * @return 返回 {@link #metric}
     */
    public String getMetric() {
        return metric;
    }

    /**
     * 设置 {@link #metric}
     *
     * @param metric 新的{@link #metric}
     */
    public TsdbQueryResult setMetric(String metric) {
        this.metric = metric;
        return this;
    }

    /**
     * 获取 {@link #tags}
     *
     * @return 返回 {@link #tags}
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * 设置 {@link #tags}
     *
     * @param tags 新的{@link #tags}
     */
    public TsdbQueryResult setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * 获取 {@link #aggregatorTags}
     *
     * @return 返回 {@link #aggregatorTags}
     */
    public List<String> getAggregatorTags() {
        return aggregatorTags;
    }

    /**
     * 设置 {@link #aggregatorTags}
     *
     * @param aggregatorTags 新的{@link #aggregatorTags}
     */
    public TsdbQueryResult setAggregatorTags(List<String> aggregatorTags) {
        this.aggregatorTags = aggregatorTags;
        return this;
    }

    /**
     * 获取 {@link #dps}
     *
     * @return 返回 {@link #dps}
     */
    public Map<String, Double> getDps() {
        return dps;
    }

    /**
     * 设置 {@link #dps}
     *
     * @param dps 新的{@link #dps}
     */
    public TsdbQueryResult setDps(Map<String, Double> dps) {
        this.dps = dps;
        return this;
    }
}
