package com.example.demo.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DebugCache implements Cache {
	ConcurrentHashMap map = new ConcurrentHashMap();

	@Override
	public Object get(Object key) {
		log.info("get {}", key);
		return map.get(key);
	}

	@Override
	public Map getAll(Set keys) {
		log.info("getAll");
		return null;
	}

	@Override
	public boolean containsKey(Object key) {
		log.info("containsKey");
		return false;
	}

	@Override
	public void loadAll(Set keys, boolean replaceExistingValues, CompletionListener completionListener) {
		log.info("loadAll");

	}

	@Override
	public void put(Object key, Object value) {
		log.info("put {} {}", key, value);
		map.put(key, value);
	}

	@Override
	public Object getAndPut(Object key, Object value) {
		log.info("getAndPut");
		return null;
	}

	@Override
	public void putAll(Map map) {
		log.info("putAll");

	}

	@Override
	public boolean putIfAbsent(Object key, Object value) {
		log.info("putIfAbsent");
		return false;
	}

	@Override
	public boolean remove(Object key) {
		log.info("remove");
		return false;
	}

	@Override
	public boolean remove(Object key, Object oldValue) {
		log.info("remove");
		return false;
	}

	@Override
	public Object getAndRemove(Object key) {
		log.info("getAndRemove");
		return null;
	}

	@Override
	public boolean replace(Object key, Object oldValue, Object newValue) {
		log.info("replace");
		return false;
	}

	@Override
	public boolean replace(Object key, Object value) {
		log.info("replace");
		return false;
	}

	@Override
	public Object getAndReplace(Object key, Object value) {
		log.info("getAndReplace");
		return null;
	}

	@Override
	public void removeAll(Set keys) {
		log.info("removeAll");

	}

	@Override
	public void removeAll() {
		log.info("removeAll");

	}

	@Override
	public void clear() {
		log.info("clear");

	}

	@Override
	public String getName() {
		log.info("getName");
		return null;
	}

	@Override
	public CacheManager getCacheManager() {
		log.info("getCacheManager");
		return null;
	}

	@Override
	public void close() {
		log.info("close");

	}

	@Override
	public boolean isClosed() {
		log.info("isClosed");
		return false;
	}

	@Override
	public void registerCacheEntryListener(CacheEntryListenerConfiguration cacheEntryListenerConfiguration) {
		log.info("registerCacheEntryListener");

	}

	@Override
	public void deregisterCacheEntryListener(CacheEntryListenerConfiguration cacheEntryListenerConfiguration) {
		log.info("deregisterCacheEntryListener");

	}

	@Override
	public Iterator<Entry> iterator() {
		log.info("iterator");
		return null;
	}

	@Override
	public Object unwrap(Class clazz) {
		log.info("unwrap");
		return null;
	}

	@Override
	public Map invokeAll(Set keys, EntryProcessor entryProcessor, Object... arguments) {
		log.info("invokeAll");
		return null;
	}

	@Override
	public Object invoke(Object key, EntryProcessor entryProcessor,
	                     Object... arguments) throws EntryProcessorException {
		log.info("invoke");
		return null;
	}

	@Override
	public Configuration getConfiguration(Class clazz) {
		log.info("getConfiguration");
		return null;
	}
}
