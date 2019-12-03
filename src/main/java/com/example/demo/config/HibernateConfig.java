package com.example.demo.config;

import static com.example.demo.config.CacheNames.*;

import java.io.Serializable;
import java.net.URI;
import java.time.Duration;
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

import org.ehcache.PersistentCacheManager;
import org.ehcache.clustered.client.config.ClusteredStoreConfiguration;
import org.ehcache.clustered.client.config.ClusteringServiceConfiguration;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteredStoreConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ServerSideConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.TimeoutsBuilder;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.expiry.Expirations;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.cache.DebugCacheManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HibernateConfig {

	@Value("${demo.cache.tc-host:localhost}")
	String tcHost;

	@Value("${demo.cache.consistency:EVENTUAL}")
	Consistency cacheConsistency;

	//	@Bean
//	public CacheManager simpleCacheManager() {
//		long heapSize = 1000;
//        long sizeInMb = 100;
//
//		CacheManager cacheManager = getCachingProvider().getCacheManager();
//
//		CacheConfiguration<Serializable, Object> cacheConfiguration = CacheConfigurationBuilder
//				.newCacheConfigurationBuilder(Serializable.class, Object.class, ResourcePoolsBuilder.heap(heapSize).offheap(sizeInMb, MemoryUnit.MB))
//				.build();
//
//		javax.cache.configuration.Configuration<Serializable,Object> config = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration);
//
//		cacheManager.createCache(ASSET, config);
//		cacheManager.createCache(ASSET_TYPE, config);
//		cacheManager.createCache(RESOURCE, config);
//		cacheManager.createCache(ASSET_FIND_BY_NAME, config);
//		cacheManager.createCache(RESOURCE_FIND_BY_NAME, config);
//		cacheManager.createCache(COMMUNITY, config);
//		cacheManager.createCache(COMMUNITY_SUB_COMMUNITIES, config);
//
//		return cacheManager;
//	}

	@Bean
	public CacheManager createClusteredCacheManager() {
		URI clusterUri = URI.create("terracotta://" + tcHost + ":9410/dgc");
		log.info("Cluster URI: {}", clusterUri);
		log.info("Cache consistency: {}", cacheConsistency);
		long clusterSizeInMb = 1;
		long heapSize = 1000;

		Duration duration = Duration.ofSeconds(5);
		//Changes in the resource pool configuration requires TC server restart!
		ClusteringServiceConfiguration clusterConfig = ClusteringServiceConfigurationBuilder
				.cluster(clusterUri).timeouts(TimeoutsBuilder.timeouts().connection(duration).read(duration).write(duration).build())
				.autoCreate(b -> b
						//.defaultServerResource("offheap-1")
						.resourcePool("shared", clusterSizeInMb, MemoryUnit.MB, "offheap-1"))
				.build();

		org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(Object.class, Object.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(heapSize)
						//.offheap(offHeapSizeInMb, MemoryUnit.MB)
//						.with(ClusteredResourcePoolBuilder.clusteredDedicated("offheap-1", clusterSizeInMb, MemoryUnit.MB)))
						.with(ClusteredResourcePoolBuilder.clusteredShared("shared")))
				.withService(ClusteredStoreConfigurationBuilder.withConsistency(cacheConsistency))
				.build();
		javax.cache.configuration.Configuration<Object, Object> config = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration);

		EhcacheCachingProvider provider = getCachingProvider();
		DefaultConfiguration configuration = new DefaultConfiguration(provider.getDefaultClassLoader(), clusterConfig);
		CacheManager cacheManager = provider.getCacheManager(provider.getDefaultURI(), configuration);

		cacheManager.createCache(ASSET, config);
		cacheManager.createCache(ASSET_TYPE, config);
		cacheManager.createCache(RESOURCE, config);
		cacheManager.createCache(ASSET_FIND_BY_NAME, config);
		cacheManager.createCache(RESOURCE_FIND_BY_NAME, config);
		cacheManager.createCache(COMMUNITY, config);
		cacheManager.createCache(COMMUNITY_SUB_COMMUNITIES, config);

		//all other caches will use unbounded heap resource pool !

		return cacheManager;
	}

	private EhcacheCachingProvider getCachingProvider() {
		return (EhcacheCachingProvider) Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
	}


	@Bean
	public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(CacheManager cacheManager) {
		return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
	}
}



