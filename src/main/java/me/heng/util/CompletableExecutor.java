package me.heng.util;

import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * AUTHOR: Di.W
 * DATE: 2019-03-12
 * TIME: 11:05
 */
public class CompletableExecutor implements ReactiveExecutor, Loggable {

    protected final Logger logger = getLogger();

    ThreadLocal<String> flowId = new ThreadLocal<>();

    ThreadLocal<CompletableFuture<Object>> future;

    public CompletableExecutor(String name, int core, int max) {

    }

    @Override
    public <T, R> void sync(Function<T, R> functor) {

    }

    @Override
    public <T, R> void async(Function<T, R> functor) {

    }

    @Override
    public <T, R> void schedule(int delays, Function<T, R> functor) {

    }

    @Override
    public <T, R> void retry(int times, int interval, Function<T, R> functor) {

    }

    @Override
    public <T, R> void timeout(Function<T, R> functor, int millis) {

    }

    @Override
    public <R, I> Future<R> submit(I input) {
        return null;
    }
}
