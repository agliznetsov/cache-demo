package com.example.demo.service;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.management.CacheStatisticsMXBean;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.ehcache.config.CacheRuntimeConfiguration;
import org.ehcache.config.ResourceType;
import org.ehcache.config.SizedResourcePool;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CacheService {

	@Autowired
	CacheManager cacheManager;

	private List<String> cacheNames;

	public CacheStatisticsMXBean getCacheStatisticsMXBean(final String cacheName) {
		final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = null;
		try {
			name = new ObjectName("*:type=CacheStatistics,*,Cache=" + cacheName);
		} catch (MalformedObjectNameException ex) {
			throw new RuntimeException(ex);
		}
		Set<ObjectName> beans = mbeanServer.queryNames(name, null);
		if (beans.isEmpty()) {
			log.debug("Cache Statistics Bean not found");
			return null;
		}
		ObjectName[] objArray = beans.toArray(new ObjectName[beans.size()]);
		return JMX.newMBeanProxy(mbeanServer, objArray[0], CacheStatisticsMXBean.class);
	}

	public Map<String,CacheStatisticsMXBean> getCacheStatisticsMXBeans() {
		final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = null;
		try {
			name = new ObjectName("*:type=CacheStatistics,*");
		} catch (MalformedObjectNameException ex) {
			throw new RuntimeException(ex);
		}
		Set<ObjectName> beans = mbeanServer.queryNames(name, null);
		if (beans.isEmpty()) {
			log.debug("Cache Statistics Bean not found");
			return null;
		}
		Map<String,CacheStatisticsMXBean> map = new HashMap<>();
		beans.forEach(it -> map
				.put(it.getKeyProperty("Cache"), JMX.newMBeanProxy(mbeanServer, it, CacheStatisticsMXBean.class)));
		return map;
	}

	public void clearStats() {
		getCacheStatisticsMXBeans().values().forEach(it -> it.clear());
	}

	public Map<String,CacheStats> getStats() {
		Map<String,CacheStats> map = new HashMap<>();
		getCacheNames().forEach(it -> map.put(it, getCacheStats(cacheManager.getCache(it))));
		return map;
	}

	private List<String> getCacheNames() {
		if (cacheNames == null) {
			cacheNames = new ArrayList<>();
			cacheManager.getCacheNames().forEach(it -> cacheNames.add(it));
		}
		return cacheNames;
	}

	public void printStats() {
		Map<String,CacheStats> stats = getStats();
		stats.keySet().stream().sorted(Comparator.comparing(it -> it)).forEach(key -> log.info(key + " " + stats.get(key)));
	}

	private CacheStats getCacheStats(Cache cache) {
		CacheStatisticsMXBean mbean = getCacheStatisticsMXBean(cache.getName());

		Eh107Configuration ehConfig = (Eh107Configuration) cache.getConfiguration(Eh107Configuration.class);
		CacheRuntimeConfiguration runtimeConfig = (CacheRuntimeConfiguration) ehConfig
				.unwrap(CacheRuntimeConfiguration.class);

		return createStats(mbean, runtimeConfig);
	}


	private CacheStats createStats(CacheStatisticsMXBean mbean, CacheRuntimeConfiguration runtimeConfig) {
		SizedResourcePool heap = runtimeConfig.getResourcePools().getPoolForResource(ResourceType.Core.HEAP);
		SizedResourcePool offheap = runtimeConfig.getResourcePools().getPoolForResource(ResourceType.Core.OFFHEAP);
		CacheStats.CacheStatsBuilder builder = CacheStats.builder();

		builder.heapSize(heap == null ? null : heap.getSize())
				.offHeapSize(offheap == null ? null : offheap.getSize());

		if (mbean != null) {
			builder.gets(mbean.getCacheGets())
					.puts(mbean.getCachePuts())
					.hits(mbean.getCacheHits())
					.misses(mbean.getCacheMisses());
		}

		return builder.build();
	}


	@ToString
	@Getter
	@Builder
	public static class CacheStats {
		Long gets;
		Long puts;
		Long hits;
		Long misses;
		Long heapSize;
		Long offHeapSize;
	}
}
