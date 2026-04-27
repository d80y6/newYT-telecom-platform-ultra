package com.yemenptc.bss.coreservice.orchestration;

import com.yemenptc.bss.coreservice.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvisioningResult {
    private String orderId;
    private String status;
    private String errorCode;
    private String message;
    private List<Order.StepResult> steps;

    public static ProvisioningResult success(String orderId, List<Order.StepResult> steps) {
        return ProvisioningResult.builder()
                .orderId(orderId)
                .status("SUCCESS")
                .steps(steps)
                .build();
    }

    public static ProvisioningResult failure(String orderId, String errorCode, String message, List<Order.StepResult> steps) {
        return ProvisioningResult.builder()
                .orderId(orderId)
                .status("FAILURE")
                .errorCode(errorCode)
                .message(message)
                .steps(steps)
                .build();
    }
}
