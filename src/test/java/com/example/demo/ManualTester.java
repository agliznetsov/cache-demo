package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManualTester {
	AssetClient client1;
	AssetClient client2;

	@BeforeEach
	void setUp() {
		client1 = new AssetClient("http://localhost:8081");
		client2 = new AssetClient("http://localhost:8082");
	}
	@Test
	void resetName() {
		client1.putAsset(1, "name");
	}

	@Test
	void update1() {
		client1.putAsset(1, "name1");
	}

	@Test
	void update2() {
		client2.putAsset(1, "name22");
	}

	@Test
	void get1() {
		log.info("Client1 name: {}", client1.getAsset(1).getName());
	}

	@Test
	void get2() {
		log.info("Client2 name: {}", client2.getAsset(1).getName());
	}

	/*
		Stopped server:
		 - get:  slow, then OK, no cache
		 - update : slow, then OK, no cache
		 - cache is disabled. app need to restart!
	 */
}
