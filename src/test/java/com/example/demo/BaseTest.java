package com.example.demo;

import java.util.function.Consumer;

import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class BaseTest {

	protected void measureTime(String name, int count, Consumer<Integer> operation) {
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			operation.accept(i);
		}
		long end = System.currentTimeMillis();
		long time = end - start;
		log.info(name + " total time: {}ms time per op: {}ms", time, time * 1.0 / count);
	}

	protected void tryCatch(Runnable runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			//nop
		}
	}

}
