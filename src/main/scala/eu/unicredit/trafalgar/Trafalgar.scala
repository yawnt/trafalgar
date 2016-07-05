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
import akka.stream.scaladsl._

object Trafalgar {
  import Rules._

  def getFlow(name: Int) = Flow[Boolean].filter(x => x).map { bool =>
    new Throwable(s"Service is misbehaving - Flow $name")
  }

  def internalFlow = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val rules = 8
    val broadcastElems = b.add(Broadcast[Stats](rules))
    val merge = b.add(Merge[Throwable](rules))


    broadcastElems ~> rule1 ~> getFlow(1) ~> merge
    broadcastElems ~> rule2 ~> getFlow(2) ~> merge
    broadcastElems ~> rule3 ~> getFlow(3) ~> merge
    broadcastElems ~> rule4 ~> getFlow(4) ~> merge
    broadcastElems ~> rule5 ~> getFlow(5) ~> merge
    broadcastElems ~> rule6 ~> getFlow(6) ~> merge
    broadcastElems ~> rule7 ~> getFlow(7) ~> merge
    broadcastElems ~> rule8 ~> getFlow(8) ~> merge

    FlowShape(broadcastElems.in, merge.out)
  })

  def flow = Flow.fromGraph(new StatsFlow[Double]) via internalFlow

}
