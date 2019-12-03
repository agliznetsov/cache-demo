package com.example.demo;

import static org.ehcache.config.builders.ResourcePoolsBuilder.newResourcePoolsBuilder;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;

import org.ehcache.clustered.client.config.ClusteringServiceConfiguration;
import org.ehcache.clustered.client.config.builders.ClusteredResourcePoolBuilder;
import org.ehcache.clustered.client.config.builders.ClusteredStoreConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.config.builders.ServerSideConfigurationBuilder;
import org.ehcache.clustered.common.Consistency;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.jsr107.Eh107Configuration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CacheBenchmark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(CacheBenchmark.class.getSimpleName())
				.build();
		new Runner(opt).run();
	}

	@State(Scope.Benchmark)
	public static class MyState {
		public Cache heap;
		public Cache heap2;
		public Cache offHeap;
		public Cache cluster;
		public CacheElement element = new CacheElement();

		@Setup(Level.Trial)
		public void setup() {
			System.out.println("Setup");
			//local();
			cluster();
		}

//		private void local() {
//			CacheManager cacheManager = getCachingProvider().getCacheManager();
//
//			heap = createCache(cacheManager, "heap", newResourcePoolsBuilder().heap(100).build());
//			offHeap = createCache(cacheManager, "off-heap", newResourcePoolsBuilder().offheap(1, MemoryUnit.MB).build());
//
//			heap.put(element.id, element);
//			offHeap.put(element.id, element);
//		}

		private void cluster() {
			URI clusterUri = URI.create("terracotta://localhost:9410/dgc");

			ClusteringServiceConfiguration clusterConfig = ClusteringServiceConfigurationBuilder
					.cluster(clusterUri)
					.autoCreate(b -> b
							.resourcePool("shared", 100, MemoryUnit.MB, "offheap-1")
					)
					.build();

			EhcacheCachingProvider provider = getCachingProvider();
			DefaultConfiguration configuration = new DefaultConfiguration(provider.getDefaultClassLoader(), clusterConfig);
			CacheManager cacheManager = provider.getCacheManager(provider.getDefaultURI(), configuration);

			heap = createCache(cacheManager, "heap", newResourcePoolsBuilder().heap(1, MemoryUnit.MB));
			heap2 = createCache(cacheManager, "heap2", newResourcePoolsBuilder().heap(1, MemoryUnit.MB).with(ClusteredResourcePoolBuilder.clusteredShared("shared")));
			offHeap = createCache(cacheManager, "off-heap", newResourcePoolsBuilder().offheap(1, MemoryUnit.MB));
			cluster = createCache(cacheManager, "cluster", newResourcePoolsBuilder().with(ClusteredResourcePoolBuilder.clusteredShared("shared")));

			heap.put(element.id, element);
			heap2.put(element.id, element);
			offHeap.put(element.id, element);
			cluster.put(element.id, element);
		}

		private Cache createCache(CacheManager cacheManager, String name, ResourcePoolsBuilder resourcePools) {
			CacheConfiguration cacheConfiguration = CacheConfigurationBuilder
					.newCacheConfigurationBuilder(Serializable.class, Object.class, resourcePools)
					.withService(ClusteredStoreConfigurationBuilder.withConsistency(Consistency.STRONG))
					.build();

			javax.cache.configuration.Configuration config = Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration);

			return cacheManager.createCache(name, config);
		}

		private EhcacheCachingProvider getCachingProvider() {
			return (EhcacheCachingProvider) Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
		}
	}


	@Benchmark @BenchmarkMode(Mode.AverageTime)
	public Object heapRead(MyState state) {
		return state.heap.get(state.element.id);
	}

	@Benchmark @BenchmarkMode(Mode.AverageTime)
	public void heapWrite(MyState state) {
		state.heap.put(state.element.id, state.element);
	}

	@Benchmark @BenchmarkMode(Mode.AverageTime)
	public Object heapClusterRead(MyState state) {
		return state.heap2.get(state.element.id);
	}

	@Benchmark @BenchmarkMode(Mode.AverageTime)
	public void heapClusterWrite(MyState state) {
		state.heap2.put(state.element.id, state.element);
	}

	@Benchmark @BenchmarkMode(Mode.AverageTime)
	public Object offHeapRead(MyState state) {
		return state.offHeap.get(state.element.id);
	}

	@Benchmark @BenchmarkMode(Mode.AverageTime)
	public void offHeapWrite(MyState state) {
		state.offHeap.put(state.element.id, state.element);
	}

	@Benchmark @BenchmarkMode(Mode.AverageTime)
	public Object clusterRead(MyState state) {
		return state.cluster.get(state.element.id);
	}

	@Benchmark @BenchmarkMode(Mode.AverageTime)
	public void clusterWrite(MyState state) {
		state.cluster.put(state.element.id, state.element);
	}

	public static class CacheElement implements Serializable {
		public String id = randomString();
		public String value1 = randomString();
		public String value2 = randomString();
		public String value3 = randomString();
		public String value4 = randomString();
		public String value5 = randomString();

		@Override
		public String toString() {
			return "CacheElement{" +
					"id='" + id + '\'' +
					", value1='" + value1 + '\'' +
					", value2='" + value2 + '\'' +
					", value3='" + value3 + '\'' +
					", value4='" + value4 + '\'' +
					", value5='" + value5 + '\'' +
					'}';
		}
	}

	private static String randomString() {
		return UUID.randomUUID().toString();
	}

}
