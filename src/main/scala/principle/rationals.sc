
val t1_3 = new Rational(1, 3)
val t5_7 = new Rational(5, 7)
val t3_2 = new Rational(3, 2)
t1_3.numerator
t1_3.denominator
t1_3.add(t5_7)
t1_3.sub(t5_7).sub(t3_2)
t1_3.less(t5_7)
t1_3.max(t3_2)
// val t1_0 = new Rational(1, 0)
new Rational(1)


class Rational(x: Int, y: Int) {
  // enforce precondition on the caller
  require(y != 0, "denominator must nonzero!!")

  // poly constructor
  def this(x: Int) = this(x, 1)

  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a%b)
  def numerator = x
  def denominator = y
  def add(that: Rational): Rational = {
    new Rational(
      numerator * that.denominator + that.numerator * denominator,
      denominator * that.denominator
    )
  }

  def neg(): Rational = new Rational(-numerator, denominator)

  def sub(that: Rational): Rational= add(that.neg)

  def less(that: Rational): Boolean = this.numerator * that.denominator < that.numerator * this.denominator

  def max(that: Rational): Rational = if (this.less(that)) that else this

  def < (that: Rational): Boolean = this.numerator * that.denominator < that.numerator * this.denominator

  def + (that: Rational): Rational = {
    new Rational(
      this.numerator * that.denominator + that.numerator * this.denominator,
      this.denominator * that.denominator
    )
  }

  def - (that: Rational): Rational = this + -that

  def unary_- : Rational = new Rational(this.numerator, this.denominator)

  override def toString ={
    val g = gcd(numerator, denominator)
    numerator / g + "/" + denominator / g
  }


}