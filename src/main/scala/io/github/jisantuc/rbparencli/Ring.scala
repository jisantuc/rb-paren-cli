package io.github.jisantuc.rbparencli

import cats.data.NonEmptyList
import cats.kernel.Eq
import cats.syntax.eq._

trait Ring[A] {
  protected[rbparencli] val underlying: List[A]

  def next: A
}

object Ring {

  def apply[A](h: A, t: A*): Ring[A] =
    fromNonEmptyList(NonEmptyList(h, t.toList))

  def fromNonEmptyList[A](as: NonEmptyList[A]) = new Ring[A] {

    protected[rbparencli] val underlying: List[A] = as.toList

    private var cursor = 0
    private def idx = cursor % as.length
    def next = {
      val out = underlying(idx)
      cursor += 1
      out
    }
  }

  implicit def eqRing[A: Eq]: Eq[Ring[A]] = new Eq[Ring[A]] {
    def eqv(x: Ring[A], y: Ring[A]): Boolean = {
      x.underlying === y.underlying
    }
  }
}
