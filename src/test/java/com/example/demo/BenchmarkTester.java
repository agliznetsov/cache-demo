package com.example.demo;

import static com.example.demo.config.CacheNames.ASSET;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import javax.cache.CacheManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.service.AssetService;
import com.example.demo.service.CacheService;
import com.example.demo.service.CommunityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class BenchmarkTester extends BaseTest {
	final Random random = new Random();

	@Autowired
	AssetService assetService;
	@Autowired
	CacheService cacheService;
	@Autowired
	CommunityService communityService;

	@Test
	void benchmark() {
		int iterations = 1_000;
		int assetCount = 1000;

		//warm-up
		measureTime("Write", assetCount, (id) -> assetService.createAsset(id));
		measureTime("Read", assetCount, (id) -> assetService.findById(id));
		cacheService.clearStats(ASSET);

		for (int n = 0; n < 3; n++) {
			//test
			measureTime("Read", iterations, (id) -> assetService.findById(random.nextInt(assetCount)));
			cacheService.printStats(ASSET);
		}
	}

	@Test
	void testQueryCache() {
		assetService.createAsset(1, "aaa", null);
		assetService.createAsset(2, "bbb", null);
		assetService.createResource(1, "aaa", null);
		assetService.createResource(2, "bbb", null);
		assertEquals(1, assetService.findAssetByName("aaa").size());
		assertEquals(1, assetService.findResourceByName("aaa").size());

		cacheService.clearStats();
		cacheService.printStats();
		measureTime("findAssetByName", 10, (id) -> assetService.findAssetByName("aaa"));
		measureTime("findResourceByName", 10, (id) -> assetService.findResourceByName("aaa"));
		cacheService.printStats();

		assetService.createAsset(3, "aaa", null);
		assertEquals(2, assetService.findAssetByName("aaa").size());
		assertEquals(1, assetService.findResourceByName("aaa").size());
		measureTime("findAssetByName", 10, (id) -> assetService.findAssetByName("aaa"));
		measureTime("findResourceByName", 10, (id) -> assetService.findResourceByName("aaa"));
		cacheService.printStats();
	}

	@Test
	void testCollectionCache() {
		Random random = new Random();
		communityService.deleteAll();
		communityService.createTree();

		cacheService.clearStats();
		measureTime("warm-up", 10, (i) -> communityService.getSubCommunities(i));
		log.info("--------------------------------------------------------------------------");
		measureTime("GetSubCommunities", 100, (i) -> communityService.getSubCommunities(random.nextInt(10)));
		log.info("--------------------------------------------------------------------------");
		cacheService.printStats();
	}

}
