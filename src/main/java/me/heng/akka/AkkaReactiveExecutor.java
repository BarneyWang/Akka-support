package me.heng.akka;

import akka.actor.*;
import com.alibaba.ais.scrm.task.ReactiveExecutor;
import com.alibaba.ais.scrm.task.akka.Event.FlowEvent;
import com.alibaba.ais.scrm.util.Loggable;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import scala.concurrent.duration.Duration;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Created by chuanbao on 5/8/2016 AD.
 *
 * akka 实现 ReactiveExecutor
 */
public class AkkaReactiveExecutor implements ReactiveExecutor, Loggable<Logger> {
    protected Logger logger = getLogger();

    final ActorSystem actorSystem;
    /**
     * TODO 应该 ThreadLocal类型
     */
    FlowActorCreator creator;
    final ActorRef manager;

    public AkkaReactiveExecutor(String name, Map conf) {
        Config config = null;
        actorSystem = ActorSystem.create(name, config);
        manager = actorSystem.actorOf(Props.create(ManagerActor.class));
        creator = new FlowActorCreator(actorSystem);
    }

    @Override
    public <T, R> void sync(Function<T, R> functor) {
        creator.sync(functor);
    }

    @Override
    public <T, R> void async(Function<T, R> functor) {
        creator.async(functor);
    }

    @Override
    public <T, R> void schedule(int delay, Function<T, R> functor) {
        creator.delay(functor, delay);
    }

    @Override
    public <T, R> void retry(int times, int interval, Function<T, R> functor) {
        creator.retry(functor, times, interval);
    }

    @Override
    public <T, R> void timeout(Function<T, R> functor, int millis) {
        creator.timeout(functor, millis);
    }

    @Override
    public <R, I> Future<R> submit(I input) {
        manager.tell(FlowEvent.start(creator, input), ActorRef.noSender());
        /**
         * TODO 实现future
         */
        return null;
    }

    public void stop() {
        final Inbox inbox = Inbox.create(actorSystem);
        try {
            assert inbox.receive(Duration.create(1, TimeUnit.SECONDS)) instanceof Terminated;
        } catch (TimeoutException e) {
            // timeout
        }
    }

    /**
     * service facade 调用入口
     */
    public static class ManagerActor extends BaseActor {

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Throwable) {

            } else if (message instanceof Event.FlowEvent) {
                Event.FlowEvent event = (Event.FlowEvent) message;
                if (event instanceof Event.FlowStart) {
                    Event.FlowStart meta = (Event.FlowStart) message;
                    ActorRef flow = context().actorOf(Props.create(meta.getCreator()), "flow");
                    flow.tell(meta.getData(), self());
                    // int FlowTimeout = 10000;
                    // scala.concurrent.Future<Object> future = ask(flow, meta.getData(),
                    // FlowTimeout);
                    // pipe(future, context().dispatcher()).to(sender());
                } else if (event instanceof Event.FlowEnd) {
                    logger.info("flow:{} end:{}", sender(), event.getData());
                    killChild(sender());
                } else if (event instanceof Event.FlowError) {
                    logger.error("flow:{} error:{}", sender(), event.getData());
                    killChild(sender());
                }
            }
        }


    }
}
