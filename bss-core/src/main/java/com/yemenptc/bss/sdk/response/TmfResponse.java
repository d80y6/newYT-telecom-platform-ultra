package com.yemenptc.bss.sdk.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Standard TMF API response wrapper.
 * Supports pagination with @type, @baseType, href, total, offset, limit.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TmfResponse<T> {

    private String id;
    private String href;
    private String type;
    private String baseType;
    private Instant atBaseType;
    private Instant atSchemaLocation;
    private String name;
    private String description;

    // For list responses
    private Integer total;
    private Integer offset;
    private Integer limit;
    private List<T> items;

    // For single entity responses
    private T data;

    // For error responses
    private String status;
    private String code;
    private String reason;
    private String message;
    private String referenceError;

    /**
     * Creates a successful single entity response.
     */
    public static <T> TmfResponse<T> success(T data, String type) {
        return TmfResponse.<T>builder()
                .type(type)
                .data(data)
                .build();
    }

    /**
     * Creates a successful list response with pagination.
     */
    public static <T> TmfResponse<T> list(List<T> items, int total, int offset, int limit, String type) {
        return TmfResponse.<T>builder()
                .type(type)
                .items(items)
                .total(total)
                .offset(offset)
                .limit(limit)
                .build();
    }

    /**
     * Creates an error response following RFC 7807 Problem Details.
     */
    public static <T> TmfResponse<T> error(String status, String code, String reason, String message) {
        return TmfResponse.<T>builder()
                .status(status)
                .code(code)
                .reason(reason)
                .message(message)
                .build();
    }

    /**
     * Creates a not found error response.
     */
    public static <T> TmfResponse<T> notFound(String entityType, String id) {
        return error("404", "NOT_FOUND",
                entityType + " not found",
                "The requested " + entityType + " with id '" + id + "' does not exist");
    }

    /**
     * Creates a conflict error response.
     */
    public static <T> TmfResponse<T> conflict(String message) {
        return error("409", "CONFLICT", "Resource conflict", message);
    }

    /**
     * Creates a validation error response.
     */
    public static <T> TmfResponse<T> validationError(String message) {
        return error("400", "VALIDATION_ERROR", "Validation failed", message);
    }
}
