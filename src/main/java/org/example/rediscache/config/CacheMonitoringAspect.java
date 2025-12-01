package org.example.rediscache.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.rediscache.utils.MonitoredCache;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CacheMonitoringAspect {

    private final MeterRegistry meterRegistry;

    @Around("@annotation(org.example.rediscache.utils.MonitoredCache) && @annotation(monitoredCache)")
    public Object monitorCacheMethod(ProceedingJoinPoint joinPoint, MonitoredCache monitoredCache) throws Throwable {
        String cacheName = monitoredCache.name();
        Timer.Sample sample = Timer.start(meterRegistry);

        Object result = null;
        String status = "miss";

        try {
            result = joinPoint.proceed();
            if (result != null) {
                status = "hit";
            }
        }
        catch (Exception e) {
            status = "error";
            throw e;
        }
        finally {
            meterRegistry.counter("custom.aop.cache", "name", cacheName, "result", status).increment();
            sample.stop(meterRegistry.timer("custom.aop.time", "name", cacheName));
        }
        return result;
    }
}
