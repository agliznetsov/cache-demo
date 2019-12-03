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

Infinispan:
replicated_sync:
write: e-4
write: 0.1 read: e-4
write: 0.2 read: e-4, e-4
write: 0.3 read: e-4, e-4, e-4

replicated_async:
write: e-4
write: 0.07 read: e-4
write: 0.14 read: e-4, e-4
write: 0.18 read: e-4, e-4, e-4

invalidated sync:
write e-4
write 0.06
write 0.1
write 0.13
