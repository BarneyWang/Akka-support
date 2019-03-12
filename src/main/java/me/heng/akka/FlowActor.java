package me.heng.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.alibaba.ais.scrm.task.akka.Event.FlowEvent;
import com.alibaba.ais.scrm.task.akka.Event.NodeEnd;
import com.alibaba.ais.scrm.task.akka.Event.NodeError;
import com.alibaba.ais.scrm.task.akka.Event.NodeEvent;

import java.util.List;

/**
 * Created by chuanbao on 5/8/2016 AD.
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
        } else if (message instanceof NodeEvent) {
            if (message instanceof NodeError) {
                /**
                 * TODO 错误控制
                 */
                report(FlowEvent.error( data));
            } else if (message instanceof NodeEnd) {
                if (!startNextNode(data)) {
                    logger.info("no next node:{}", _cursor);
                    logger.info("flow end message:{}", data);
                    report(FlowEvent.success(data));
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
