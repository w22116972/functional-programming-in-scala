# 1.1 Query with `For`

To find the titles of books whose author’s name is "Bird":

```scala
for (b <- books; a <- b.authors if a startsWith "Bird,")
    yield b.title
```

To find all the books which have the word "Program" in the title:
```scala
for (b <- books if b.title indexOf "Program" >= 0)
    yield b.title
```

To find the names of all authors who have written at least two books present in the database:

```scala
for {
    book1 <- books
    book2 <- books
    if book1.title < book2.title
    author1 <- book1.authors
    author2 <- book2.authors
    if author1 == author2
} yield author1
```

remove duplicate

```scala
{ for {
    b1 <- books
    b2 <- books
    if b1.title < b2.title
    a1 <- b1.authors
    a2 <- b2.authors
    if a1 == a2
    } yield a1
}.distinct

val bookSet = books.toSet
for {
    b1 <- bookSet
    b2 <- bookSet
    if b1 != b2
    a1 <- b1.authors
    a2 <- b2.authors
    if a1 == a2
} yield a1
```

---

# 1.2 

#### implementation of `map`

```scala
def map[T, U](xs: List[T], f: T => U): List[U] = 
    for (x <- xs) yield f(x)
```

#### implementation of `flatMap`

```scala
def flatMap[T, U](xs: List[T], f: T => Iterable[U]): List[U] = 
    for (x <- xs; y <- f(x)) yield y
```

#### implementation of `filter`

```scala
def filter[T](xs: List[T], p: T => Boolean): List[T] = 
    for (x <- xs if p(x)) yield x
```

## Translation

compiler will convert `for` expression into `map`, `filter`, `flatMap`

#### `for` to `map`

```scala
for (x <- e1) yield e2
// translated to
e1.map(x => e2)
```

#### `for` to `withFilter`

```scala
for (x <- e1 if isSomething; seq) yield e2
// translated to
for (x <- e1.withFilter(x => isSomething); seq) yield e2
```

#### `for` to `flatMap`

```scala
for (x <- e1; y <- e2; s) yield e3
// translated to
e1.flatMap(x => for (y <- e2; s) yield e3)
```

#### exercise

```scala
for {
    i <- 1 until n
    j <- 1 until i
    if isPrime(i + j)
} yield (i, j)
// translated to
(1 until n).flatMap(i =>
    (1 until i).withFilter(j => isPrime(i+j))
    .map(j => (i, j)))  
```

For example, books might not be a list, but a database stored on some server.
As long as the client interface to the database defines the methods map, flatMap and withFilter, we can use the for syntax for querying the database.
This is the basis of the Scala data base connection frameworks ScalaQuery and Slick.
Similar ideas underly Microsoft’s LINQ.

---

# 1.3 functional random generator

## generator

使隨機產生器可以用在各種資料型態
```scala
trait Generator[+T] {
    def generate: T
}

val integers = new Generator[Int] {
    val rand = new java.util.Random
    def generate = rand.nextInt()
}

// sol 1
val booleans = new Generator[Boolean] {
    def generate = integers.generate > 0
}
// sol 2
val booleans = for (x <- integers) yield x > 0
// compiler's view of sol 2
val booleans = integer.map(x => x > 0)

// sol 1
val pairs = new Generator[(Int, Int)] {
    def generate = (integers.generate, integers.generate)
}
// sol 2
def pairs[T, U](t: Generator[T], u: Generator[U]) = for {
    x <- t 
    y <- u 
} yield (x, y)
// compiler's view of sol 2
def pairs[T, U](t: Generator[T], u: Generator[U]) = 
    t.flatMap(x => u.map(y => (x, y)))
```

既然在編譯器眼中都轉成map, flatmap

```scala
trait Generator[+T] {
    self => // this 
    
    def generate: T 

    def map[S](f: T => S): Generator[S] = new Generator[S] {
        // 之所以不能用this是因為此處是anonymous class, 
        // 所以會refer到current(下面的)generate, 最後會變成無窮遞迴.
        // 所以為了refer到上面的generate, 要在外面define self(指向trait Generator)
        // note: f(generate) == f(this.generate) == f(Generator.this.generate)
        def generate = f(self.generate)
    }
    def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
        def generate = f(self.generate).generate
    }
}

// compiler's view
val booleans = new Generator[Boolean] {
    def generate = (x: Int => x > 0)(integers.generate)
}
// sol 3
val booleans = new Generator[Boolean] {
    def generate = integers.generate > 0
}

// compiler's view
def pairs[T, U](t: Generator[T], u: Generator[U]) = 
    t.flatMap {
        x => new Generator[(T, U)] { def generate = (x, u.generate) } 
    }
def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)] {
    def generate = (new Generator[(T, U)] {
        def generate = (t.generate, u.generate)
    }).generate 
}

def pairs[T, U](t: Generator[T], u: Generator[U]) = new Generator[(T, U)] {
    def generate = (t.generate, u.generate)
}
```

## Other Generator example

```scala
def single[T](x: T): Generator[T] = new Generator[T] {
    def generate = x
}
def choose(lo: Int, hi: Int): Generator[Int] =
    for (x <- integers) yield lo + x % (hi - lo)
def oneOf[T](xs: T*): Generator[T] =
    for (idx <- choose(0, xs.length)) yield xs(idx)
```

## List Generator

```scala
def lists: Generator[List[Int]] = for {
    isEmpty <- booleans
    list <- if (isEmpty) emptyLists else nonEmptyLists
} yield list

def emptyLists = single(Nil)

def nonEmptyLists = for {
    head <- integers
    tail <- lists
} yield head :: tail
```

## Tree Generator

```scala  
trait Tree
case class Inner(left: Tree, right: Tree) extends Tree
case class Leaf(x: Int) extends Tree
```

```scala
def leafs: Generator[Leaf] = for {
    x <- integers
} yield Leaf(x)

def inners: Generator[Inner] = for {
    left <- trees 
    right <- trees
} yield Inner(left, right)

def trees: Generator[Tree] = for {
    isLeaf <- booleans
    tree <- if (isLeaf) leafs else inners
} yield tree
```

## Random Testing

```scala
def test[T](g: Generator[T], numTimes: Int = 100)(test: T => Boolean): Unit = {
    for (i <- 0 until numTimes) {
        val value = g.generate
        assert(test(value), "test failed for " + value)
    }
    println("passed " + numTimes + " tests")
}
```

---

# 1.4 Monads

monad `M` is a parametric type `M[T]` with `flatMap` and `unit`, that have to satisfy some laws.

```scala
trait M[T] {
    def flatMap[U](f: T => M[U]): M[U]
}
def unit[T](x: T): M[T]
```

#### example

- `List` is monad with `unit(x) = List(x)`
- `Set` is monad with `unit(x) = Set(x)`
- `Option` is monad with `unit(x) = Some(x)`
- `Generator` is monad with `unit(x) = single(x)`

#### use `flatMap` and `unit` to define `map`

```scala
m.map(f) 
m.flatMap(x => unit(f(x)))
m.flatMap(f andThen unit)
```

## Monad Laws 

To qualify as a monad, a type has to satisfy three laws:

#### 1. Associativity

`m flatMap f flatMap g == m flatMap (x => f(x) flatMap g)`

#### 2. Left unit

`unit(x).flatMap(f) == f(x)`

#### 3. Right unit

`m.flatMap(unit) == m`