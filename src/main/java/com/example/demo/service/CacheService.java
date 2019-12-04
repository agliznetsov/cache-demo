package com.example.demo.service;

import javax.cache.CacheManager;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.stats.Stats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CacheService {

	@Autowired
	CacheManager cacheManager;

	@Autowired
	EmbeddedCacheManager embeddedCacheManager;

	public void clearStats() {
		embeddedCacheManager.getCacheNames().forEach(this::clearStats);
	}

	public void clearStats(String name) {
		embeddedCacheManager.getCache(name, false).getAdvancedCache().getStats().reset();
	}

	public Stats getStats(String name) {
		return embeddedCacheManager.getCache(name, false).getAdvancedCache().getStats();
	}

	public void printStats() {
		embeddedCacheManager.getCacheNames().forEach(this::printStats);
	}

	public void printStats(String name) {
		Stats stats = getStats(name);
		log.info("Name: {} Hits: {} Misses: {} Size: {} Read time: {} Write time: {}", name, stats.getHits(), stats.getMisses(),
				stats.getCurrentNumberOfEntries(),
				stats.getAverageReadTime(), stats.getAverageWriteTime());
	}

}
