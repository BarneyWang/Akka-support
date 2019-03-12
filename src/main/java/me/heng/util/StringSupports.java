package me.heng.util;

/**
 * AUTHOR: Di.W
 * DATE: 2019-03-12
 * TIME: 11:05
 */
public class StringSupports {

    /**
     * 格式化字符串
     *
     * @param fmt String
     * @param objs Object...
     * @return String
     */
    public static String format(String fmt, Object... objs) {
        String line = fmt;
        if (objs != null && objs.length > 0) {
            line = String.format(fmt, objs);
        }
        return line;
    }

}
