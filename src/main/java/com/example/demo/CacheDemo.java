package com.example.demo;

import java.util.Arrays;
import java.util.Random;


import javax.cache.Cache;
import javax.cache.CacheManager;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.monitor.LocalReplicatedMapStats;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheDemo {
	final String cacheMode;
	final String runMode;
	Random random = new Random();
	CacheManager cacheManager;
	HazelcastInstance hazel;

	public static void main(String[] args) throws Exception {
		String cacheMode = args[0];
		String runMode = args[1];
		new CacheDemo(cacheMode, runMode).run();
	}

	public CacheDemo(String cacheMode, String runMode) {
		this.cacheMode = cacheMode;
		this.runMode = runMode;
		log.info("CacheMode: {} RunMode {}", cacheMode, runMode);
	}

	private void run() {
		Config config = new Config();
		config.setProperty("hazelcast.logging.type", "slf4j");
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(
				Arrays.asList("localhost:5701", "localhost:5702", "localhost:5703", "localhost:5704", "localhost:5705"));

		ReplicatedMapConfig replicatedMapConfig = config.getReplicatedMapConfig("elements");
		replicatedMapConfig.setInMemoryFormat(InMemoryFormat.OBJECT);

		hazel = Hazelcast.newHazelcastInstance(config);

		if ("write".equals(runMode)) {
			testWrite();
		} else if ("read".equals(runMode)) {
			testRead();
		} else {
			log.error("Invalid runMode: {}", runMode);
		}
	}

	private void testWrite() {
//		Cache<Integer, CacheElement> cache = cacheManager.getCache("elements");
		ReplicatedMap<Integer, CacheElement> cache = hazel.getReplicatedMap("elements");
		while (true) {
			long count = 0;
			long start = System.currentTimeMillis();
			CacheElement cacheElement = new CacheElement(1);
			while (System.currentTimeMillis() - start < 5_000) {
				cache.put(random.nextInt(100), cacheElement);
				count++;
			}
			double time = System.currentTimeMillis() - start;
			log.info("Write Time: {} Count: {} ms/op: {}", time, count, (time / count));
		}
	}

	private void testRead() {
		ReplicatedMap<Integer, CacheElement> cache = hazel.getReplicatedMap("elements");
		while(true) {
			long count = 0;
			long sum = 0;
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 5_000) {
				CacheElement e = cache.get(random.nextInt(100));
				if (e != null) {
					sum += e.id;
				}
				count++;
			}
			double time = System.currentTimeMillis() - start;
			log.info("Read Time: {} Count: {} sum: {} ms/op: {}", time, count, sum, (time / count));
			LocalReplicatedMapStats stats = cache.getReplicatedMapStats();
//			log.info("Stats {}", stats);
		}
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}