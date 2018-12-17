// import org.apache.spark.sql.sources.LessThan

/* List property
* 1. Immutable
* 2. Recursive
* */

List(1, 2, 3).count(_ > 0)
1 :: 2 :: 3 :: Nil

/* Insertion Sort
* 1. sort tail
* 2. insert head in the right place
* */

def insertSort(xs: List[Int]): List[Int] = xs match {
  case List() => List() // empty list
  case head :: tail => insert(head, insertSort(tail))
}

def insert(x: Int, xs: List[Int]): List[Int] = xs match {
  case List() => List(x)
  case head :: tail =>
    if (x <= head) x::xs
    else head :: insert(x, tail)
}

def times(chars: List[Char]): List[(Char, Int)] = {
  chars.groupBy(identity).mapValues(_.size).toList
}

times(List('a', 'b', 'a'))

/**
  * MergeSort
  * 1. divide list into 2 half sub-lists
  * 2. sort 2 sub-lists
  * 3. merge 2 sorted sub-lists into single list
  */
def mergeSort(xList: List[Int]): List[Int] = {
  val n = xList.length / 2
  if (n == 0) xList
  else {
    val (firstList, secondList) = xList.splitAt(n)
    merge(mergeSort(firstList), mergeSort(secondList))
  }
}

def merge(firstList: List[Int], secondList: List[Int]): List[Int] =
  firstList match {
    case Nil => secondList
    case firstHead :: firstListRemain =>
      secondList match {
        case Nil => firstList
        case secondHead :: secondListRemain =>
          if (firstHead < secondHead) firstHead :: merge(firstListRemain, secondList)
          else secondHead :: merge(firstList, secondListRemain)
      }
  }

def mergePatternMatch(firstList: List[Int], secondList: List[Int]): List[Int] =
  (firstList, secondList) match {
    case (Nil, secondList) => secondList
    case (firstList, Nil) => firstList
    case (firstHead :: firstListRemain, secondHead :: secondListRemain) =>
      if (firstHead < secondHead) firstHead :: mergePatternMatch(firstListRemain, secondList)
      else secondHead :: mergePatternMatch(firstList, secondListRemain)
  }

mergeSort(List(1,2,5,4,3,2,1))

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

mergeSortT(List(1,2,5,6,3,2,1))((x: Int, y: Int) => x < y)

val a = List((1,"a"), (2,"b"), (2,"a"))
val b = List((1,"c"), (3, "a"))

