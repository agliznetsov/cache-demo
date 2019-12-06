package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Random;

import javax.cache.CacheManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.service.AssetCache;
import com.example.demo.service.AssetService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AssetServiceTester extends BaseTest {

	@Autowired
	AssetService assetService;
	@Autowired
	CacheManager cacheManager;
	@Autowired
	AssetCache assetCache;

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

		for (int i = 0; i < 100; i++) {
			assetService.findById(random.nextInt(10));
		}
	}

}
