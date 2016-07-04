/* Copyright 2016 UniCredit S.p.A.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.unicredit

package object trafalgar {

  def helper(f: (Double, Double, Double) => Boolean) =
  { t: Stats =>
    t match {
      case Stats(elem, μ, σ) => f(μ, σ, elem)
      case _ => false
    }
  }

  def sliding(f: (Double, Double, Seq[Double]) => Boolean) = { stats: Seq[Stats] =>
    val Stats(_, μ, σ) = stats.head
    val seq = stats.map(_.elem)

    f(μ, σ, seq)
  }

  def sameSide(s: Seq[Double], μ: Double) = s.forall(_ < μ) || s.forall(_ > μ)
}
