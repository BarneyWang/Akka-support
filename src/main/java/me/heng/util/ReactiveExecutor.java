package me.heng.util;

import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Created by Di Wang on 5/5/2016 AD.
 */
public interface ReactiveExecutor {

    <T, R> void sync(Function<T, R> functor);

    <T, R> void async(Function<T, R> functor);

    /**
     * 按照 delays 延迟顺序,按序执行functor,直到返回true/object!=null等
     * @param <T>
     * @param <R>
     * @param delays
     * @param functor
     */
    <T, R> void schedule(int delays, Function<T, R> functor);

    /**
     * 重试times次,每次间隔 interval
     *  @param <T>
     * @param <R>
     * @param times
     * @param interval
     * @param functor
     */
    <T, R> void retry(int times, int interval, Function<T, R> functor);

    /**
     * 执行 functor, 超时millis未成功则返回
     *
     * @param functor
     * @param millis
     * @param <T>
     * @param <R>
     */
    <T, R> void timeout(Function<T, R> functor, int millis);

    /**
     * 提交任务定义
     *
     * @return
     */
    <R, I> Future<R> submit(I input);
}
