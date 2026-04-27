package com.yemenptc.bss.coreservice.exception;

public class ConcurrentChargingException extends BssException {
    public ConcurrentChargingException(String message) {
        super(message);
    }

    public ConcurrentChargingException(String message, Throwable cause) {
        super(message, cause);
    }
}
