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
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HazelcastTester {


	public static void main(String[] args) throws Exception {
		Config config = new Config();
		config.setProperty("hazelcast.logging.type", "slf4j");
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(Arrays.asList("localhost:5701", "localhost:5702", "localhost:5703", "localhost:5704", "localhost:5705"));

		ReplicatedMapConfig replicatedMapConfig = config.getReplicatedMapConfig("elements");
		replicatedMapConfig.setInMemoryFormat(InMemoryFormat.OBJECT);

		HazelcastInstance hazel = Hazelcast.newHazelcastInstance(config);

//		hazel.getCacheManager().getCache()
//
		CacheManager cacheManager = Caching.getCachingProvider("com.hazelcast.cache.HazelcastCachingProvider").getCacheManager();
//
//		// Create a simple but typesafe configuration for the cache.
//		CompleteConfiguration<Integer,String> config = new MutableConfiguration<>();
//
//		// Create and get the cache.
//		Cache<Integer,String> cluster = cacheManager.createCache("elements", config);

		ReplicatedMap<Integer, String> map = hazel.getReplicatedMap("elements");


		Cache<Integer, String> cache = cacheManager.getCache("elements");
//		if (cache == null) {
//			cache = hazel.getCacheManager().cr
//		}


		String value = cache.get(1);

		if (value == null) {
			System.out.println("value not found");
		} else {
			System.out.println("1 = " + value);
		}

		hazel.shutdown();
	}

}
