package me.heng.akka;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.japi.Creator;
import me.heng.akka.Node.*;

import java.util.LinkedList;
import java.util.function.Function;

/**
 * AUTHOR: Di.W
 * DATE: 2019-03-12
 * TIME: 11:05
 */
public class FlowActorCreator implements Creator<FlowActor> {

    final ActorSystem system;
    final Scheduler scheduler;

    private LinkedList<Props> nodeList = new LinkedList<>();

    public FlowActorCreator(ActorSystem actorSystem) {
        this.system = actorSystem;
        this.scheduler = actorSystem.scheduler();
    }

    void sync(Function functor) {
        buildNode(SyncActor.class, functor);
    }

    void async(Function functor) {
        buildNode(AsyncActor.class, functor);
    }

    void delay(Function functor, int delay) {
        buildNode(DelayActor.class, functor, delay);
    }

    void timeout(Function functor, int timeout) {
        buildNode(TimeoutActor.class, functor, timeout);
    }

    void retry(Function functor, int times, int interval) {
        buildNode(RetryActor.class, functor, times, interval);
    }

    protected void buildNode(Class<?> clz, Object... args) {
        Props props;
        Function functor = (Function) args[0];
        if (clz == TimeoutActor.class) {
            int timeout = (Integer) args[1];
            props = Props.create(clz, functor, timeout);
        } else if (clz == DelayActor.class) {
            int delay = (Integer) args[1];
            props = Props.create(clz, functor, delay);
        } else if (clz == RetryActor.class) {
            int times = (Integer) args[1];
            int interval = (Integer) args[2];
            props = Props.create(clz, functor, times, interval);
        } else {
            props = Props.create(clz, functor);
        }
        nodeList.add(props);
    }

    @Override
    public FlowActor create() throws Exception {
        return new FlowActor(nodeList);
    }
}
