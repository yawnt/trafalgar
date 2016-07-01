package eu.unicredit.trafalgar

import akka.actor.ActorSystem

import akka.stream._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.stream.FlowShape

object Trafalgar {
  import Rules._

  def flow = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val rules = 8
    val broadcastElems = b.add(Broadcast[Double](rules + 1))
    val merge = b.add(Merge[Boolean](rules))
    val broadcastStats = b.add(Broadcast[Stats](rules))
    val finalFilter = Flow[Boolean].filter(identity)

    /* Source for Stats */
    broadcastElems ~> new StatsFlow[Double] ~> broadcastStats

    /* Rule 1 */
    val rule1Zip = b.add(Zip[Stats, Double])
    broadcastStats ~> rule1Zip.in0
    broadcastElems ~> rule1Zip.in1
    rule1Zip.out ~> rule1 ~> merge

    /* Rule 2 */
    val rule2Zip = b.add(Zip[Stats, Seq[Double]])
    broadcastStats ~> rule2Zip.in0
    broadcastElems.expand(Iterator.continually(_)).sliding(9) ~> rule2Zip.in1
    rule2Zip.out ~> rule2 ~> merge

    /* Rule 3 */
    val rule3Zip = b.add(Zip[Stats, Seq[Double]])
    broadcastStats ~> rule3Zip.in0
    broadcastElems.expand(Iterator.continually(_)).sliding(6) ~> rule3Zip.in1
    rule3Zip.out ~> rule3 ~> merge

    /* Rule 4 */
    val rule4Zip = b.add(Zip[Stats, Seq[Double]])
    broadcastStats ~> rule4Zip.in0
    broadcastElems.expand(Iterator.continually(_)).sliding(14) ~> rule4Zip.in1
    rule4Zip.out ~> rule4 ~> merge

    /* Rule 5 */
    val rule5Zip = b.add(Zip[Stats, Seq[Double]])
    broadcastStats ~> rule5Zip.in0
    broadcastElems.expand(Iterator.continually(_)).sliding(3) ~> rule5Zip.in1
    rule5Zip.out ~> rule5 ~> merge

    /* Rule 6 */
    val rule6Zip = b.add(Zip[Stats, Seq[Double]])
    broadcastStats ~> rule6Zip.in0
    broadcastElems.expand(Iterator.continually(_)).sliding(5) ~> rule6Zip.in1
    rule6Zip.out ~> rule6 ~> merge

    /* Rule 7 */
    val rule7Zip = b.add(Zip[Stats, Seq[Double]])
    broadcastStats ~> rule7Zip.in0
    broadcastElems.expand(Iterator.continually(_)).sliding(15) ~> rule7Zip.in1
    rule7Zip.out ~> rule7 ~> merge

    /* Rule 8 */
    val rule8Zip = b.add(Zip[Stats, Seq[Double]])
    broadcastStats ~> rule8Zip.in0
    broadcastElems.expand(Iterator.continually(_)).sliding(8) ~> rule8Zip.in1
    rule8Zip.out ~> rule8 ~> merge

    FlowShape(broadcastElems.in, merge.out)
  }).map(_ => new Exception("Service is misbehaving"))

}

object Main extends App {
  implicit val system = ActorSystem("main")
  implicit val mat = ActorMaterializer()

  Source(1 to 100).map(_.toDouble).via(Trafalgar.flow).runWith(Sink.foreach(println))
}
