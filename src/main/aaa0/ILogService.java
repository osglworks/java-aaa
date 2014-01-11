package org.osgl.aaa0;

public interface ILogService {
    void log(IAccount principal, boolean autoAck, String level, String message, Object... args);
}
