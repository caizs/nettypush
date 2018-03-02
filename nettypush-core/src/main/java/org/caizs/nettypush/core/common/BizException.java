package org.caizs.nettypush.core.common;

/**
 * 业务异常
 */
public class BizException extends RuntimeException {

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Exception e) {
        super(message, e);
    }
}
