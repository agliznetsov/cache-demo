package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import javax.transaction.TransactionManager;

import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.config.CacheNames;
import com.example.demo.service.AssetCache;
import com.example.demo.service.AssetService;
import com.example.demo.service.CacheService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AssetServiceTester extends BaseTest {

	@Autowired
	AssetService assetService;
	@Autowired
	CacheService cacheService;
	@Autowired
	CacheManager cacheManager;
	@Autowired
	AssetCache assetCache;
	@Autowired
	EmbeddedCacheManager embeddedCacheManager;
	@Autowired
	org.springframework.cache.CacheManager springCacheManager;

	@BeforeEach
	void setUp() {
		assetService.deleteAll();
		assetCache.clear();
	}

	@Test
	void testCreate() {
		assetService.createAsset(1);
		assertTrue(assetService.findById(1).isPresent());
	}

	@Test
	void testCreateRollback() {
		tryCatch(() -> assetService.createAssetAndFail(1));
		assertFalse(assetService.findById(1).isPresent());
	}

	@Test
	void testUpdate() {
		assetService.createAsset(1);
		assertEquals("name1", assetService.getAsset(1).getName());
		assetService.updateAsset(1, "new");
		assertEquals("new", assetService.getAsset(1).getName());
	}

	@Test
	void testUpdateAndFail() {
		assetService.createAsset(1);
		assertEquals("name1", assetService.getAsset(1).getName());
		tryCatch(() -> assetService.updateAssetAndFail(1, "new"));
		assertEquals("name1", assetService.getAsset(1).getName());
	}

	@Test
	void testCreateEvent() {
		assertEquals(0, assetCache.getEvents().size());

		assetService.createAsset(1, "test", null);
		assertEquals(1, assetCache.getEvents().size());

		tryCatch(() -> assetService.createAsset(2, "test", null));
		assertEquals(1, assetCache.getEvents().size());
	}

	@Test
	void testCache() {
		Random random = new Random();

		for (int i = 0; i < 10; i++) {
			assetService.createAsset(i);
		}

		cacheService.clearStats();

		long sum = 0;
		for (int i = 0; i < 100; i++) {
			sum += assetService.findById(random.nextInt(10)).get().getId();
		}
		log.info("Sum: {}", sum);
		cacheService.printStats();
	}

	@Test
	void testCacheAsideCommit() {
		String key = UUID.randomUUID().toString();
		assetService.cachePut(key, "value", false);
		assertEquals("value", assetService.cacheGet(key));
	}

	@Test
	@Disabled // not working because infinispan use JTA txManager and not spring one
	void testCacheAsideRollback() {
		String key = UUID.randomUUID().toString();
		tryCatch(() -> assetService.cachePut(key, "value", true));
		assertNull(assetService.cacheGet(key));
	}

	@Test
	void testTxManager() throws Exception {
		String key = UUID.randomUUID().toString();
		org.infinispan.Cache<Object,Object> cache = embeddedCacheManager.getCache(CacheNames.ASIDE);
		TransactionManager tm = cache.getAdvancedCache().getTransactionManager();
		tm.begin();
		cache.put(key, "value");
		tm.rollback();
		assertNull(cache.get(key));
	}

	@Test
	void testCacheLimit() {
		Cache cache = cacheManager.getCache(CacheNames.ASIDE);
		for (int i = 0; i < 20; i++) {
			cache.put(String.valueOf(i), String.valueOf(i));
		}
		AtomicInteger counter = new AtomicInteger();
		cache.iterator().forEachRemaining((e) -> counter.incrementAndGet());
		assertEquals(10, counter.get());
	}

	@Test
	void testSpringManager() throws Exception {
		String key = UUID.randomUUID().toString();
		NonSerializable value = springCacheManager.getCache(CacheNames.ASIDE).get(key, () -> new NonSerializable());
		assertNotNull(value);
	}

	public static class NonSerializable {
		String data = "";
	}

}
