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

package eu.unicredit.trafalgar

import akka.stream.scaladsl.Flow

object Rules {

  /* Point is over 3 standard deviations from mean */
  def rule1 = Flow.fromFunction(helper { (μ, σ, curr) =>
    (curr > μ + 3 * σ) || (curr < μ - 3 * σ)
  })

  /* 9+ points are on the same side of the mean */
  def rule2 = Flow[Stats].sliding(9).map(sliding { (μ, _, seq) =>
    seq.forall(_ > μ) || seq.forall(_ < μ)
  })

  /* 6+ points are continually increasing / decreasing */
  def rule3 = Flow[Stats].sliding(6).map(sliding { (_, _, seq) =>
    seq.sliding(2).forall { x =>
      x(0) > x(1)
    } ||
    seq.sliding(2).forall { x =>
      x(0) < x(1)
    }
  })

  /* 14+ points alternate in direction */
  def rule4 = Flow[Stats].sliding(14).map(sliding { (μ, _, seq) =>
    val forall = { (t: (Boolean, Boolean), elem: Double) =>
      val (trend, result) = t

      if(trend) (false, elem > μ && result)
      else (true, elem < μ && result)
    }
    seq.foldLeft((true, true))(forall)._2 || seq.foldLeft((false, true))(forall)._2
  })

  /* 2/3 points are more than 2 standard deviations from the mean in the same direction */
  def rule5 = Flow[Stats].sliding(3).map(sliding { (μ, σ, seq) =>
    val lessThanMean = seq.filter(_ < μ - 2 * σ)
    val greaterThanMean = seq.filter(_ > μ + 2 * σ)

    lessThanMean.size >= 2 && sameSide(lessThanMean, μ) ||
    greaterThanMean.size >= 2 && sameSide(greaterThanMean, μ)
  })

  /* 4/5 points are more than 1 standard deviation from the mean in the same direction */
  def rule6 = Flow[Stats].sliding(5).map(sliding { (μ, σ, seq) =>
    val lessThanMean = seq.filter(_ < μ - σ)
    val greaterThanMean = seq.filter(_ > μ + σ)

    lessThanMean.size >= 4 && sameSide(lessThanMean, μ) ||
    greaterThanMean.size >= 4 && sameSide(greaterThanMean, μ)
  })

  /* 15 points are all within 1 standard deviation from the mean */
  def rule7 = Flow[Stats].sliding(15).map(sliding { (μ, σ, seq) =>
    seq.forall { elem =>
      elem > μ - σ || elem < μ + σ
    }
  })

  /* 8 points in a row are all above or below 1 standard deviation from the mean */
  def rule8 = Flow[Stats].sliding(8).map(sliding { (μ, σ, seq) =>
    seq.forall { elem =>
      elem > μ + σ || elem < μ - σ
    }
  })

}
