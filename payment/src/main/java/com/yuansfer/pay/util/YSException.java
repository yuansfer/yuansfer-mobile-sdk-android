package com.yuansfer.pay.util;

public class YSException extends RuntimeException {
    public YSException() {
    }

    public YSException(String message) {
        super(message);
    }

    public YSException(String message, Throwable cause) {
        super(message, cause);
    }

    public YSException(Throwable cause) {
        super(cause);
    }

}
