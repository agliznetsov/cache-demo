package com.example.demo.cache;

import java.net.URI;
import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;

public class DebugCacheManager implements CacheManager {
	Cache cache = new DebugCache();

	@Override
	public CachingProvider getCachingProvider() {
		return null;
	}

	@Override
	public URI getURI() {
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		return null;
	}

	@Override
	public Properties getProperties() {
		return null;
	}

	@Override
	public <K, V, C extends Configuration<K,V>> Cache<K,V> createCache(String cacheName,
	                                                                   C configuration) throws IllegalArgumentException {
		return null;
	}

	@Override
	public <K, V> Cache<K,V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
		return null;
	}

	@Override
	public <K, V> Cache<K,V> getCache(String cacheName) {
		return cache;
	}

	@Override
	public Iterable<String> getCacheNames() {
		return null;
	}

	@Override
	public void destroyCache(String cacheName) {

	}

	@Override
	public void enableManagement(String cacheName, boolean enabled) {

	}

	@Override
	public void enableStatistics(String cacheName, boolean enabled) {

	}

	@Override
	public void close() {

	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> clazz) {
		return null;
	}
}
