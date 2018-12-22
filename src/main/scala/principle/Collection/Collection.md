# Collection

#### Range

- as single object with 
    1. lower bound
    2. uppder bound
    3. step value

```scala
var r: Range = 1 until 5 // 1~4
var s: Range = 1 to 5 // 1~5  ->
```

#### Combination

list all combination of x, y where x is drawn from 1 ~ M and y is drawn from 1 ~ N

```scala
(1 to M).flatMap{
    x => (1 to N).map(y => (x, y))
}
```

#### Scala product of two vectors

```scala
def scalaProduct(xs: Vector[Double], ys: Vector[Double]): Double = (xs zip ys).map(xy => xy._1 * xy._2).sum
def scalaProduct(xs: Vector[Double], ys: Vector[Double]): Double = (xs zip ys).map{case (x, y) => x * y}.sum
```

#### Is prime

```scala
// my solution
def isPrime(n: Int): Boolean = (2 to n/2).filter(n % x == 0).length == 0
// Instructor's solution
def isPrime(n: Int): Boolean = (2 until n).forall(d => n % d != 0)
```

---

