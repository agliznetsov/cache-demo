package com.example.demo.web;

import java.util.Map;

import javax.cache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.CacheService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cache")
public class CacheController {

	@Autowired
	CacheService cacheService;
	@Autowired
	CacheManager cacheManager;

	@GetMapping("/stats")
	public Map getStats() {
		log.info("get cache stats");
		return cacheService.getStats();
	}

}
