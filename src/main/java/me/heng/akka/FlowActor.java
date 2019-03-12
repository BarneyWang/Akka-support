package me.heng.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import java.util.List;

/**
 * AUTHOR: Di.W
 * DATE: 2019-03-12
 * TIME: 11:05
 */
public class FlowActor extends BaseActor {

    List<Props> nodeList;
    private int _cursor = 0;

    public FlowActor(List<Props> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        Object data = null;
        if (message instanceof Event) {
            data = ((Event) message).getData();
        } else
            data = message;
        if (sender().equals(context().parent())) {
            /**
             * 开始节点
             */
            if (!startNextNode(data)) {
                logger.error("no node found in this flow");
            }
        } else if (message instanceof Event.NodeEvent) {
            if (message instanceof Event.NodeError) {
                /**
                 * TODO 错误控制
                 */
                report(Event.FlowEvent.error( data));
            } else if (message instanceof Event.NodeEnd) {
                if (!startNextNode(data)) {
                    logger.info("no next node:{}", _cursor);
                    logger.info("flow end message:{}", data);
                    report(Event.FlowEvent.success(data));
                }
            }
        } else {
            logger.warning("unknown message:{}", message);
            unhandled(message);
        }
    }

    private boolean startNextNode(Object data) {
        if (nodeList.size() > _cursor) {
            Props props = nodeList.get(_cursor);
            String name = "node-" + _cursor + "-" + actorNameOf(props);
            ActorRef actor = context().actorOf(props, name);
            logger.info("start node:{}, message:{}", name, data);
            _cursor++;
            actor.tell(data, self());
            return true;
        }
        return false;
    }

    protected String actorNameOf(Props props) {
        return props.actorClass().getSimpleName();
    }
}
