# Cache Demo

## Benchmarks
 
### Read/Write single cache element by ID

```
Benchmark                        Mode  Cnt   Score    Error  Units
CacheBenchmark.clusterRead       avgt    5   1.216 ±  0.621  ms/op
CacheBenchmark.clusterWrite      avgt    5   1.590 ±  0.580  ms/op
CacheBenchmark.heapClusterRead   avgt    5  ≈ 10⁻⁴           ms/op
CacheBenchmark.heapClusterWrite  avgt    5   1.462 ±  0.334  ms/op
CacheBenchmark.heapRead          avgt    5  ≈ 10⁻⁴           ms/op
CacheBenchmark.heapWrite         avgt    5   0.002 ±  0.001  ms/op
CacheBenchmark.offHeapRead       avgt    5   0.006 ±  0.001  ms/op
CacheBenchmark.offHeapWrite      avgt    5   0.004 ±  0.001  ms/op
```

### Hazelcast benchmarks
```
remote:
write: 3.0 read ~1.5

local:
write: 0.1 read 0.05

alone:
write: 0.01 read: 0.01
```

### Infinispan:
read: e-4
write:
replicate_sync: 3 - 6 ms
replicate_async: 3 - 6 ms
invalidate_sync: 3 - 6 ms
invalidate_async: 0.04, 0.08, 0.15, 0.3
