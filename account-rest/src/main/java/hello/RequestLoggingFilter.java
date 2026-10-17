package hello;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import se.liu.ida.tdp024.account.logic.util.KafkaConfig;
import se.liu.ida.tdp024.account.logic.kafka.KafkaPublisher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final KafkaPublisher kafkaPublisher;

    public RequestLoggingFilter(KafkaPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("method", req.getMethod());
            event.put("uri", req.getRequestURI());
            event.put("query", req.getQueryString());
            event.put("status", res.getStatus());
            event.put("durationMs", System.currentTimeMillis() - start);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaPublisher.publish(KafkaConfig.TOPIC_REST_REQUESTS, event);
        }
    }
}