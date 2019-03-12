package me.heng.akka;

import java.util.function.Function;

/**
 * AUTHOR: Di.W
 * DATE: 2019-03-12
 * TIME: 11:05
 */
public class FunctionActor<I, O> extends BaseActor {

    protected final Function<I, O> functor;
    protected final Function<Throwable, O> handler;

    public FunctionActor(Function<I, O> functor, Function<Throwable, O> exceptionHandler) {
        super();
        this.functor = functor;
        this.handler = exceptionHandler;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        logger.debug("received message:{}", message);
        if (message instanceof Throwable) {
            logger.warning("received throwable:{}", message);
        }
        Object result = null;
        try {
            result = functor.apply((I) message);
        } catch (ClassCastException e) {
            logger.error("received error typed message input:{}", message.getClass(), e);
            result = FunctorError.errorType(e);
        } catch (Throwable t) {
            logger.warning("functor error:{}", t.getMessage(), t);
            try {
                result = handler.apply(t);
            } catch (Throwable e) {
                logger.error("exception handler error:{}", t.getMessage(), t);
                result = FunctorError.throwable(e);
            }
        }
        /**
         * 将处理结果返回给 sender
         */
        if (result != null) {
            sender().tell(result, self());
        } else {
            sender().tell(FunctorError.NullValue, self());
        }
    }


}
