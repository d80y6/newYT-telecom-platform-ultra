package com.yemenptc.bss.coreservice.adapter.titan;

public class TitanConnectionException extends RuntimeException {
    public TitanConnectionException(String message) { super(message); }
    public TitanConnectionException(String message, Throwable cause) { super(message, cause); }
}
