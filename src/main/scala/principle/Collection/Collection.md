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

# 6.2 Combinatorial Search

Given positive integer `n`, find all pairs of positive integers `(i, j)` s.t. `1 <= j < i < n` and `i + j` is prime

## Nested Sequences

1. generate `i` in 1 ~ n-1
2. for each `i`, generate `(i, 1), ... (i, i-1)`

```scala
def generate(n: Int): List(Int, Int) = {
    (1 until n).map{i =>
        (1 until i).map(j => (i, j))
    }.foldRight(Seq[Int]())(_ ++ _)  // flatten
}
```

#### flatMap vs map + flatten

```scala
xs.flatMap(f) = (xs.map(f)).flatten
```

```scala
(1 until n).flatMap{i => 
    (1 until i).map(j => (i, j))
}
```

#### check prime

check `i + j` is prime or not

```scala
object Pairs{
    def isPrime(n: Int): Boolean = (2 until n).forall(d => n % d != 0)
    val n = 7
    (1 until n).flatMap{i => 
        (1 until i).map(j => (i, j))}.filter(pair => isPrime(pair._1 + pair._2))
}
```

## `for`, `yield`

```scala
case class Person(name: String, age: Int)

// imperative lang
for (p <- persons if p.age > 20) yield p.name
// functional lang
persons.filter(p => p.age > 20).map(p => p.name)
```

#### readable checking prime

```scala
for {
    i <- 1 until n
    j <- 1 until i
    if isPrime(i + j)
} yield (i, j)
```


#### exercise: `scalarProduct` with `for`

```scala
// my solution
def scalarProduct(xs: List[Double], ys: List[Double]): Double = {
  (for {
    x <- xs
    y <- ys
  } yield x * y).sum
}
// instructor's solution
def scalarProduct2(xs: List[Double], ys: List[Double]): Double = {
  (for ((x, y) <- xs.zip(ys)) yield  x * y).sum
}
```

---

# 6.3 Set

N-queen: there can't be two queens in same row, column, or diagonal

在n-1個皇后都安全的情況下，在沒人放過的row上放上第n個皇后

```scala
object NQueens {
    def queens(n: Int): Set[List[Int]] = {
        def placeQueens(k: Int): Set[List[Int]] = 
            if (k == 0) Set(List())
            else
                for {
                    queens <- placeQueens(k - 1)
                    col <- 0 until n  // now we have to put queens on col
                    if isQueenSafe(col, queens)
                } yield col :: queens
        
        placeQueens(n)
    }

    def isQueenSafe(col: Int, queens: List[Int]): Boolean = {
        val row = queens.length
        val queensWithRow = (row - 1 to 0 by -1) zip queens
        queensWithRow.forall{
            // check new col not equal to previous columns and
            // check diagonal
            case (r, c) => col != c && math.abs(col - c) != row - r  
        }
    }

    def show(queens: List[Int]) = {
        val lines = 
            for (col <- queens.reverse)
                yield Vector.fill(queens.length)("* ").updated(col, "X ").mkString("\n") + (lines.mkString("\n"))
    }
    queens(4).map(show)
}
```

---

# 6.4 Maps

## Options

#### 3 Option Types

```scala
trait Option[+A]
case class Some[+A](value: A) extends Option[A]
object None extends Option[Nothing]
```

#### usage

```scala
def findValue(x: String): String = y.get(x) match {
    case Some(x) => x
    case None => "missing data"
}
```

## Polynomial

```scala
object Polynomial {
    class Poly(val terms0: Map[Int, Double]) {
        // constructor
        def this(bindings: (Int, Double)*) = this(bindings.toMap)
        val terms = terms0.withDefaultValue(0.0)
        def +(other: Poly): Poly = new Poly(terms ++ other.terms)
        // def adjust(term: (Int, Double)): (Int, Double) = {
        //     val (exp, coeff): (Int, Doble) = term
        //     term.get(exp).match {
        //         case Some(coeff1) => exp -> (coeff + coeff1)
        //         case None => exp -> coeff
        //     }
        // }
        def adjust(term: (Int, Double)): (Int, Double) = {
            val (exp, coeff): (Int, Double) = term
            exp -> (coeff + terms(exp))
        }
        override def toString: String = for ((exp, coeff) <- terms.toList.sorted.reverse) yield coeff + "x^" + exp).mkString(" + ")
    }
    val p1 = new Poly(1 -> 2.0, 3 -> 4.0, 5 -> 6.2)
    val p2 = new Poly(0 -> 3.0, 3 -> 7.0)
    p1 + p2
    p1.terms(7)
}
```

```scala
// improved
object Polynomial {
    class Poly(val terms0: Map[Int, Double]) {
        def this(bindings: (Int, Double)*) = this(bindings.toMap)
        val terms = terms0.withDefaultValue(0.0)
        // Using foldLeft instaed of `++` avoids creating of entire list
        def +(other: Poly): Poly = new Poly((other.terms.foldLeft(terms)(addTerm))
        def addTerm(terms: Map[Int, Double], term: (Int, Double)): Map[Int, Double] = {
            val (exp, coeff): (Int, Double) = term
            exp -> (coeff + terms(exp))
        }
    }
}
```

---

# 6.5

Produce all phrases of words that can serve as mnemonics for phone number

```scala

import scala.io.Source

object x {
    val in = Source.fromURL("https://lamp.epfl.ch/files/content/sites/lamp/files/teaching/progfun/linuxwords.txt")
    val words = in.getLines.toList.filter(_.forall(_.isLetter))
    val mnem = Map('2' -> "ABC", '3' -> "DEF", '4' -> "GHI", '5' -> "JKL",
'6' -> "MNO", '7' -> "PQRS", '8' -> "TUV", '9' -> "WXYZ"))
    
    // convert mnem map to give a map from chars 'A'~'Z' to '2'~'9'
    val charCode: Map[Char, Char] = 
        for {
            (digit, str) <- mnem 
            letter <- str
        } yield letter -> digit

    // maps a word to digit string it can represent
    // "Java" -> "5282"
    def wordCode(word: String): String = word.toUpperCase.map(charCode)

    // map from digit strings to the words that represent them
    // "5282" -> List("Java", "Kata", "Lava", ...)
    val wordsForNum: Map[String, Seq[String]] = words.groupBy(wordCode).withDefaultValue(Seq())

    // return all ways to encode number as list of words
    def encode(number: String): Set[List[String]] = 
        if (number.isEmpty) Set(List())
        else {
            for {
                split <- 1 to number.length
                word <- wordsForNum(number.take(split))
                rest <- encode(number.drop(split))
            } yield word :: rest
        }.toSet

    def translate(number: String): Set[String] = 
        encode(number).map(_.mkString(" "))
}


```