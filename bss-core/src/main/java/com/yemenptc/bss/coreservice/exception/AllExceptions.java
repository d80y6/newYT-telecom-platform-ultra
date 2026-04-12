package com.yemenptc.bss.coreservice.exception;

class AccountNotFoundException extends BssException {
    public AccountNotFoundException(String message) { super(message); }
}

class InsufficientBalanceException extends BssException {
    public InsufficientBalanceException(String message) { super(message); }
}

class InvoiceNotFoundException extends BssException {
    public InvoiceNotFoundException(String message) { super(message); }
}

class OrderNotFoundException extends BssException {
    public OrderNotFoundException(String message) { super(message); }
}

class OrderItemNotFoundException extends BssException {
    public OrderItemNotFoundException(String message) { super(message); }
}

class ProductNotFoundException extends BssException {
    public ProductNotFoundException(String message) { super(message); }
}

class PriceNotFoundException extends BssException {
    public PriceNotFoundException(String message) { super(message); }
}

class CategoryNotFoundException extends BssException {
    public CategoryNotFoundException(String message) { super(message); }
}

class InvalidProductException extends BssException {
    public InvalidProductException(String message) { super(message); }
}

class InvalidOrderException extends BssException {
    public InvalidOrderException(String message) { super(message); }
}

class InvalidOrderStateException extends BssException {
    public InvalidOrderStateException(String message) { super(message); }
}

class InvalidRechargeException extends BssException {
    public InvalidRechargeException(String message) { super(message); }
}

class RatePlanNotFoundException extends BssException {
    public RatePlanNotFoundException(String message) { super(message); }
}

class TariffNotFoundException extends BssException {
    public TariffNotFoundException(String message) { super(message); }
}

class ProvisioningException extends BssException {
    public ProvisioningException(String message) { super(message); }
    public ProvisioningException(String message, Throwable cause) { super(message, cause); }
}

class NumberNotAvailableException extends BssException {
    public NumberNotAvailableException(String message) { super(message); }
}

class NumberNotFoundException extends BssException {
    public NumberNotFoundException(String message) { super(message); }
}

class ElementNotFoundException extends BssException {
    public ElementNotFoundException(String message) { super(message); }
}

class AdapterNotFoundException extends BssException {
    public AdapterNotFoundException(String message) { super(message); }
}

class AdapterUnavailableException extends BssException {
    public AdapterUnavailableException(String message) { super(message); }
}

class TaskNotFoundException extends BssException {
    public TaskNotFoundException(String message) { super(message); }
}

class InvalidTaskStateException extends BssException {
    public InvalidTaskStateException(String message) { super(message); }
}

class TitanConnectionException extends BssException {
    public TitanConnectionException(String message) { super(message); }
    public TitanConnectionException(String message, Throwable cause) { super(message, cause); }
}

class TitanProvisioningException extends BssException {
    public TitanProvisioningException(String message) { super(message); }
}

class OracleBrmException extends BssException {
    public OracleBrmException(String message) { super(message); }
}
