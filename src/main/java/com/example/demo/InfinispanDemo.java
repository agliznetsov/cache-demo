package com.example.demo;

import java.util.Random;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.global.TransportConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfinispanDemo {
	final String cacheMode;
	final String runMode;
	Random random = new Random();
	DefaultCacheManager cacheManager;

	public static void main(String[] args) throws Exception {
		String cacheMode = args[0];
		String runMode = args[1];
		new InfinispanDemo(cacheMode, runMode).run();
	}

	public InfinispanDemo(String cacheMode, String runMode) {
		this.cacheMode = cacheMode;
		this.runMode = runMode;
		log.info("CacheMode: {} RunMode {}", cacheMode, runMode);
	}

	private void run() {
		// Setup up a clustered cache manager
		TransportConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder()
				.transport().clusterName("demo").addProperty("configurationFile", "jgroups-tcp.xml");

		// Make the default cache a replicated synchronous one
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.clustering().cacheMode(CacheMode.valueOf(cacheMode));

		// Initialize the cache manager
		cacheManager = new DefaultCacheManager(global.build(), builder.build());

		log.info("Cluster name: {}", cacheManager.getClusterName());
		log.info("Cluster size: {}", cacheManager.getClusterSize());
		log.info("Cluster members: {}", cacheManager.getClusterMembers());

		if ("timeWriter".equals(runMode)) {
			timeWriter();
		} else if ("timeReader".equals(runMode)) {
			timeReader();
		} else if ("write".equals(runMode)) {
			testWrite();
		} else if ("read".equals(runMode)) {
			testRead();
		} else {
			log.error("Invalid runMode: {}", runMode);
			cacheManager.stop();
		}
	}

	private void timeReader() {
		Cache<String, String> cache = cacheManager.getCache("time");
		while (true) {
			String time = cache.get("time");
			if (time == null) {
				time = String.valueOf(System.currentTimeMillis());
				cache.put("time", time);
				log.info("New time: {}", time);
			}
			sleep(100);
		}
	}

	private void timeWriter() {
		Cache<String, String> cache = cacheManager.getCache("time");
		while (true) {
			String time = String.valueOf(System.currentTimeMillis());
			cache.put("time", time);
			log.info("time: {}", time);
			sleep(2_000);
		}
	}

	private void testWrite() {
		Cache<Integer, CacheElement> cache = cacheManager.getCache("elements");
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
		Cache<Integer, CacheElement> cache = cacheManager.getCache("elements");
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