package demo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HazelcastBenchmarkTest {

	CacheManager cacheManager;
	Cache<Integer,CacheElement> cluster;
	Random random = new Random();

	@BeforeEach
	void setUp() {
		Config hconfig = new Config();
		hconfig.setProperty("hazelcast.logging.type", "slf4j");
		hconfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		hconfig.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		hconfig.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(
				Arrays.asList("localhost:5701", "localhost:5702"));
		HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hconfig);

		cacheManager = Caching.getCachingProvider("com.hazelcast.cache.HazelcastCachingProvider")
				.getCacheManager();

		// Create a simple but typesafe configuration for the cache.
		CompleteConfiguration<Integer,CacheElement> config = new MutableConfiguration<>();

		// Create and get the cache.
		cluster = cacheManager.createCache("elements", config);

//		for (int i = 0; i < 10; i++) {
//			CacheElement e = new CacheElement(i);
//			cluster.put(e.id, e);
//		}
	}

	@AfterEach
	public void tearDown() {
		cluster.close();
		cacheManager.close();
	}

	@Test
	void test_write() {
		for (int i = 0; i < 100; i++) {
			testWriteIteration();
		}
	}

	@Test
	void test_read() {
		for (int i = 0; i < 100; i++) {
			testReadIteration();
		}
	}

	@Test
	void test_write_read() {
		for (int i = 0; i < 100; i++) {
			testWriteIteration();
			testReadIteration();
		}
	}

	private void testWriteIteration() {
		long count = 0;
		long sum = 0;
		long start = System.currentTimeMillis();
		CacheElement cacheElement = new CacheElement(1);
		while (System.currentTimeMillis() - start < 5_000) {
			cluster.put(random.nextInt(100), cacheElement);
			count++;
		}
		double time = System.currentTimeMillis() - start;
		log.info("Write Time: {} Count: {} sum: {} ms/op: {}", time, count, sum, (time / count));
	}

	private void testReadIteration() {
		long count = 0;
		long sum = 0;
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < 5_000) {
			CacheElement e = cluster.get(random.nextInt(100));
			if (e != null) {
				sum += e.id;
			}
			count++;
		}
		double time = System.currentTimeMillis() - start;
		log.info("Read Time: {} Count: {} sum: {} ms/op: {}", time, count, sum, (time / count));
	}

	public static class CacheElement implements Serializable {
		public final Integer id;
		public String value1 = randomString();
		public String value2 = randomString();
		public String value3 = randomString();
		public String value4 = randomString();
		public String value5 = randomString();

		public CacheElement(Integer id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "CacheElement{" +
					"id='" + id + '\'' +
					", value1='" + value1 + '\'' +
					", value2='" + value2 + '\'' +
					", value3='" + value3 + '\'' +
					", value4='" + value4 + '\'' +
					", value5='" + value5 + '\'' +
					'}';
		}
	}

	private static String randomString() {
		return UUID.randomUUID().toString();
	}

}
