package io.github.jisantuc.rbparencli

import cats.data.NonEmptyList

trait Ring[A] {
  def next: A
}

object Ring {

  def apply[A](h: A, t: A*): Ring[A] =
    fromNonEmptyList(NonEmptyList(h, t.toList))

  def fromNonEmptyList[A](as: NonEmptyList[A]) = new Ring[A] {
    private var cursor = 0
    private def idx = cursor % as.length
    private val asList = as.toList
    def next = {
      val out = asList(idx)
      cursor += 1
      out
    }
  }
}
