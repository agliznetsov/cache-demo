package com.example.demo.config;

import static com.example.demo.config.CacheNames.ASIDE;
import static com.example.demo.config.CacheNames.ASSET;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Properties;

import javax.cache.CacheManager;
import javax.cache.Caching;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.eviction.fifo.FifoEvictionPolicyFactory;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataPageEvictionMode;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.hibernate.cache.jcache.ConfigSettings;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.ignite.CachingProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HibernateConfig {

	@Value("${demo.cache.consistency:REPL_SYNC}")
	String cacheConsistency;

	@Bean
	public CacheManager cacheManager(Ignite ignite) {
		return new CachingProvider(ignite).getCacheManager();
	}

	@Bean
	public org.springframework.cache.CacheManager springCacheManager(CacheManager cacheManager) {
		return new JCacheCacheManager(cacheManager);
	}

	@Bean
	public Ignite ignite() {
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setIgniteInstanceName("demo");
		cfg.setWorkDirectory(System.getProperty("java.io.tmpdir"));

		configureCluster(cfg);
		//configureStorage(cfg);
		configureCaches(cfg);

		// Start Ignite node.
		return Ignition.start(cfg);
	}

	private void configureCaches(IgniteConfiguration cfg) {
		cfg.setCacheConfiguration(getCacheConfiguration(ASSET, 100), getCacheConfiguration(ASIDE, 10));
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
		ipFinder.setAddresses(Arrays.asList("192.168.1.20:48500..48520", "192.168.1.19:48500..48520"));

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

	private void configureStorage(IgniteConfiguration cfg) {
		// Durable Memory configuration.
		DataStorageConfiguration storageCfg = new DataStorageConfiguration();

		// Creating a new data region.
		DataRegionConfiguration regionCfg = new DataRegionConfiguration();

		// Region name.
		regionCfg.setName("default");

		// 10 MB initial size (RAM).
		regionCfg.setInitialSize(10L * 1024 * 1024);

		// 20 MB max size (RAM).
		regionCfg.setMaxSize(20L * 1024 * 1024);

		// Enabling RANDOM_LRU eviction for this region.
		regionCfg.setPageEvictionMode(DataPageEvictionMode.RANDOM_LRU);

		// Setting the data region configuration.
		storageCfg.setDataRegionConfigurations(regionCfg);

		cfg.setDataStorageConfiguration(storageCfg);
	}

	private CacheConfiguration getCacheConfiguration(String name, int size) {
		CacheConfiguration cfg = new CacheConfiguration<>(name);
		cfg.setStatisticsEnabled(true);
		cfg.setCacheMode(CacheMode.REPLICATED);
		cfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
		cfg.setStoreByValue(true);
		cfg.setCopyOnRead(false);
		cfg.setOnheapCacheEnabled(true);
		cfg.setEvictionPolicyFactory(new FifoEvictionPolicyFactory(size));
		return cfg;
	}

	@Bean
	public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(CacheManager cacheManager) {
		return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
	}

}



