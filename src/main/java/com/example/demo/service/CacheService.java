package com.example.demo.service;

import javax.cache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CacheService {

	@Autowired(required = false)
	CacheManager cacheManager;


	public void clearStats() {
	}

	public void clearStats(String name) {
	}

	public Object getStats(String name) {
		return null;
	}

	public void printStats() {
	}

	public void printStats(String name) {
	}

}
