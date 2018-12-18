# 5.1

## Implementation of `last`
> The list's last element, exception if `xs` is empty

- T(`head`) = constant

```scala
def last[T](xs: List[T]): T = xs match {
  case List() => throw new Error("last of empty list")
  case List(x) => x
  case y :: ys => last(ys)
}
```

## Implementation of `init`
> a list consisting of all elements of xs except the last one, exception if `xs` is empty

```scala
def init[T](xs: List[T]): List[T] = xs match {
  case List() => throw new Error("init of empty list")
  case List(x) => List()
  case y :: ys => y :: init(ys)
}
```

## Implementation of `concat`
> `xs ::: ys`

```scala
def concat[T](xs: List[T], ys: List[T]): List[T] = xs match {
  case List() => ys
  case z :: zs => z :: concat(zs, ys)
}
```

## Implementation of `reverse`

```scala
// T = (len^2)
def reverse[T](xs: List[T]): List[T] = xs match {
  case List() => xs
  case y :: ys => reverse(ys) ++ List(y)
}
```

## Implementation of `removeAt`

```scala
def removeAt(xs: List[T], n: Int) = (xs take(n)) ::: (xs.drop(n+1))
```

## Implementation of `flatten`

```scala
def flatten(xs: List[Any]): List[Any] = xs match {
  case List() => List()
  case (y :: ys) :: yss => flatten(y :: ys) ::: flatten(yss)
  case y :: ys => y :: flatten(ys)
}
```

---

# 5.2 Pair and Tuple

##　Merge Sort

1. separate list into 2 sub-list, each containing half of the elements of the original list
2. sort 2 sub-lists individually
3. merge 2 sorted sub-lists into a single sorted list

```scala
def mergeSort(xList: List[Int]): List[Int] = {
  val n = xList.length / 2
  if (n == 0) xList
  else {
    val (firstList, secondList) = xList.splitAt(n)
    merge(mergeSort(firstList), mergeSort(secondList))
  }
}
def merge(firstList: List[Int], secondList: List[Int]): List[Int] = firstList match {
    case Nil => secondList
    case firstHead :: firstListRemain =>
      secondList match {
        case Nil => firstList
        case secondHead :: secondListRemain =>
          if (firstHead < secondHead) firstHead :: merge(firstListRemain, secondList)
          else secondHead :: merge(firstList, secondListRemain)
      }
}
```

## Tuple class

```scala
case class Tuple2[T1, T2](_1: +T1, _2: +T2) {
  override def toString = "(" + _1 + ", " + _2 + ")"
}
```

## rewrite msort with pattern match

```scala
def mergePatternMatch(firstList: List[Int], secondList: List[Int]): List[Int] =
  (firstList, secondList) match {
    case (Nil, secondList) => secondList
    case (firstList, Nil) => firstList
    case (firstHead :: firstListRemain, secondHead :: secondListRemain) =>
      if (firstHead < secondHead) firstHead :: mergePatternMatch(firstListRemain, secondList)
      else secondHead :: mergePatternMatch(firstList, secondListRemain)
  }
```

---

# 5.3 Implicit Parameters

To make sort more general, first thought is to define arbitrary type `T`

**But `<` is not defined in `T`**

#### Sol 1: polymorphic `sort` func and comparison op as additional parameter

```scala
def mergeSortT[T](xList: List[T])(lessThan: (T, T) => Boolean): List[T] = {
  val n = xList.length / 2
  if (n == 0) xList
  else {
    def mergeT(firstList: List[T], secondList: List[T]): List[T] = (firstList, secondList) match {
      case (Nil, secondList) => secondList
      case (firstList, Nil) => firstList
      case (firstHead :: firstListRemain, secondHead :: secondListRemain) =>
        if (lessThan(firstHead, secondHead)) firstHead :: mergeT(firstListRemain, secondList)
        else secondHead :: mergeT(firstList, secondListRemain)
    }
    val (firstList, secondList) = xList.splitAt(n)
    mergeT(mergeSortT(firstList)(lessThan), mergeSortT(secondList)(lessThan))
  }
}
val nums = List(2, -4, 5 ,7, 1)
mergeSortT(nums)((x, y) => x < y)
val fruits: List[String] = List("apple", "pineapple", "orange", "banana")
mergeSortT(fruits)((x: String, y: String) => x.compareTo(y) < 0)
```

#### Sol 2: `scala.math.Ordering[T]`

```scala
import math.Ordering
def msort[T](xList: List[T])(ord: Ordering[T]): List[T] = ???
val nums: List[Int] = List(2, -4, 5 ,7, 1)
msort(nums)(Ordering.Int)
val fruits: List[String] = List("apple", "pineapple", "orange", "banana")
msort(fruits)(Ordering.String)
```

#### Sol 3: Implicit Parameter

Let compiler figure out the right implicit to pass based demand type

```scala
import math.Ordering
def msort[T](xList: List[T])(implicit ord: Ordering[T]): List[T] = ???
val nums: List[Int] = List(2, -4, 5 ,7, 1)
msort(nums)
val fruits: List[String] = List("apple", "pineapple", "orange", "banana")
msort(fruits)
```

---

# Higher-Order List Functions

## ScaleListByFactor
> multiply each element of a list by same factor

```scala
def scaleList(xs: List[Double], factor: Double): List[Double] = xs match {
  case Nil => xs
  case y :: ys => y * factor :: scaleList(ys, factor)
}
```

## Map

```scala
abstract class List[T] {
  def map[U](f: T => U): List[U] = this match {
    case Nil => this
    case x :: xs => f(x) :: xs.map(f)
  }
}
def scaleList(xs: List[Double], factor: Double) = xs.map(x => x * factor)
```

## SquareList

```scala
def squareList(xs: List[Int]): List[Int] = xs match {
  case Nil => Nil
  case y :: ys => y * y :: squareList(ys)
}
def squareList2(xs: List[Int]): List[Int] = xs.map(x => x * x)
```

## Filtering

```scala
def posElems(xs: List[Int]): List[Int] = xs match {
  case Nil => xs
  case y :: ys => 
    if (y > 0) 
      y :: posElems(ys) 
    else 
      posElems(ys)
}
abstract class List[T] {
  def filter(p: T => Boolean): List[T] = this match { 
    case Nil => this
    case x :: xs => if (p(x)) x :: xs.filter(p) else xs.filter(p)
  }
}

def posElems2(xs: List[Int]): List[Int] = xs filter (x => x > 0)
```

## Pack
> pack consecutive duplicates of list elements into sub-lists

```scala
def pack[T](xs: List[T]): List[List[T]] = xs match {
  case Nil => Nil
  case x :: xs1 => 
    val (first, rest) = xs.span(_ == x)
    first :: pack(rest)
}
```

## Encode
> encode n consecutive duplicates of an element x as a pair (x, n)

```scala
def encode[T](xs: List[T]): List[(T, Int)] = pack(xs).map(ys => (ys.head, ys.length))
```

---

