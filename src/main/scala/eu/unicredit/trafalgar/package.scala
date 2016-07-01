package eu.unicredit

package object trafalgar {

  def helper[A](f: (Double, Double, A) => Boolean) =
  { t: (Stats, A) =>
    t match {
      case (Stats(μ, σ), elem) => f(μ, σ, elem)
      case _ => false
    }
  }

  def sameSide(s: Seq[Double], μ: Double) = s.forall(_ < μ) || s.forall(_ > μ)
}
