# 1.1 Paradigm

## Imperative Programming

- mutable variable -> **memory call**
- variable dereference -> **load** instruction
- variable assignment -> **store** instruction
- control structure -> **jumps**

pure imperative programming is limited by the **Von Neumann** bottleneck
> One tends to conceptualize data structures word-by-word



## Functional Programming

#### goal

- concentrate on defining theories for operators
- minimize state changes
- treat operators as functions, often composed of simpler functions

#### function is first-class

- defined anywhere, including inside other functions
- be passed as parameters to functions and returned as results
- there exists a set operators to compose functions

---

# 1.2

---

# 1.3 Evaluation Strategies and Termination

## how non-primitive expression is evaluated

1. Take the leftmost operator
2. Evaluate its operands (left before right)
3. Apply the operator to the operands

## how parameterized function is evaluated

1. Evaluate all function arguments, from left to right
2. Replace the function application by the function’s right-hand side, and, at the same time
3. Replace the formal parameters of the function by the actual arguments.

## First evaluation strategy

reduce to the same final values if 
- reduced expression consists of pure functions
- both evaluations terminate

## Call-by-Value

evaluates every function argument only once

## Call-by-Name

function argument is not evaluated if the corresponding parameter is unused in the evaluation of the function body

#### example: terminates under CBN but not under CBV

Scala use Call-by-Value by default

```scala
def first(x: Int, y: Int) = x 
first(1, loop)
```

use `=> type of parameter` to become Call-by-Name

```scala
def constOne(x: Int, y: => Int) = 1
```
