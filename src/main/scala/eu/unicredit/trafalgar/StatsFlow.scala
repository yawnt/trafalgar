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

import akka.stream._
import akka.stream.FlowShape
import akka.stream.stage._

case class Stats(
  μ: Double,
  σ: Double
)

class StatsFlow[A <: Double] extends GraphStage[FlowShape[A, Stats]] {
  val in: Inlet[A] = Inlet("Stats.in")
  val out: Outlet[Stats] = Outlet("Stats.out")

  val shape = FlowShape.of(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      private var nOfElements = 0
      private var μ = 0d
      private var M2 = 0d

      setHandler(in, new InHandler {
        override def onPush: Unit = {
          val element = grab(in)

          nOfElements += 1
          val δ = element - μ
          μ += δ / nOfElements

          M2 += δ * (element - μ)

          push(out, Stats(
            μ,
            Math.sqrt(M2 / nOfElements)
          ))

        }

      })
      setHandler(out, new OutHandler {
        override def onPull: Unit = pull(in)
      })
    }

}
