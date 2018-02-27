package io.iabc.tsdb.opentsdb.client.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-07 14:54
 */
public class MultiPoint implements Serializable {
    ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private List<Point> points;
    /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static MultiPointBuilder getBuilder() {
        return new MultiPointBuilder();
    }

    ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public String jsonProtocol() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Point point : this.points) {
            sb.append(point.jsonProtocol()).append(",");
        }
        if (!this.points.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        return sb.toString();
    }

    public String lineProtocol() {
        StringBuilder sb = new StringBuilder();
        for (Point point : this.points) {
            sb.append(point.lineProtocol()).append("\n");
        }
        return sb.toString();
    }

    //------------------------ Implements: 

    //------------------------ Overrides:

    //---------------------------- Abstract Methods -----------------------------

    //---------------------------- Utility Methods ------------------------------

    //---------------------------- Property Methods -----------------------------

    /**
     * 获取 {@link #points}
     *
     * @return 返回 {@link #points}
     */
    public List<Point> getPoints() {
        return points;
    }

    /**
     * 设置 {@link #points}
     *
     * @param points 新的{@link #points}
     */
    public MultiPoint setPoints(List<Point> points) {
        this.points = points;
        return this;
    }

    public static class MultiPointBuilder {
        private Long time;
        private TimeUnit precision = TimeUnit.SECONDS;
        private Map<String, String> tags = new HashMap<String, String>();
        private Map<String, Long> fields = new HashMap<String, Long>();
        private List<Point> points = new ArrayList<Point>();

        protected MultiPointBuilder() {

        }

        public MultiPointBuilder tag(String name, String value) {
            this.tags.put(name, value);
            return this;
        }

        public MultiPointBuilder timestamp(long time) {
            Preconditions.checkArgument(!(time > 9999999999L), "timestamp can't be larger than 9999999999L");
            Preconditions.checkArgument(!(time < 0), "timestamp can't be larger than 9999999999L");
            this.time = time;
            this.precision = TimeUnit.SECONDS;
            return this;
        }

        public MultiPointBuilder time(long time, TimeUnit timeUnit) {
            if (time <= 0) {
                throw new IllegalArgumentException("time should >0");
            }
            this.time = time;
            this.precision = timeUnit;
            return this;
        }

        public MultiPointBuilder field(String metric, long value) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(metric), "metric field name can't be null or empty");
            if (StringUtils.containsAny(metric, '\n', '\\', '^', '$', '\'', '\"', ',')) {
                throw new IllegalArgumentException("field name should not containt any of [\\,^,$,',\",,] key words");
            }
            this.fields.put(metric, value);
            return this;
        }

        public MultiPointBuilder point(Point point) {
            this.points.add(point);
            return this;
        }

        public MultiPointBuilder points(Point... points) {
            this.points.addAll(Arrays.asList(points));
            return this;
        }

        public MultiPointBuilder points(List<Point> points) {
            this.points.addAll(points);
            return this;
        }

        public MultiPoint build() {
            MultiPoint multiPoint = new MultiPoint();

            this.fields.forEach((metric, value) -> {
                Point point = Point.metric(metric).value(value).time(this.time, this.precision).tags(this.tags).build();
                this.points.add(point);
            });

            for (Point point : points) {
                point.getTags().putAll(this.tags);
            }

            multiPoint.setPoints(this.points);
            return multiPoint;
        }

    }

    //just for test
    public static void main(String[] args) {

        Point point = Point.metric("dubbo.invoke.success").timestamp(1517881145).value(2)
            .tag("application", "hrs-std-service").tag("invokeType", "provider").tag("consumer", "192.168.3.43")
            .tag("provider", "192.168.1.64").tag("service", "io.iabc.hrs.std.rpc.share.ExpertRpcService")
            .tag("method", "list").build();

        System.out.println(point.jsonProtocol());
        System.out.println(point.lineProtocol());
        System.out.println();

        Point point2 = Point.metric("dubbo.invoke.failure").timestamp(1517881145).value(1)
            .tag("application", "hrs-std-service").tag("invokeType", "provider").tag("consumer", "192.168.3.43")
            .tag("provider", "192.168.1.64").tag("service", "io.iabc.hrs.std.rpc.share.ExpertRpcService")
            .tag("method", "list").build();

        System.out.println(point2.jsonProtocol());
        System.out.println(point2.lineProtocol());
        System.out.println();

        MultiPoint multiPoint = MultiPoint.getBuilder().point(point).point(point2).build();

        System.out.println(multiPoint.jsonProtocol());
        System.out.println(multiPoint.lineProtocol());
        System.out.println();

        MultiPoint multiPoint2 = MultiPoint.getBuilder().timestamp(1517881145).field("dubbo.invoke.success", 3)
            .field("dubbo.invoke.failure", 2).tag("application", "hrs-std-service").tag("invokeType", "provider")
            .tag("consumer", "192.168.3.43").tag("provider", "192.168.1.64")
            .tag("service", "io.iabc.hrs.std.rpc.share.ExpertRpcService").tag("method", "list").build();

        System.out.println(multiPoint2.jsonProtocol());
        System.out.println(multiPoint2.lineProtocol());
        System.out.println();

    }

}
