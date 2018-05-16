package io.iabc.tsdb.opentsdb.client.domain;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO
 *
 * @author <a href="mailto:h@iabc.io">shuchen</a>
 * @author <a href="mailto:h@heyx.net">shuchen</a>
 * @version V1.0
 * @since 2018-02-05 13:50
 */
public class TsdbQuery implements Serializable {
    ///////////////////////////// Class Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //////////////////////////////// Attributes \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private String start;
    private String end;
    private Boolean useCalendar;
    private List<Query> queries = new ArrayList<Query>();

    /////////////////////////////// Constructors \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    ////////////////////////////// Class Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static TsdbQueryBuilder start(String start) {
        return new TsdbQueryBuilder(start);
    }

    public static TsdbQueryBuilder start(long start) {
        return new TsdbQueryBuilder(String.valueOf(start));
    }

    public static TsdbQueryBuilder range(long start, long end) {
        return new TsdbQueryBuilder(String.valueOf(start), String.valueOf(end));
    }

    public static TsdbQueryBuilder range(String start, String end) {
        return new TsdbQueryBuilder(start, end);
    }

    ////////////////////////////////// Methods \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    public String jsonProtocol() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"start\":\"").append(this.start).append("\"");
        if (!StringUtils.isEmpty(this.end)) {
            sb.append(", \"end\":\"").append(this.end).append("\"");
        }
        sb.append(", \"useCalendar\":\"").append(this.useCalendar).append("\"");

        sb.append(", \"queries\":[");
        for (Query query : this.queries) {
            sb.append(query.jsonProtocol()).append(",");
        }

        if (!this.queries.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
        //        return JSON.toJSONString(this);
    }

    //------------------------ Implements: 

    //------------------------ Overrides:

    //---------------------------- Abstract Methods -----------------------------

    //---------------------------- Utility Methods ------------------------------

    //---------------------------- Property Methods -----------------------------

    /**
     * 获取 {@link #start}
     *
     * @return 返回 {@link #start}
     */
    public String getStart() {
        return start;
    }

    /**
     * 获取 {@link #end}
     *
     * @return 返回 {@link #end}
     */
    public String getEnd() {
        return end;
    }

    /**
     * 获取 {@link #useCalendar}
     *
     * @return 返回 {@link #useCalendar}
     */
    public Boolean getUseCalendar() {
        return useCalendar;
    }

    /**
     * 获取 {@link #queries}
     *
     * @return 返回 {@link #queries}
     */
    public List<Query> getQueries() {
        return queries;
    }

    public static class Query {

        private String aggregator;
        private String metric;
        private Boolean rate;
        private String downsample;
        private Map<String, String> tags = new HashMap<>();
        private List<Filter> filters = new ArrayList<Filter>();

        public String jsonProtocol() {

            final StringBuilder sb = new StringBuilder("{");
            if (!StringUtils.isEmpty(this.aggregator)) {
                sb.append("\"aggregator\":\"").append(this.aggregator).append("\",");
            } else {
                sb.append("\"aggregator\":\"").append("sum").append("\",");
            }
            sb.append("\"metric\":\"").append(this.metric).append("\",");
            if (this.rate != null) {
                sb.append("\"rate\":\"").append(this.rate).append("\",");
            }
            if (!StringUtils.isEmpty(this.downsample)) {
                sb.append("\"downsample\":\"").append(this.downsample).append("\",");
            }

            sb.append("\"filters\":[");
            for (Filter filter : this.filters) {
                sb.append(filter.jsonProtocol()).append(",");
            }

            if (!this.filters.isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]");

            if (this.tags.size() > 0) {
                sb.append(",\"tags\":{");
                LongAdder couter = new LongAdder();
                this.tags.forEach((key, value) -> {
                    sb.append("\"").append(key).append("\":").append("\"").append(value).append("\"");
                    couter.increment();
                    if (couter.intValue() < this.tags.size()) {
                        sb.append(",");
                    }
                });
                sb.append("}");
            }

            sb.append("}");

            return sb.toString();
            //        return JSON.toJSONString(this);
        }

        public static QueryBuilder metric(String metric) {
            return new QueryBuilder(metric);
        }

        /**
         * 获取 {@link #aggregator}
         *
         * @return 返回 {@link #aggregator}
         */
        public String getAggregator() {
            return aggregator;
        }

        /**
         * 获取 {@link #metric}
         *
         * @return 返回 {@link #metric}
         */
        public String getMetric() {
            return metric;
        }

        /**
         * 获取 {@link #rate}
         *
         * @return 返回 {@link #rate}
         */
        public Boolean getRate() {
            return rate;
        }

        /**
         * 获取 {@link #downsample}
         *
         * @return 返回 {@link #downsample}
         */
        public String getDownsample() {
            return downsample;
        }

        /**
         * 获取 {@link #filters}
         *
         * @return 返回 {@link #filters}
         */
        public List<Filter> getFilters() {
            return filters;
        }

        public static class QueryBuilder {
            private String aggregator;
            private String metric;
            private Boolean rate;
            private String downsample;
            private Map<String, String> tags = new HashMap<>();
            private List<Filter> filters = new ArrayList<Filter>();

            QueryBuilder(String metric) {
                Preconditions.checkArgument(!Strings.isNullOrEmpty(metric), "metric can't be null or empty");
                this.metric = metric;
            }

            public QueryBuilder aggregator(String aggregator) {
                Preconditions.checkArgument(!Strings.isNullOrEmpty(aggregator), "aggregator can't be null or empty");
                this.aggregator = aggregator;
                return this;
            }

            public QueryBuilder rate(boolean rate) {
                this.rate = rate;
                return this;
            }

            public QueryBuilder downsample(String downsample) {
                Preconditions.checkArgument(!Strings.isNullOrEmpty(downsample), "downsample can't be null or empty");
                this.downsample = downsample;
                return this;
            }

            public QueryBuilder tags(Map<String, String> tags) {
                this.tags.putAll(tags);
                return this;
            }

            public QueryBuilder tag(String key, String value) {
                this.tags.putIfAbsent(key, value);
                return this;
            }

            public QueryBuilder iliteralOrTag(String key, String value) {
                this.tag(key, "literal_or(" + value + ")");
                return this;
            }

            public QueryBuilder wildcardTag(String key, String value) {
                this.tag(key, "wildcard(" + value + ")");
                return this;
            }

            public QueryBuilder groupBy(String groupBy) {
                this.wildcardTag(groupBy, "*");
                return this;
            }

            public QueryBuilder groupBys(Collection<String> groupBys) {
                Preconditions.checkArgument(groupBys != null, "groupBys can't be null");

                for (String groupBy : groupBys) {
                    this.wildcardTag(groupBy, "*");
                }
                return this;
            }

            public QueryBuilder filter(Filter filter) {
                this.filters.add(filter);
                return this;
            }

            public QueryBuilder filters(Collection<Filter> filters) {
                this.filters.addAll(filters);
                return this;
            }

            public Query build() {

                Query query = new Query();
                query.metric = this.metric;
                query.aggregator = this.aggregator;
                query.rate = this.rate;
                query.filters = this.filters;
                query.tags = this.tags;
                query.downsample = this.downsample;

                return query;
            }
        }
    }

    public static class Filter {

        private String type;
        private String tagk;
        private String filter;
        private Boolean groupBy;

        public String jsonProtocol() {

            final StringBuilder sb = new StringBuilder("{");
            if (this.groupBy != null) {
                sb.append("\"groupBy\":\"").append(this.groupBy).append("\",");
            }
            sb.append("\"type\":\"").append(this.type).append("\",");
            sb.append("\"tagk\":\"").append(this.tagk).append("\",");
            sb.append("\"filter\":\"").append(this.filter).append("\"");
            sb.append("}");

            return sb.toString();
            //        return JSON.toJSONString(this);
        }

        protected Filter() {
        }

        public static FilterBuilder iliteralOr(String tagk, String... filters) {
            return new FilterBuilder("iliteral_or", tagk, filters);
        }

        public static FilterBuilder wildcard(String tagk, String... filters) {
            return new FilterBuilder("wildcard", tagk, filters);
        }

        public static FilterBuilder regexp(String tagk, String... filters) {
            return new FilterBuilder("regexp", tagk, filters);
        }

        public static class FilterBuilder {
            private String type;
            private String tagk;
            private String filter;
            private Boolean groupBy;

            FilterBuilder(String type, String tagk, String... filters) {
                Preconditions.checkArgument(!Strings.isNullOrEmpty(type), "type can't be null or empty");
                Preconditions.checkArgument(!Strings.isNullOrEmpty(tagk), "tagk can't be null or empty");
                Preconditions.checkArgument(filters != null, "filters can't be null or empty");
                this.type = type;
                this.tagk = tagk;
                this.filter = String.join("|", filters);
            }

            public FilterBuilder groupBy(boolean groupBy) {
                this.groupBy = groupBy;
                return this;
            }

            public Filter build() {
                Filter f = new Filter();

                f.type = this.type;
                f.tagk = this.tagk;
                f.filter = this.filter;
                f.groupBy = this.groupBy;

                return f;
            }

        }

        /**
         * 获取 {@link #type}
         *
         * @return 返回 {@link #type}
         */
        public String getType() {
            return type;
        }

        /**
         * 获取 {@link #tagk}
         *
         * @return 返回 {@link #tagk}
         */
        public String getTagk() {
            return tagk;
        }

        /**
         * 获取 {@link #filter}
         *
         * @return 返回 {@link #filter}
         */
        public String getFilter() {
            return filter;
        }

        /**
         * 获取 {@link #groupBy}
         *
         * @return 返回 {@link #groupBy}
         */
        public Boolean getGroupBy() {
            return groupBy;
        }

    }

    public static class TsdbQueryBuilder {
        private String start;
        private String end;
        private Boolean useCalendar;
        private List<Query> queries = new ArrayList<Query>();
        private TimeUnit precision = TimeUnit.SECONDS;

        TsdbQueryBuilder(String start) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(start), "start can't be null or empty");

            this.start = start;
        }

        TsdbQueryBuilder(String start, String end) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(start), "start can't be null or empty");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(end), "end can't be null or empty");

            this.start = start;
            this.end = end;
        }

        public TsdbQueryBuilder useCalendar(boolean useCalendar) {
            this.useCalendar = useCalendar;
            return this;
        }

        public TsdbQueryBuilder query(Query query) {
            this.queries.add(query);
            return this;
        }

        public TsdbQueryBuilder queries(Query... queries) {
            this.queries.addAll(Arrays.asList(queries));
            return this;
        }

        public TsdbQueryBuilder queries(Collection<Query> queries) {
            this.queries.addAll(queries);
            return this;
        }

        public TsdbQuery build() {

            TsdbQuery tsdbQuery = new TsdbQuery();
            tsdbQuery.start = this.start;
            tsdbQuery.end = this.end;
            tsdbQuery.queries = this.queries;
            tsdbQuery.useCalendar = this.useCalendar;
            return tsdbQuery;
        }

    }

}
