package concurrent
package common

object MergeSort {


//  def parMergeSort(xs: Array[Int], maxDepth: Int): Unit = {
//    val intermediate = new Array[Int](xs.length)
//    // at each level of merge sort, alternate source array and intermediate
//    def sort(from: Int, until: Int, depth: Int): Unit = {
//      if (depth == maxDepth)
//        quickSort(xs, from , until - from)
//      else {
//        val mid: Int = (from + until) / 2
//        parallel(sort(mid, until, depth + 1), sort(from, mid, depth + 1))
//        val flip: Boolean = (maxDepth - depth) % 2 == 0
//        val src: Array[Int] = if (flip) intermediate else xs
//        val dst: Array[Int] = if (flip) xs else intermediate
//        merge(src, dst, from ,mid, until)
//      }
//    }
//    def copy(src: Array[Int], target: Array[Int], from: Int, until: Int, depth: Int) = {
//      if (depth == maxDepth) {
//        Array.copy(src, from, target, from, until - from)
//      }
//      else {
//        val mid: Int = (from + until) / 2
//        val right = parallel (
//          copy(src, target, mid, until, depth + 1),
//          copy(src, target, from ,mid, depth + 1)
//        )
//      }
//      if (maxDepth % 2 == 0) {
//        copy()
//      }
//    }
//
//    sort(0, xs.length, 0)
//
//  }
  private def quickSort(xs: Array[Int], from: Int, end: Int): Unit = {

  }



}
