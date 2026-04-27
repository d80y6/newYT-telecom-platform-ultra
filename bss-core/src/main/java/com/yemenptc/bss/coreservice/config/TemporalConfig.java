package com.yemenptc.bss.coreservice.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import com.yemenptc.bss.coreservice.temporal.OrderActivities;
import com.yemenptc.bss.coreservice.temporal.FtthOrderWorkflowImpl;
import com.yemenptc.bss.coreservice.temporal.MobileActivationWorkflowImpl;
import com.yemenptc.bss.coreservice.temporal.ProvisioningActivities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class TemporalConfig {

    @Value("${temporal.serviceAddress:localhost:7233}")
    private String serviceAddress;

    @Value("${temporal.namespace:default}")
    private String namespace;

    private final OrderActivities orderActivities;
    private final ProvisioningActivities provisioningActivities;

    public TemporalConfig(OrderActivities orderActivities, ProvisioningActivities provisioningActivities) {
        this.orderActivities = orderActivities;
        this.provisioningActivities = provisioningActivities;
    }

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newInstance(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(serviceAddress)
                        .build());
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(serviceStubs,
                WorkflowClientOptions.newBuilder()
                        .setNamespace(namespace)
                        .build());
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient) {
        return WorkerFactory.newInstance(workflowClient);
    }

    @PostConstruct
    public void startWorkers(WorkerFactory factory) {
        Worker worker = factory.newWorker("FTTH_ORDER_TASK_QUEUE");
        worker.registerWorkflowImplementationTypes(FtthOrderWorkflowImpl.class);
        worker.registerActivitiesImplementations(orderActivities);

        Worker mobileWorker = factory.newWorker("MOBILE_ACTIVATION_TASK_QUEUE");
        mobileWorker.registerWorkflowImplementationTypes(MobileActivationWorkflowImpl.class);
        mobileWorker.registerActivitiesImplementations(provisioningActivities);

        factory.start();
    }
}
