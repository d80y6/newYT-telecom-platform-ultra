# Phase 3: Zero-Touch Automation Implementation Plan

## Objective
To expand the platform's automated provisioning capabilities to achieve >95% zero-touch fulfillment using resilient Temporal workflows.

## Execution Steps

### 1. Workflow Library Expansion
- **Target:** `ProvisioningOrchestrator` / `bss-core/src/main/java/com/yemenptc/bss/coreservice/temporal`
- **Action:**
    - Create `MobileActivationWorkflow`: Automate SIM activation, MSISDN allocation, and rate-plan association.
    - Create `EnterpriseConnectivityWorkflow`: Standardize complex fiber/link setup orchestration with integrated SLA monitoring.
    - Implement `RetryOptions` for all `OrderActivities` to handle transient network/resource failures.

### 2. Intelligent Routing & Exception Handling
- **Target:** `ZeroTouchAutomationService.java`
- **Action:**
    - Develop an `OrderRouter` that maps incoming service requests to specific workflows based on `ProductOffering` traits.
    - Implement an `ExceptionResolver` that distinguishes between "retryable" workflow failures (e.g., resource locked) and "terminal" errors (e.g., invalid data), escalating the latter to manual queues via `TroubleTicket`.

### 3. Monitoring & Automation Dashboard
- **Target:** `ZeroTouchAutomationService.java` (Enhancement)
- **Action:**
    - Add metrics for "Auto-Provisioning Success Rate" (APSR).
    - Implement a "Stalled Workflow" detector to identify orders stuck in intermediate states.

## Verification
- **E2E Automation Suite:** Create an automated integration test suite in `tests/load/provisioning_stress.py` to trigger 1,000+ orders.
- **Chaos Injection:** Use Istio/Kubernetes to simulate network partitions during an active workflow; verify that Temporal resumes correctly.

---
*Implementation beginning immediately.*
