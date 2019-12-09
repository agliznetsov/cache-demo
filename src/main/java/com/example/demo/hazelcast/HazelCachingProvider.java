package com.example.demo.hazelcast;

import java.net.URI;
import java.util.Properties;

import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;

import com.hazelcast.core.HazelcastInstance;

public class HazelCachingProvider implements CachingProvider {
	private HazelCacheManager cacheManager;

	public HazelCachingProvider(HazelcastInstance hazel) {
		cacheManager = new HazelCacheManager(this, hazel);
	}

	@Override
	public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
		return cacheManager;
	}

	@Override
	public ClassLoader getDefaultClassLoader() {
		return getClass().getClassLoader();
	}

	@Override
	public URI getDefaultURI() {
		return null;
	}

	@Override
	public Properties getDefaultProperties() {
		return new Properties();
	}

	@Override
	public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
		return cacheManager;
	}

	@Override
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	@Override
	public void close() {

	}

	@Override
	public void close(ClassLoader classLoader) {

	}

	@Override
	public void close(URI uri, ClassLoader classLoader) {

	}

	@Override
	public boolean isSupported(OptionalFeature optionalFeature) {
		return false;
	}
}
