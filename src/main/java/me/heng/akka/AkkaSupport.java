package me.heng.akka;

import akka.actor.Props;
import com.alibaba.ais.scrm.util.Exceptions;

import java.util.function.Function;

/**
 * Created by chuanbao on 5/8/2016 AD.
 */
public class AkkaSupport {

    public static <I, O> Props actorOf(Function<I, O> functor, Function<Throwable, O> handler) {
        return Props.create(FunctionActor.class, functor, handler);
    }

    public static <I, O> Props actorOf(Function<I, O> functor) {
        return actorOf(functor, Exceptions::softThrow);
    }
}
