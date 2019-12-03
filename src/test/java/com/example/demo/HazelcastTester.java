package com.example.demo;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.MutableConfiguration;



import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HazelcastTester {


	public static void main(String[] args) throws Exception {
//		Config hconfig = new Config();
//		hconfig.setProperty("hazelcast.logging.type", "slf4j");
//		hconfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
//		hconfig.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
//		hconfig.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(Arrays.asList("localhost:5701", "localhost:5702"));
//		HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hconfig);

		CacheManager cacheManager = Caching.getCachingProvider("com.hazelcast.cache.HazelcastCachingProvider")
				.getCacheManager();

		// Create a simple but typesafe configuration for the cache.
		CompleteConfiguration<Integer,String> config = new MutableConfiguration<>();

		// Create and get the cache.
		Cache<Integer,String> cluster = cacheManager.createCache("elements", config);

		String value = cluster.get(1);
		if (value == null) {
			System.out.println("value not found");
			cluster.put(1, "test");
		} else {
			System.out.println(" 1 = " + value);
		}
	}


	private static String randomString() {
		return UUID.randomUUID().toString();
	}

}
