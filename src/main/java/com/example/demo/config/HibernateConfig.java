package com.example.demo.config;

import static com.example.demo.config.CacheNames.*;

import java.io.Serializable;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;
import javax.cache.spi.CachingProvider;


import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.cache.DebugCacheManager;
import com.example.demo.hazelcast.HazelCachingProvider;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HibernateConfig {

	@Bean
	public HazelcastInstance hazel() {
		Config config = new Config();
		config.setProperty("hazelcast.logging.type", "slf4j");
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setMembers(Arrays.asList("localhost:5701", "localhost:5702", "localhost:5703"));

		return Hazelcast.newHazelcastInstance(config);
	}

	@Bean
	public HazelCachingProvider cachingProvider(HazelcastInstance hazel) {
		return new HazelCachingProvider(hazel);
	}

	@Bean
	public CacheManager cacheManager(HazelCachingProvider cachingProvider) {
		return cachingProvider.getCacheManager();
	}

	@Bean
	public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(CacheManager cacheManager) {
		return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
	}
}



