package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import javax.cache.CacheManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.AssetType;
import com.example.demo.service.AssetService;
import com.example.demo.service.AssetTypeService;
import com.example.demo.service.CacheService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AssetTypeServiceTester extends BaseTest {

	@Autowired
	AssetTypeService assetTypeService;
	@Autowired
	CacheService cacheService;

	@BeforeEach
	void setUp() {
		assetTypeService.deleteAll();
	}

	@Test
	void testCache() {
		AssetType assetType = null;
		for (int i = 0; i < 5; i++) {
			assetType = assetTypeService.createAssetType(assetType, "name" + i);
		}
		assertEquals(5, assetTypeService.getParentTypeIds(assetType.getId()).size());

		cacheService.clearStats();
		String typeId = assetType.getId();
		log.info("------------------------------------");
		measureTime("GetParent", 1000, (i) -> assetTypeService.getParentTypeIds(typeId));
		log.info("------------------------------------");
		cacheService.printStats();
	}

}
