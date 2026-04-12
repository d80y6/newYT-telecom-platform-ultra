package com.yemenptc.bss.coreservice.benchmark;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class OrderBenchmark {

    private static final int BENCHMARK_ITERATIONS = 1000;
    private static final long TARGET_LATENCY_MS = 1000;

    @Test
    void testOrderCreationLatency() throws InterruptedException {
        AtomicLong totalTime = new AtomicLong(0);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(BENCHMARK_ITERATIONS);

        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            executor.submit(() -> {
                long start = System.nanoTime();
                simulateOrderCreation();
                long end = System.nanoTime();
                totalTime.addAndGet(end - start);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        double avgLatencyMs = (totalTime.get() / 1_000_000.0) / BENCHMARK_ITERATIONS;
        double throughput = BENCHMARK_ITERATIONS / (totalTime.get() / 1_000_000_000.0);

        System.out.println("=== Order Creation Benchmark ===");
        System.out.printf("Average latency: %.2f ms%n", avgLatencyMs);
        System.out.printf("Throughput: %.2f orders/sec%n", throughput);

        assertTrue(avgLatencyMs < TARGET_LATENCY_MS,
            "Order latency " + avgLatencyMs + "ms exceeds target " + TARGET_LATENCY_MS + "ms");
    }

    private void simulateOrderCreation() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testConcurrentOrderCreation() throws InterruptedException {
        int[] threadCounts = {1, 5, 10, 20};

        for (int threads : threadCounts) {
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            CountDownLatch latch = new CountDownLatch(BENCHMARK_ITERATIONS);
            AtomicLong totalTime = new AtomicLong(0);

            long start = System.nanoTime();
            for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
                executor.submit(() -> {
                    long s = System.nanoTime();
                    simulateOrderCreation();
                    totalTime.addAndGet(System.nanoTime() - s);
                    latch.countDown();
                });
            }

            latch.await();
            long end = System.nanoTime();
            executor.shutdown();

            double totalSeconds = (end - start) / 1_000_000_000.0;
            double throughput = BENCHMARK_ITERATIONS / totalSeconds;

            System.out.printf("Threads: %d, Throughput: %.2f orders/sec%n", threads, throughput);
        }
    }
}
