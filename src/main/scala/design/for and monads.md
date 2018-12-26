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

comopiler will convert `for` expression into `map`, `filter`, `flatMap`

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
// transalted to
(1 until n).flatMap(i =>
    (1 until i).withFilter(j => isPrime(i+j))
    .map(j => (i, j)))  
```

For example, books might not be a list, but a database stored on
some server.
As long as the client interface to the database defines the methods
map, flatMap and withFilter, we can use the for syntax for querying
the database.
This is the basis of the Scala data base connection frameworks
ScalaQuery and Slick.
Similar ideas underly Microsoft’s LINQ.

---