package com.example.demo;

import java.util.Arrays;
import java.util.Random;

import javax.cache.Cache;
import javax.cache.configuration.FactoryBuilder;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.eviction.fifo.FifoEvictionPolicy;
import org.apache.ignite.cache.eviction.fifo.FifoEvictionPolicyFactory;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataPageEvictionMode;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IgniteDemo {
	final String cacheMode;
	final String runMode;
	Random random = new Random();
	Ignite ignite;

	public static void main(String[] args) throws Exception {
		String cacheMode = args[0];
		String runMode = args[1];
		new IgniteDemo(cacheMode, runMode).run();
	}

	public IgniteDemo(String cacheMode, String runMode) {
		this.cacheMode = cacheMode;
		this.runMode = runMode;
		log.info("CacheMode: {} RunMode {}", cacheMode, runMode);
	}

	private void run() throws Exception {
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setWorkDirectory(System.getProperty("java.io.tmpdir"));

		configureCluster(cfg);

		cfg.setIgniteInstanceName("demo");

		cfg.setCacheConfiguration(getCacheConfiguration("time"), getCacheConfiguration("elements"));

		// Start Ignite node.
		ignite = Ignition.start(cfg);

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
			ignite.close();
		}
	}

	private void configureCluster(IgniteConfiguration cfg) {
		// Explicitly configure TCP discovery SPI to provide list of initial nodes
		// from the first cluster.
		TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

		// Initial local port to listen to.
		discoverySpi.setLocalPort(48500);

		// Changing local port range. This is an optional action.
		discoverySpi.setLocalPortRange(20);

		TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();

		// Addresses and port range of the nodes from the first cluster.
		// 127.0.0.1 can be replaced with actual IP addresses or host names.
		// The port range is optional.
		ipFinder.setAddresses(Arrays.asList("127.0.0.1:48500..48520"));

		// Overriding IP finder.
		discoverySpi.setIpFinder(ipFinder);

		// Explicitly configure TCP communication SPI by changing local port number for
		// the nodes from the first cluster.
		TcpCommunicationSpi commSpi = new TcpCommunicationSpi();

		commSpi.setLocalPort(48100);

		// Overriding discovery SPI.
		cfg.setDiscoverySpi(discoverySpi);

		// Overriding communication SPI.
		cfg.setCommunicationSpi(commSpi);
	}

	private CacheConfiguration getCacheConfiguration(String name) {
		CacheConfiguration cfg = new CacheConfiguration<>(name);
		cfg.setCacheMode(CacheMode.REPLICATED);
		cfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_ASYNC);
		cfg.setStoreByValue(true);
		cfg.setCopyOnRead(false);
		cfg.setOnheapCacheEnabled(true);
		return cfg;
	}

	private void timeReader() {
		Cache<String, String> cache = ignite.cache("time");
		String time = "";
		while (true) {
			String newTime = cache.get("time");
			if (newTime != null && !newTime.equals(time)) {
				log.info("New time: {}", newTime);
				time = newTime;
			}
			sleep(100);
		}
	}

	private void timeWriter() {
		Cache<String, String> cache = ignite.cache("time");
		while (true) {
			String time = String.valueOf(System.currentTimeMillis());
			cache.put("time", time);
			log.info("time: {}", time);
			sleep(2_000);
		}
	}

	private void testWrite() {
		Cache<Integer, CacheElement> cache = ignite.cache("elements");
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
		Cache<Integer, CacheElement> cache = ignite.cache("elements");
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