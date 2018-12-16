/**
  * # Higher-Order Function
  * def: functions take other function as param or as result
  *
  *

 */

/* Take sum of the integers between a and b */
def sumInt(a: Int, b: Int): Int =
  if (a > b) 0 else a + sumInt(a+1, b)

/**
  * sumInt(1, 3)
  * = 1 + sumInt(2, 3)
  * = 1 + 2 + sumInt(3, 3)
  * = 1 + 2 + 3 + sumInt(4, 3)
  * = 1 + 2 + 3 + 0
  *
  * sumF(f, a, b): Int =
  *   if (a > b) 0 else f(a) + sumF(f, a+1, b)
  */

def cube(x: Int): Int = x * x * x

def sumCubes(a: Int, b: Int): Int =
  if (a > b) 0 else cube(a) + sumCubes(a+1, b)


def sum(f: Int => Int, a: Int, b: Int): Int = {
  def loop(a: Int, acc: Int): Int =
    if (a > b) acc
    else loop(a+1, f(a)+ acc)
  loop(a, 0)
}
/* Anonymous Function */
def sumCubes_2(a: Int, b: Int) = sum(x => x * x * x, a, b)

/* Curring */
def sum_2(f: Int => Int): (Int, Int) => Int = {
  def sumF(a: Int, b: Int): Int =
    if (a > b) 0
    else f(a) + sumF(a+1, b)
  sumF
}
/* Curring
* No need sumInt, sumCubes anymore
* */
sum_2(cube)(1, 3)

def sum_3(f: Int => Int)(a: Int, b: Int): Int =
  if (a > b) 0 else f(a) + sum_2(f)(a+1, b)
(sum_3(cube))(1, 3)


