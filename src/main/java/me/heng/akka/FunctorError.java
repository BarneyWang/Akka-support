package me.heng.akka;

/**
 * Created by chuanbao on 5/8/2016 AD.
 */
public interface FunctorError {

    ReturnNull NullValue = new ReturnNull();

    static RunError throwable(Throwable t) {
        return new RunError(t);
    }

    static TypeError errorType(Throwable t) {
        return new TypeError(t);
    }

    class RunError extends RuntimeException implements FunctorError {
        protected RunError(Throwable throwable) {
            super(throwable);
        }
    }

    class TypeError extends RuntimeException implements FunctorError {
        protected TypeError(Throwable throwable) {
            super(throwable);
        }
    }

    class ReturnNull extends RuntimeException implements FunctorError {
        protected ReturnNull() {
            super(new NullPointerException("functor return null"));
        }
    }
}
