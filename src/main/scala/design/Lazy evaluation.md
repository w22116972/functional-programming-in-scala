# 2.1 Structural Induction on Trees

## general induction principle

To prove property `P(t)` for all trees `t` of certain type

- show that `P(l)` holds for all leaves `l` of a tree
- for each type of internal node `t` with subtrees `s1`, ..., `sn`, show that `P(s1) ^ ... ^ P(sn)` implies `P(t)`

#### example: IntSet

```scala
abstract class IntSet {
    def incl(x: Int): IntSet
    def contains(x: Int): Boolean
}
object Empty extends IntSet {
    def contains(x: Int): Boolean = false
    def incl(x: Int): IntSet = NonEmpty(x, Empty, Empty)
}

case class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
def contains(x: Int): Boolean =
    if (x < elem) left contains x
    else if (x > elem) right contains x
    else true
def incl(x: Int): IntSet =
    if (x < elem) NonEmpty(elem, left incl x, right)
    else if (x > elem) NonEmpty(elem, left, right incl x)
    else this
}
```

#### 3 Laws of IntSet

- `Empty.contains(x)` = `false`
- `s.incl(x).contains(x)` = `true`
- `s.incl(x).contains(y)` = `s.contains(y) if x != y`


---

# 2.2 Stream

## Combinatorial Search

#### example: find second prime number between 1000 and 10000

#### approach 1: functional

- construct all prime number but only looks at first two elements

```scala
((1000 to 10000) filter isPrime)(1)
```

#### approach 2: recursive

```scala
def secondPrime(from: Int, to: Int) = nthPrime(from, to, 2)
def nthPrime(from: Int, to: Int, n: Int): Int = 
    if (from >= to) throw new Error("no prime")
    else if (isPrime(from))
        if (n == 1) from
        else nthPrime(from + 1, to, n - 1)
    else
        nthPrime(from + 1, to, n)
```

#### approach 3: Stream

- evaluate tail of sequence on demand 

```scala
(1000 to 10000).toStream.filter(isPrime)(1)
```

#### construct Stream

1. `List.toStream`
2. `x #:: xs`
3. `Stream.cons(x, xs)`

## implementation of Stream

```scala
trait Stream[+A] extends Seq[A] {
    def isEmpty: Boolean
    def head: A 
    def tail: Stream[A]
}

object Stream {
    // tail is a by-name parameter, while tail parameter in List is by-value
    def cons[T](head: T, tail: => Stream[T]) = new Stream[T] {
        def isEmpty = false
        def head = head
        def tail = tail
    }

    val empty = new Stream[Nothing] {
        def isEmpty = true
        def head = throw new NoSuchElementException("empty.head")
        def tail = throw new NoSuchElementException("empty.tail")
    }
} 

class Stream[+T] {
    def filter(predicate: T => Boolean): Stream[T] = 
        if (isEmpty) this
        else if (predicate(head)) cons(head, tail.filter(predicate))
        else tail.filter(predicate)
}
```

#### approach 4

use `streamRange(1000, 10000)` to replace `(1000, 10000)`

```scala
def streamRange(low: Int, high: Int): Stream[Int] = {
    if (low >= high) Stream.empty
    else Stream.cons(low, streamRange(low + 1, high))
}
```

---

# 2.3 Lazy Evaluation

cons of `Stream` is if `tail` is called several times, the corresponding stream will be recomputed each time.

store result of first evaluation of `tail` and reuse it on demand.

```scala
// evaluate once on first time, reuse it every time on demand
lazy val x = expression
```

#### improve `Stream.cons` by lazy

```scala
def cons[T](hd: T, tl: => Stream[T]) = new Stream[T] {
    def head = hd
    lazy val tail = tl
}
```

---

# 2.4 Infinite Stream 

#### stream of all integer starting from a given number 

```scala
def from(n: Int): Stream[Int] = n #:: from(n + 1)
// all natural numbers
val nats: Stream[Int] = from(0)
// all multiples of 4
val mult4: Stream[Int] = nats.map(_ * 4)
```

## Sieve of Eratosthenes

1. starting from 2
2. remove all multiples of current prime number

```scala
def sieve(s: Stream[Int]): Stream[Int] = 
    s.head #:: sieve(s.tail.filter(_ % s.head != 0))
val primes = sieve(from(2))
```
