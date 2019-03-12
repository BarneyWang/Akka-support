package me.heng.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * note:   the class  that implements this IF will throw an Exception when be serialized
 * AUTHOR: wangdi
 * DATE: 2019-03-12
 * TIME: 10:58
 */
public interface Loggable <L extends Logger> {

    default L getLogger(String logName) {
        return (L)LoggerFactory.getLogger(logName);
    }

    default L getLogger() {
        return (L)LoggerFactory.getLogger(this.getClass());
    }
}
