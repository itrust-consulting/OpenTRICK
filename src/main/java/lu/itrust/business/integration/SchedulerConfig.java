package lu.itrust.business.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskExecutor executor(Environment env) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(
                env.getRequiredProperty("app.settings.background.task.core.pool.size", Integer.class));
        executor.setMaxPoolSize(
                env.getRequiredProperty("app.settings.background.task.max.pool.size", Integer.class));
        executor.setQueueCapacity(
                env.getRequiredProperty("app.settings.background.task.queue.capacity", Integer.class));
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    @Bean
    public ThreadPoolTaskExecutor emailTaskExecutor() {
        var emailTaskExecutor = new ThreadPoolTaskExecutor();
        emailTaskExecutor.setCorePoolSize(5);
        emailTaskExecutor.setMaxPoolSize(10);
        emailTaskExecutor.setQueueCapacity(30);
        emailTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return emailTaskExecutor;
    }

    @Bean
    @Primary
    public ThreadPoolTaskScheduler scheduler(@Value("${app.settings.scheduler.pool.size}") int poolSize) {
        var pool = new ThreadPoolTaskScheduler();
        pool.setPoolSize(poolSize);
        return pool;
    }

}
