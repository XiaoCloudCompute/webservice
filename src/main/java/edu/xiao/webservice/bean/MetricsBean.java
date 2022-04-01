package edu.xiao.webservice.bean;

import com.timgroup.statsd.NoOpStatsDClient;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsBean {

    @Value("${publish.metrics}")
    private boolean publishMetrics;

    @Value("${metrics.server.hostname}")
    private String metricsHost;

    @Value("${metrics.server.port}")
    private int metricsPort;

    @Bean
    public StatsDClient metricsClient() {
        if (publishMetrics) {
            return new NonBlockingStatsDClient("csye6225", metricsHost, metricsPort);
        }
        return new NoOpStatsDClient();
    }
}
