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

  def flow = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val rules = 8
    val broadcastElems = b.add(Broadcast[Stats](rules + 1))
    val merge = b.add(Merge[Boolean](rules))

    new StatsFlow[Double].shape ~> broadcastElems

    broadcastElems ~> rule1 ~> merge
    broadcastElems ~> rule2 ~> merge
    broadcastElems ~> rule3 ~> merge
    broadcastElems ~> rule4 ~> merge
    broadcastElems ~> rule5 ~> merge
    broadcastElems ~> rule6 ~> merge
    broadcastElems ~> rule7 ~> merge
    broadcastElems ~> rule8 ~> merge

    FlowShape(broadcastElems.in, merge.out)
  }).map(_ => new Exception("Service is misbehaving"))

}
