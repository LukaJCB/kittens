/*
 * Copyright (c) 2015 Miles Sabin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cats.derived

import cats.Eq
import org.scalacheck.{Cogen, Arbitrary}, Arbitrary.arbitrary

object TestDefns {
  sealed trait IList[A]
  final case class ICons[A](head: A, tail: IList[A]) extends IList[A]
  final case class INil[A]() extends IList[A]

  object IList {
    def fromSeq[T](ts: Seq[T]): IList[T] =
      ts.foldRight(INil[T](): IList[T])(ICons(_, _))

    def toList[T](l: IList[T]): List[T] = l match {
      case INil() => Nil
      case ICons(h, t) => h :: toList(t)
    }

  }

  implicit def arbIList[A:Arbitrary]: Arbitrary[IList[A]] = Arbitrary(
    arbitrary[Seq[A]].map(IList.fromSeq))

  implicit def cogenIList[A:Cogen]: Cogen[IList[A]] =
    Cogen[Seq[A]].contramap(IList.toList)

  sealed trait Snoc[A]
  final case class SCons[A](init: Snoc[A], last: A) extends Snoc[A]
  final case class SNil[A]() extends Snoc[A]

  object Snoc {
    def fromSeq[T](ts: Seq[T]): Snoc[T] =
      ts.foldLeft(SNil[T](): Snoc[T])(SCons(_, _))
  }

  sealed trait Tree[T]
  final case class Leaf[T](t: T) extends Tree[T]
  final case class Node[T](l: Tree[T], r: Tree[T]) extends Tree[T]

  case class CaseClassWOption[T](a: Option[T])

  final case class Foo(i: Int, b: Option[String])

  implicit val arbFoo: Arbitrary[Foo] =
    Arbitrary(for {
      i <- arbitrary[Int]
      b <- arbitrary[Option[String]]
    } yield Foo(i, b))

  implicit val eqFoo: Eq[Foo] =
    Eq.fromUniversalEquals
}
