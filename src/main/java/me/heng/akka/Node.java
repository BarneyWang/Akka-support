package me.heng.akka;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.ReceiveTimeout;
import akka.util.Timeout;
import com.alibaba.ais.scrm.task.akka.Event.NodeEvent;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;
import static com.alibaba.ais.scrm.task.akka.Event.NodeEvent.error;
import static com.alibaba.ais.scrm.task.akka.Event.NodeEvent.success;
import static com.alibaba.ais.scrm.task.akka.FunctorError.*;

/**
 * Created by chuanbao on 5/8/2016 AD.
 */
class Node {

    static abstract class NodeActor extends BaseActor {
        private final Function _functor;

        public NodeActor(Function functor) {
            this._functor = functor;
        }

        protected Function functor() {
            return this._functor;
        }

        protected Cancellable scheduleSeconds(int seconds, ActorRef receiver, Object message,
                ActorRef sender) {
            FiniteDuration duration = Duration.create(seconds, TimeUnit.SECONDS);
            return system().scheduler().scheduleOnce(duration, receiver, message, system().dispatcher(),
                    sender);
        }
    }


    /**
     * functor 直接在Node中执行
     */
    static class SyncActor extends NodeActor {

        public SyncActor(Function functor) {
            super(functor);
        }

        @Override
        public void onReceive(Object message) throws Exception {
            if (sender().equals(context().parent())) {
                try {
                    Object result = functor().apply(message);
                    report(NodeEvent.success(result));
                } catch (Throwable t) {
                    report(NodeEvent.error(t));
                }
            } else
                unhandled(message);
        }
    }


    /**
     * functor 在 新的 Actor执行
     */
    static class AsyncActor extends NodeActor {
        private ActorRef _target;

        public AsyncActor(Function functor) {
            super(functor);
        }

        @Override
        public void onReceive(Object message) throws Exception {
            logger.debug("received:{}", message);
            if (message instanceof FunctorError) {
                logger.warning("{} return error", sender());
                if (message instanceof ReturnNull) {
                    logger.warning("{} return null", sender());
                } else if (message instanceof RunError) {
                    logger.warning("{} run error:{}", sender(), ((RunError) message).getMessage());
                    report(NodeEvent.error( message));
                } else if (message instanceof TypeError) {
                    logger.warning("{} parameter type mismatch:{}", sender(),
                            ((RunError) message).getMessage());
                    report(NodeEvent.error( message));
                }
            } else {
                if (sender().equals(_target)) {
                    doTargetMessage(message);
                } else
                    doFlowMessage(message);
            }
        }

        protected void doTargetMessage(Object message) {
            if (message instanceof Throwable) {
                report(error(message));
            } else
                report(success(message));
        }

        protected void doFlowMessage(Object message) {
            _target = newChild(AkkaSupport.actorOf(functor()));
            _target.tell(message, self());
        }

        protected Cancellable scheduleSeconds(int seconds, Object message) {
            FiniteDuration duration = Duration.create(seconds, TimeUnit.SECONDS);
            return scheduleSeconds(seconds, _target, message, self());
        }

        protected Future<Object> askChild(Object msg, int seconds) {
            Future<Object> future = ask(_target, msg, Timeout.apply(seconds, TimeUnit.SECONDS));
            pipe(future, context().dispatcher()).to(self());
            return future;
        }
    }

    static class DelayActor extends AsyncActor {
        int delay;

        protected DelayActor(Function functor, int delay) {
            super(functor);
            this.delay = delay;
        }

        @Override
        protected void doFlowMessage(Object message) {
            scheduleSeconds(delay, message);
        }
    }


    static class TimeoutActor extends AsyncActor {
        final int timeout;

        protected TimeoutActor(Function functor, int seconds) {
            super(functor);
            timeout = seconds;
        }

        @Override
        protected void doTargetMessage(Object message) {
            if (message instanceof TimeoutException) {
                report(NodeEvent.error(message));
                /**
                 * TODO 超时处理
                 */
                logger.warning("timeout:{}, {}", timeout, message);
            } else
                super.doTargetMessage(message);
        }

        @Override
        protected void doFlowMessage(Object message) {
            askChild(message, timeout);
        }
    }


    static class RetryActor extends AsyncActor {
        int times;
        FiniteDuration duration;
        Object flowMsg;

        protected RetryActor(Function functor, int times, int interval) {
            super(functor);
            this.times = times;
            this.duration = Duration.create(interval, TimeUnit.SECONDS);
        }

        @Override
        protected void doTargetMessage(Object message) {
            if (message instanceof ReceiveTimeout) {
                times--;
                if (times > 0) {
                    doTriggerTarget();
                } else {
                    /**
                     * TODO 多次重试后依然失败
                     */
                    logger.warning("retry {}/{} still failed, message:{}", times, duration,
                            message);
                    report(error(message));
                }
            } else {
                super.doTargetMessage(message);
            }
        }

        @Override
        protected void doFlowMessage(Object message) {
            flowMsg = message;
            doTriggerTarget();
        }

        void doTriggerTarget() {
            super.doFlowMessage(flowMsg);
            context().setReceiveTimeout(duration);
        }
    }
}
