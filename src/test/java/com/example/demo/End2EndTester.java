package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class End2EndTester {
	AssetClient client1;
	AssetClient client2;

	@BeforeEach
	void setUp() {
		client1 = new AssetClient("http://localhost:8081");
		client2 = new AssetClient("http://localhost:8082");
	}

	@Test
	void updateAndGet() {
		//given
		client1.deleteAllAssets();

		//when
		String name = "test";
		client1.putAsset(1, name);
		//then
		assertEquals(name, client1.getAsset(1).getName());
		assertEquals(name, client2.getAsset(1).getName());
		assertEquals(name, client1.getAssets().get(0).getName());
		assertEquals(name, client2.getAssets().get(0).getName());

		//when
		name = "test2";
		client1.putAsset(1, name);
		//then
		assertEquals(name, client1.getAsset(1).getName());
		assertEquals(name, client2.getAsset(1).getName());
		assertEquals(name, client1.getAssets().get(0).getName());
		assertEquals(name, client2.getAssets().get(0).getName());
	}

	@Test
	void updateAndGetMany() {
		//given
		client1.deleteAllAssets();

		//when
		String prefix = "test";
		for (int i = 1; i <= 10; i++) {
			client1.putAsset(i, prefix + i);
		}
		//then
		for (int i = 1; i <= 10; i++) {
			String name = prefix + i;
			assertEquals(name, client1.getAsset(i).getName());
			assertEquals(name, client2.getAsset(i).getName());
		}
	}

	@Test
	void testQueryCache() {
		//given
		client1.deleteAllAssets();

		//when
		client1.putAsset(1, "aaa");
		client1.putAsset(2, "bbb");
		//then
		assertEquals(1, client1.findAssetByName("aaa").size());
		assertEquals(1, client2.findAssetByName("aaa").size());

		//when
		client1.putAsset(3, "aaa");
		//then
		assertEquals(2, client1.findAssetByName("aaa").size());
		assertEquals(2, client2.findAssetByName("aaa").size());
	}

	@Test
	@Disabled
	void printStats() {
		log.info("{}", client1.getStats());
	}

}
