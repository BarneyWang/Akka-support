package me.heng.util;

/**
 * AUTHOR: Di.W
 * DATE: 2019-03-12
 * TIME: 11:05
 */
public class Exceptions {

    public static void fail(Throwable throwable) throws Exception {
        throw new Exception(throwable);
    }

    public static void fail(String fmt, Object... objs) throws Exception {
        fail(new Exception(), fmt, objs);
    }

    public static void fail(Throwable e, String fmt, Object... objs) throws Exception {
        throw new Exception(StringSupports.format(fmt, objs), e);
    }

    public static <T> T nothing(Throwable e) {
        // nothing
        return null;
    }

    /**
     * 不对RuntimeException类型处理
     *
     * @param e Throwable
     * @return
     * @throws RuntimeException
     */
    public static <T> T softThrow(Throwable e) throws RuntimeException {
        if (e instanceof RuntimeException) {
            throw  (RuntimeException) e;
        }
        throw new RuntimeException(e);
    }


    public static <T extends Exception> ExceptionHandler<T> awalysTrue() {
        return new ExceptionHandler<T>() {
            @Override
            public Boolean apply(T input) {
                return true;
            }
        };
    }

    public static <T extends Exception> ExceptionHandler<T> awalysFalse() {
        return new ExceptionHandler<T>() {
            @Override
            public Boolean apply(T input) {
                return false;
            }
        };
    }
}
