package io.iabc.tsdb.opentsdb.client.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-05 14:15
 */
public class Point implements Serializable {

    private static final long serialVersionUID = -6496372940607953280L;

    private String metric;
    private long timestamp;
    private long value;
    private Map<String, String> tags = new TreeMap<>();

    private TimeUnit precision = TimeUnit.SECONDS;

    protected Point() {

    }

    public String jsonProtocol() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"metric\":\"").append(metric).append("\"");
        sb.append(", \"timestamp\":\"").append(timestamp).append("\"");
        sb.append(", \"value\":\"").append(value).append("\"");
        sb.append(", \"tags\":{");
        this.tags.forEach((tagk, tagv) -> {
            sb.append("\"").append(tagk).append("\":\"").append(tagv).append("\",");
        });
        if (!this.tags.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}}");
        return sb.toString();
        //        return JSON.toJSONString(this);
    }

    public String lineProtocol() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.metric);
        sb.append(" ").append(this.timestamp);
        sb.append(" ").append(this.value);

        for (Map.Entry<String, String> tag : this.tags.entrySet()) {
            sb.append(" ");
            sb.append(tag.getKey()).append("=").append(tag.getValue());
        }

        return sb.toString();
    }

    public static PointBuilder metric(String metric) {
        return new PointBuilder(metric);
    }

    public static PointBuilder metric(String metric, String application) {
        return new PointBuilder(metric, application);
    }

    public static PointBuilder metric(String metric, String application, String host) {
        return new PointBuilder(metric, application, host);
    }

    public String getMetric() {
        return metric;
    }

    public Point setMetric(String metric) {
        this.metric = metric;
        return this;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Point setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * 获取 {@link #timestamp}
     *
     * @return 返回 {@link #timestamp}
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置 {@link #timestamp}
     *
     * @param timestamp 新的{@link #timestamp}
     */
    public Point setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * 获取 {@link #value}
     *
     * @return 返回 {@link #value}
     */
    public long getValue() {
        return value;
    }

    /**
     * 设置 {@link #value}
     *
     * @param value 新的{@link #value}
     */
    public Point setValue(long value) {
        this.value = value;
        return this;
    }

    public TimeUnit getPrecision() {
        return precision;
    }

    public Point setPrecision(TimeUnit precision) {
        this.precision = precision;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Point{");
        sb.append("metric=\"").append(metric).append("\"");
        sb.append(", timestamp=").append(timestamp);
        sb.append(", value=").append(value);
        sb.append(", tags=").append(tags);
        sb.append(", precision=").append(precision);
        sb.append('}');
        return sb.toString();
    }

    public static class PointBuilder {
        private String metric;
        private Map<String, String> tags = new HashMap<String, String>();
        private Long time;
        private Long value;
        private TimeUnit precision = TimeUnit.SECONDS;

        PointBuilder(String metric) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(metric), "metric can't be null or empty");
            if (StringUtils.containsAny(metric, '\n', '\\', '^', '$', '\'', '\"', ',')) {
                throw new IllegalArgumentException("metric name should not containt any of [\\,^,$,',\",,] key words");
            }

            this.metric = metric;
        }

        PointBuilder(String metric, String application) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(metric), "metric can't be null or empty");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(application), "application can't be null or empty");

            if (StringUtils.containsAny(metric, '\n', '\\', '^', '$', '\'', '\"', ',')) {
                throw new IllegalArgumentException("metric name should not containt any of [\\,^,$,',\",,] key words");
            }

            this.metric = metric;
            tags.put("application", application);
        }

        PointBuilder(String metric, String application, String host) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(metric), "metric can't be null or empty");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(application), "application can't be null or empty");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "host can't be null or empty");

            if (StringUtils.containsAny(metric, '\n', '\\', '^', '$', '\'', '\"', ',')) {
                throw new IllegalArgumentException("metric name should not containt any of [\\,^,$,',\",,] key words");
            }

            this.metric = metric;
            tags.put("application", application);
            tags.put("host", host);
        }

        public PointBuilder tags(Map<String, String> tagkvs) {
            Preconditions.checkArgument(tagkvs != null, "tags can't be null or empty");

            tagkvs.forEach((tagk, tagv) -> {
                if (StringUtils.containsAny(tagk, '\n', '\\', '^', '$', '\'', '\"', ',')) {
                    throw new IllegalArgumentException("tag name should not containt any of [\\,^,$,',\",,] key words");
                }
                if (StringUtils.containsAny(tagv, '\n', '\\', '^', '$', '\'', '\"', ',')) {
                    throw new IllegalArgumentException(
                        "tag value should not containt any of [\\,^,$,',\",,] key words");
                }

                this.tags.put(tagk, tagv);
            });

            return this;
        }

        public PointBuilder tag(String key, String value) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "tag key can't be null or empty");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(value), "tag value can't be null or empty");

            if (StringUtils.containsAny(key, '\n', '\\', '^', '$', '\'', '\"', ',')) {
                throw new IllegalArgumentException("tag name should not containt any of [\\,^,$,',\",,] key words");
            }
            if (StringUtils.containsAny(value, '\n', '\\', '^', '$', '\'', '\"', ',')) {
                throw new IllegalArgumentException("tag value should not containt any of [\\,^,$,',\",,] key words");
            }
            this.tags.put(key, value);
            return this;
        }

        public PointBuilder value(long value) {
            this.value = value;
            return this;
        }

        /**
         * Notice: Only support Millisecond precision,TimeUnit.MILLISECONDS
         *
         * @param time 时间，毫秒级
         * @return PointBuilder
         */
        public PointBuilder timestamp(long time) {
            if (time <= 0) {
                throw new IllegalArgumentException("time should >0");
            }
            this.time = time;
            this.precision = TimeUnit.SECONDS;
            return this;
        }

        public PointBuilder time(long time, TimeUnit timeUnit) {
            if (time <= 0) {
                throw new IllegalArgumentException("time should >0");
            }
            this.time = time;
            this.precision = timeUnit;
            return this;
        }

        @Deprecated
        public PointBuilder application(String application) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(application), "application can't be null or empty");
            tags.put("application", application);
            return this;
        }

        @Deprecated
        public PointBuilder host(String host) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "host can't be null or empty");
            tags.put("host", host);
            return this;
        }

        public Point build() {
            if (this.metric == null || this.metric.trim().isEmpty()) {
                throw new IllegalArgumentException("metric can't be null or blank");
            }

            Point point = new Point();
            point.setMetric(this.metric);
            point.setValue(this.value);
            point.setTags(this.tags);
            if (this.time <= 0) {
                throw new IllegalArgumentException("time should >0");
            }
            point.setTimestamp(point.precision.convert(this.time, this.precision));

            return point;
        }

    }

}
