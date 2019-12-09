package com.example.demo.hazelcast;

import java.net.URI;
import java.util.Collections;
import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;

import com.hazelcast.core.HazelcastInstance;

public class HazelCacheManager implements CacheManager {
	private final HazelCachingProvider cachingProvider;
	private final HazelcastInstance hazel;

	public HazelCacheManager(HazelCachingProvider cachingProvider, HazelcastInstance hazel) {
		this.cachingProvider = cachingProvider;
		this.hazel = hazel;
	}

	@Override
	public CachingProvider getCachingProvider() {
		return cachingProvider;
	}

	@Override
	public URI getURI() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ClassLoader getClassLoader() {
		return cachingProvider.getDefaultClassLoader();
	}

	@Override
	public Properties getProperties() {
		return cachingProvider.getDefaultProperties();
	}

	@Override
	public <K, V, C extends Configuration<K,V>> Cache<K,V> createCache(String cacheName,
	                                                                   C configuration) throws IllegalArgumentException {
		return getCache(cacheName);
	}

	@Override
	public <K, V> Cache<K,V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
		return getCache(cacheName);
	}

	@Override
	public <K, V> Cache<K,V> getCache(String cacheName) {
		return new HazelCache(this, hazel.getReplicatedMap(cacheName));
	}

	@Override
	public Iterable<String> getCacheNames() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void destroyCache(String cacheName) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void enableManagement(String cacheName, boolean enabled) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void enableStatistics(String cacheName, boolean enabled) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void close() {
		hazel.shutdown();
	}

	@Override
	public boolean isClosed() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public <T> T unwrap(Class<T> clazz) {
		throw new RuntimeException("not implemented");
	}
}
