package com.cxj.config;

import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 虚拟线程配置 (JDK 25)
 *
 * spring.threads.virtual.enabled=true 已启用 Tomcat 与 @Async 的虚拟线程；
 * 这里为 @Async 提供显式命名的虚拟线程执行器，用于诊断与线程命名。
 */
@Configuration
@EnableAsync
public class VirtualThreadConfig {

    @Bean("applicationTaskExecutor")
    public AsyncTaskExecutor applicationTaskExecutor(SimpleAsyncTaskExecutorBuilder builder) {
        return builder.virtualThreads(true).threadNamePrefix("cxj-vt-").build();
    }

    @Bean
    public ThreadFactory virtualThreadFactory() {
        return Thread.ofVirtual().name("cxj-vt-", 0).factory();
    }

    /**
     * 可选：暴露一个基于虚拟线程的 ExecutorService，供业务代码手动提交任务
     */
    @Bean(destroyMethod = "shutdown")
    public java.util.concurrent.ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
