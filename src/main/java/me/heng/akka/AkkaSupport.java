package me.heng.akka;

import akka.actor.Props;
import me.heng.util.Exceptions;


import java.util.function.Function;

/**
 * AUTHOR: Di.W
 * DATE: 2019-03-12
 * TIME: 11:05
 */
public class AkkaSupport {

    public static <I, O> Props actorOf(Function<I, O> functor, Function<Throwable, O> handler) {
        return Props.create(FunctionActor.class, functor, handler);
    }

    public static <I, O> Props actorOf(Function<I, O> functor) {
        return actorOf(functor, Exceptions::softThrow);
    }
}
