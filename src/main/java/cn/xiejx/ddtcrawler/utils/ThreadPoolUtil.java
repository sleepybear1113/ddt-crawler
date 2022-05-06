package cn.xiejx.ddtcrawler.utils;

import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author XJX
 * @date 2021/8/9 2:08
 */
public class ThreadPoolUtil {
    private static final ThreadFactory DEFAULT_THREAD_FACTORY = new DefaultManagedAwareThreadFactory();
    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(5, 20, 2, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5), DEFAULT_THREAD_FACTORY);

    public static void start(Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }
}
