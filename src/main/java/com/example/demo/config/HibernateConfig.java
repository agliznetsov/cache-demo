package com.example.demo.config;

import java.io.Serializable;
import java.net.URI;
import java.util.Properties;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.hibernate.cache.jcache.ConfigSettings;
import org.infinispan.Cache;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.StorageType;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.configuration.global.TransportConfigurationBuilder;
import org.infinispan.eviction.EvictionType;
import org.infinispan.jcache.embedded.JCacheManager;
import org.infinispan.jcache.embedded.JCachingProvider;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HibernateConfig {

	@Value("${demo.cache.consistency:REPL_SYNC}")
	String cacheConsistency;

	@Value("${demo.cache.clustered:false}")
	Boolean clustered;

	@Bean
	public CacheManager cacheManager(EmbeddedCacheManager embeddedCacheManager) {
		return new EmbeddedCachingProvider(embeddedCacheManager).getCacheManager();
	}

	@Bean
	public EmbeddedCacheManager embeddedCacheManager() {
		GlobalConfigurationBuilder globalConfig;
		DefaultCacheManager cacheManager;

		// Make the default cache a replicated synchronous one
		ConfigurationBuilder cacheConfig = new ConfigurationBuilder();
		cacheConfig.jmxStatistics().enable();
		cacheConfig.memory()
				.storageType(StorageType.OBJECT)
				.size(10)
				.evictionType(EvictionType.COUNT);

		// Transactional configuration
		ConfigurationBuilder txConfig = new ConfigurationBuilder();
		txConfig.jmxStatistics().enable();
		txConfig.transaction().transactionMode(TransactionMode.TRANSACTIONAL).lockingMode(LockingMode.PESSIMISTIC);
		txConfig.memory()
				.storageType(StorageType.OBJECT)
				.size(10)
				.evictionType(EvictionType.COUNT);

		if (clustered) {
			// Initialize clustered cache manager
			globalConfig = GlobalConfigurationBuilder.defaultClusteredBuilder();
			globalConfig.transport().clusterName("demo").addProperty("configurationFile", "jgroups-udp.xml");
			cacheConfig.clustering().cacheMode(CacheMode.valueOf(cacheConsistency));
		} else {
			// Initialize local cache manager
			globalConfig = new GlobalConfigurationBuilder().defaultCacheName("default-cache");
		}

		globalConfig.defaultCacheName("default-cache");
		globalConfig.globalJmxStatistics().enable();
		globalConfig.serialization().marshaller(new SimpleSerializationMarshaller());

		cacheManager = new DefaultCacheManager(globalConfig.build(), cacheConfig.build());

		cacheManager.defineConfiguration("tx", txConfig.build());

		cacheManager.getCache(CacheNames.ASIDE, "tx");

		log.info("Cluster name: {}", cacheManager.getClusterName());
		log.info("Cluster size: {}", cacheManager.getClusterSize());
		log.info("Cluster members: {}", cacheManager.getClusterMembers());

		return cacheManager;
	}

	@Bean
	public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(CacheManager cacheManager) {
		return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
	}

	public static class EmbeddedCachingProvider extends JCachingProvider {
		private final EmbeddedCacheManager embeddedCacheManager;

		public EmbeddedCachingProvider(EmbeddedCacheManager embeddedCacheManager) {
			this.embeddedCacheManager = embeddedCacheManager;
		}

		@Override
		protected CacheManager createCacheManager(ClassLoader classLoader, URI uri, Properties properties) {
			return new JCacheManager(uri, embeddedCacheManager, this);
		}
	}

}



