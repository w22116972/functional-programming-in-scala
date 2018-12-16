#### Shared memory

- data partitioned in *memory*

#### Distributed nodes

- data partitioned *between machines*, *network* in between

---

# Cache, Persistence


|Level | Space | CPU time | In memory | On disk|
|---|---|---|---|---|
|`MEMORY_ONLY`|High|Low|Y|N|
|`MEMORY_ONLY_SER`|Low|High|Y|N|
|`MEMORY_AND_DISK`|High|Mid|Some|Some|
|`MEMORY_AND_DISK_SER`|Low|High|Some|Some|
|`DISK_ONLY`|Low|High|N|Y|

---

# Procedure of Spark program

1. driver create `SparkContext` to run Spark app
2. `SparkContext` connect cluster manager(e.g. YARN) to allocate resources
3. cluster manager requires executors on nodes
4. driver sends code to executors
5. `SparkContext` sends tasks for executors for run

#### example 1:

```scala
rdd.foreach(println)
```

- `foreach` is an action returns type `Unit` executing on worker nodes
- any calls `println` on stdout of worker nodes
- not visible in the stdout of the driver node

---

# Measure performance time

```scala
val timing = new StringBuffer
def timed[T](label: String, code: => T): T = {
  val start = System.currentTimeMillis()
  val result = code
  val stop = System.currentTimeMillis()
  timing.append(s"Processing $label took ${stop - start} ms.\n")
  result
}
val perm_time = timed(label="label", code=func) 
```

---

# Partition

- Same partition are guaranteed to be on the same machine
- default number of partitions is the total number of cores on all executors nodes

1. round-robin partitioning
2. hash partitioning
3. range partitioning

#### Hash Partitioning

- spread data evenly across nodes
- all tuples in the same `partition_number` are sent to the same machine.

e.g. `groupByKey`: (k, v)
```scala
partition_number = k.hashCode() % total_num_partition
```



#### Range Partitioning

- keys may contain **ordering** defined

```scala
// Provide desired number of partitions and pair RDD as ordered keys
val tunePartitioner = new RangePartitioner(NUM_OF_PART, pairRDD)
val partitioned = pairRDD.partitionBy(tunePartitioner).persist()
```
note: why `persist()`?

1. Spark reevalute chains of transformation again and again
2. this RDD would be shuffled over the network and partitioned again and again

#### transformation on partitioned RDD

![](2018-04-02-10-53-16.png)

`flatMap`, `map` will lose partitioner
- keys may be changed by using `map`; however partitioner needs keys for partition
- so `mapValues` provide `map` function without changing the keys

#### Q: why `join` is inefficient?

1. hash all keys of both df
2. send pairs with same **key hash** to same machine across network
3. join paris with same key on that machine

每次呼叫`join`就會不斷執行上面三個步驟，即使原本的RDD並沒有改變任何位置(還在原本的機器上)

#### A: `partitionBy`

```scala
val userData = sc.("")
    .partitionBy(new HashPartitioner(100))
    .persist()
```

1. spark knows that it is hash-paritioned
2. when we call `join(otherRDD)`
3. spark only shuffle `otherRDD`
4. send `otherRDD` to machine that contain corresponding hash partition of `userData`

- 只有`otherRDD`shuffle across network

ps. `RDD.toDebugString` shows execution plan

#### `join` 2 pre-partitioned RDD

1. with same paritioner, cached on the same machine
2. cause `join` **computed locally** with no shuffle across network

#### `reducedByKey` on pre-partitioned RDD

1. compute value locally
2. require only final reduced value sent from worker to driver



