package me.heng.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * AUTHOR: Di.W
 * DATE: 2019-03-12
 * TIME: 11:05
 */
public abstract class BaseActor extends UntypedActor {

    protected final LoggingAdapter logger = Logging.getLogger(context().system(), this);

    /**
     * 向parent 报告消息
     * 
     * @param msg
     */
    protected void report(Event msg) {
        context().parent().tell(msg, self());
    }

    /**
     * 杀死子actor
     * 
     * @param actor
     */
    protected void killChild(ActorRef actor) {
        context().stop(actor);
    }

    protected ActorRef newChild(Props props) {
        return context().actorOf(props);
    }

    protected ActorSystem system() {
        return context().system();
    }
}
