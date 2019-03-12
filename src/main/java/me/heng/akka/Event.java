package me.heng.akka;

import akka.japi.Creator;

import java.io.Serializable;

/**
 * Created by chuanbao on 5/8/2016 AD.
 */
public interface Event {

    Object getData();

    class NodeEvent implements Event, Serializable {
        final Object data;

        NodeEvent(Object data) {
            this.data = data;
        }

        @Override
        public Object getData() {
            return data;
        }

        static NodeStart start(Object data) {
            return new NodeStart(data);
        }

        static NodeEnd success(Object data) {
            return new NodeEnd(data);
        }

        static NodeError error(Object data) {
            return new NodeError(data);
        }
    }

    class FlowEvent implements Event, Serializable {
        final Object data;

        FlowEvent(Object data) {
            this.data = data;
        }

        @Override
        public Object getData() {
            return data;
        }

        static FlowStart start(Creator<FlowActor> creator, Object data) {
            return new FlowStart(creator, data);
        }

        static FlowEnd success(Object data) {
            return new FlowEnd(data);
        }

        public static FlowEvent error(Object data) {
            return new FlowError(data);
        }
    }

    class FlowStart extends FlowEvent {

        final Creator<FlowActor> flowActorCreator;

        FlowStart(Creator<FlowActor> flowActorCreator, Object flowData) {
            super(flowData);
            this.flowActorCreator = flowActorCreator;
        }

        Creator<FlowActor> getCreator() {
            return flowActorCreator;
        }
    }

    class FlowEnd extends FlowEvent {
        FlowEnd(Object data) {
            super(data);
        }
    }

    class FlowError extends FlowEvent {
        FlowError(Object data) {
            super(data);
        }
    }

    class NodeStart extends NodeEvent {
        NodeStart(Object data) {
            super(data);
        }
    }

    class NodeEnd extends NodeEvent {
        NodeEnd(Object data) {
            super(data);
        }
    }

    class NodeError extends NodeEvent {
        NodeError(Object data) {
            super(data);
        }
    }

}
