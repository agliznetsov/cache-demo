package com.example.demo.hazelcast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

import com.hazelcast.core.ReplicatedMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HazelCache<K, V> implements Cache<K, V> {
	private final CacheManager cacheManager;
	private final ReplicatedMap<K, V> map;

	public HazelCache(CacheManager cacheManager, ReplicatedMap<K,V> map) {
		this.cacheManager = cacheManager;
		this.map = map;
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}

	@Override
	public Map<K,V> getAll(Set<? extends K> keys) {
		Map<K, V> res = new HashMap<>();
		keys.forEach(key -> res.put(key, map.get(key)));
		return res;
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void put(K key, V value) {
		map.put(key, value);
	}

	@Override
	public V getAndPut(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K,? extends V> aMap) {
		map.putAll(aMap);
	}

	@Override
	public boolean putIfAbsent(K key, V value) {
		return map.putIfAbsent(key, value) != null;
	}

	@Override
	public boolean remove(K key) {
		return map.remove(key) != null;
	}

	@Override
	public boolean remove(K key, V oldValue) {
		return map.remove(key, oldValue);
	}

	@Override
	public V getAndRemove(K key) {
		return map.remove(key);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return map.replace(key, oldValue, newValue);
	}

	@Override
	public boolean replace(K key, V value) {
		return map.replace(key, value) != null;
	}

	@Override
	public V getAndReplace(K key, V value) {
		return map.replace(key, value);
	}

	@Override
	public void removeAll(Set<? extends K> keys) {
		keys.forEach(key -> map.remove(key));
	}

	@Override
	public void removeAll() {
		map.clear();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public <C extends Configuration<K,V>> C getConfiguration(Class<C> clazz) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public <T> T invoke(K key, EntryProcessor<K,V,T> entryProcessor,
	                    Object... arguments) throws EntryProcessorException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public <T> Map<K,EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K,V,T> entryProcessor,
	                                                    Object... arguments) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String getName() {
		return map.getName();
	}

	@Override
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	@Override
	public void close() {
		map.destroy();
	}

	@Override
	public boolean isClosed() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public <T> T unwrap(Class<T> clazz) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void registerCacheEntryListener(CacheEntryListenerConfiguration<K,V> cacheEntryListenerConfiguration) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K,V> cacheEntryListenerConfiguration) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public Iterator<Entry<K,V>> iterator() {
		throw new RuntimeException("not implemented");
	}
}
