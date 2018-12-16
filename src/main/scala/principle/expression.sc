trait Expression {
  def isNumber: Boolean
  def isSum: Boolean
  def numValue: Int
  def leftOp: Expression
  def rightOp: Expression
}

class Number(n: Int) extends Expression {
  override def isNumber: Boolean = true
  override def isSum: Boolean = false
  override def numValue: Int = n
  override def leftOp: Expression = throw new Error("Number.leftOp")
  override def rightOp: Expression = throw new Error("Number.rightOp")
}

class Sum(exp1: Expression, exp2: Expression) extends Expression {
  override def isNumber: Boolean = false
  override def isSum: Boolean = true
  override def numValue: Int = throw new Error("Sum.numValue")
  override def leftOp: Expression = exp1
  override def rightOp: Expression = exp2
}

/* 1. use `get` and `is` strategic
* -: lots of `is`
* */

def eval(exp: Expression): Int = {
  if (exp.isNumber) exp.numValue
  else if (exp.isSum) eval(exp.leftOp) + eval(exp.rightOp)
  else throw new Error("Unknown expression " + exp)
}

eval(new Sum(new Number(1), new Number(2)))

// 如果想要再新增的話, 很多都要重寫

/* 2. above way
* +: No need for `isNumber` and `isSum`
* -: low-level + unsafe
* */
def eval_TypeTest(exp: Expression): Int =
  if (exp.isInstanceOf[Number])
    exp.asInstanceOf[Number].numValue
  else if (exp.isInstanceOf[Sum])
    eval_TypeTest(exp.asInstanceOf[Sum].leftOp) +
    eval_TypeTest(exp.asInstanceOf[Sum].rightOp)
  else
    throw new Error("Unknown expression" + exp)

/* 3. Object-Oriented Decomposition
* -: Need to touch all classes to add a new method
* */

trait Expr {
  def eval: Int
  // def show: String
}

class Number_OO(n: Int) extends Expr {
  override def eval: Int = n

}

class Sum_OO(e1: Expr, e2: Expr) extends Expr {
  override def eval: Int = e1.eval + e2.eval
}

/* 4. Functional Decomposition with Pattern Matching
*
* */

trait Expr_PM {
  def eval_PM(e: Expr_PM): Int = e match {
    case Number_PM(n) => n
    case Sum_PM(e1, e2) => eval_PM(e1) + eval_PM(e2)
  }
  def show(e: Expr_PM): String = e match {
    case Number_PM(n) => n.toString
    case Sum_PM(e1, e2) => show(e1) + "" + show(e2)
  }
}
case class Number_PM(n: Int) extends Expr_PM
case class Sum_PM(e1: Expr_PM, e2: Expr_PM) extends  Expr_PM
/* Implicitly define objects with `apply`
*  object Number_PM {
*   def apply(n: Int) = new Number(n)
*  }
*  object Sum_PM {
*   def apply(e1: Expr, e2: Expr) = new Sum(e1, e2)
*  }
*  */
Number_PM(1) // instead of `new Number(1)`
