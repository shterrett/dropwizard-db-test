package dbtest;

import com.codahale.metrics.*;
import lombok.extern.slf4j.*;
import java.util.*;

@Slf4j
public class DatabaseMetrics {
    private static MetricRegistry registry;
    public static final String DATABASE_NAME = "postgresql";

    public static void setRegistry(MetricRegistry registry) { DatabaseMetrics.registry = registry; }

    public static String getPoolState() {
        int active = -1, idle = -1, size = -1, waiting = -1;
        boolean success = false;

        try {
            Map<String, Metric> metrics = registry.getMetrics();
            active = getDatabaseMetric(metrics, "active");
            idle = getDatabaseMetric(metrics, "idle");
            size = getDatabaseMetric(metrics, "size");
            waiting = getDatabaseMetric(metrics, "waiting");

            if (active != -1 && idle != -1 && size != -1 && waiting != -1) {
                success = true;
            }
        } catch (Exception exc) {
            log.error("Can't read database metrics.", exc);
        }

        return String.format("Pool Size: %d; Active: %d; Idle: %d; Waiting: %d",
                size,
                active,
                idle,
                waiting
                );
    }

    private static int getDatabaseMetric(Map<String, Metric> metrics, String metricName) {
        Metric m = metrics.get("io.dropwizard.db.ManagedPooledDataSource." + DATABASE_NAME + "." + metricName);
        if (m != null) {
            Gauge gauge = (Gauge)m;
            return (int)gauge.getValue();
        } else {
            return -1;
        }
    }
}
