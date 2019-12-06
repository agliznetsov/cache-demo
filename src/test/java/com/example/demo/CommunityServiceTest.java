package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import javax.cache.CacheManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.service.AssetService;
import com.example.demo.service.CommunityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class CommunityServiceTest extends BaseTest {

	@Autowired
	CommunityService communityService;

	@BeforeEach
	void setUp() {
		communityService.deleteAll();
	}

	@Test
	void testCreate() {
		communityService.createCommunity(0, null, "parent");
		communityService.createCommunity(1, 0, "child");

		assertNotNull(communityService.getCommunity(1).getParentCommunity());
		assertEquals(1, communityService.getCommunity(0).getSubCommunities().size());
	}

	@Test
	void testMove() {
		communityService.createCommunity(1, null, "root1");
		communityService.createCommunity(2, null, "root2");
		communityService.createCommunity(3, 1, "child");
		communityService.moveCommunity(3, 2);

		assertEquals(2, communityService.getCommunity(3).getParentCommunity().getId());
		assertEquals(0, communityService.getCommunity(1).getSubCommunities().size());
		assertEquals(1, communityService.getCommunity(2).getSubCommunities().size());
	}

	@Test
	void testMoveRollback() {
		communityService.createCommunity(1, null, "root1");
		communityService.createCommunity(2, null, "root2");
		communityService.createCommunity(3, 1, "child");
		tryCatch(() -> communityService.moveCommunityAndFail(3, 2));

		assertEquals(1, communityService.getCommunity(3).getParentCommunity().getId());
		assertEquals(1, communityService.getCommunity(1).getSubCommunities().size());
		assertEquals(0, communityService.getCommunity(2).getSubCommunities().size());
	}

}
