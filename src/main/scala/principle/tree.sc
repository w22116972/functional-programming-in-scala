/*
* Implement Binary Search Tree
*
* */


abstract class IntSet {
  def insert(x: Int): IntSet
  def contains(x: Int): Boolean
  def union(that: IntSet): IntSet
}

object EmptyTree extends IntSet {
  override def contains(x: Int): Boolean = false

  override def insert(x: Int): IntSet = new NonEmptyTree(x, EmptyTree, EmptyTree)

  override def union(that: IntSet): IntSet = that

  override def toString: String = "."
}

class NonEmptyTree(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  override def contains(x: Int): Boolean = {
    if (x < elem) this.left.contains(x)
    else if (x > elem) this.right.contains(x)
    else true
  }

  override def insert(x: Int): IntSet = {
    if (x < elem) new NonEmptyTree(this.elem, this.left.insert(x), this.right)
    else if (x > elem) new NonEmptyTree(this.elem, this.left, this.right.insert(x))
    else this
  }

  override def union(that: IntSet): IntSet = {
    this.left.union(this.right).union(that).insert(this.elem)
  }

  override def toString: String = "{" + this.left + this.elem + this.right + "}"
}


val tree1 = new NonEmptyTree(2, EmptyTree, EmptyTree)
val tree2 = tree1.insert(1).insert(3)

