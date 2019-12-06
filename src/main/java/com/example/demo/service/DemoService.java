package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class DemoService implements CommandLineRunner {
    @Autowired
    AssetService assetService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5);

    @Override
    public void run(String... args) throws Exception {
        executorService.submit(this::writeLoop);
    }

    private void writeLoop() {
        while (true) {
            long count = 0;
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 1_000) {
                assetService.saveAssets(ids);
                count++;
            }
            double time = System.currentTimeMillis() - start;
            log.info("Write Time: {} Count: {} ms/op: {}", time, count, (time / count));
        }
    }

}
