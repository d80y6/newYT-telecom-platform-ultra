package com.yemenptc.bss.coreservice.exception;

public class BssException extends RuntimeException {
    public BssException(String message) { super(message); }
    public BssException(String message, Throwable cause) { super(message, cause); }
}
