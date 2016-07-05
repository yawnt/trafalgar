package eu.unicredit.trafalgar

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Random

import akka.actor.ActorSystem

import akka.stream.scaladsl._
import akka.stream._
import akka.stream.ActorMaterializer

import org.scalatest.FunSpec

class TrafalgarSpec extends FunSpec {

  implicit val system = ActorSystem("TrafalgarSpec")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val baseIterator = Iterator.continually(10d)

  describe("Trafalgar") {

    it("should compute the right μ and σ at `n-1`") {
      val flow = Flow.fromGraph(new StatsFlow[Double])
      val future = Source((1 to 10).map(_.toDouble)) via flow runWith Sink.last

      val stats = Await.result(future, Duration.Inf)

      assert(stats.μ == 5.0)
      assert(stats.σ >= 2.73 && stats.σ <= 2.74)
    }
  }

  describe("Trafalgar has 8 rules and") {

    it("rule1 should work") {
      val future = Source(
        (for { i <- 1 to 10 } yield i % 2 + 1.0) :+ 10.0
      ) via new StatsFlow[Double] via Rules.rule1 runWith Sink.seq

      val seq = Await.result(future, Duration.Inf)

      assert(seq.filter(_ == true).size == 1)
    }

    it("rule2 should work") {
      val future = Source(
        (for { i <- 1 to 10 } yield i % 2 + 1.0) ++ (10 to 20).map(_.toDouble)
      ) via new StatsFlow[Double] via Rules.rule2 runWith Sink.seq

      val seq = Await.result(future, Duration.Inf)

      assert(seq.filter(_ == true).size == 3)
    }

    it("rule3 should work") {
      val future = Source(
        (for { i <- 1 to 10 } yield i % 2 + 1.0) ++ (10 to 16).map(_.toDouble)
      ) via new StatsFlow[Double] via Rules.rule3 runWith Sink.seq

      val seq = Await.result(future, Duration.Inf)

      assert(seq.filter(_ == true).size == 3)
    }

    it("rule4 should work") {
      val sourceSeq = Seq.fill(5)(5.0) ++ (for { i <- 1 to 14 } yield {
        if(i % 2 == 0)
          4.0
        else
          6.0
      })

      val future = Source(
        sourceSeq.to[collection.immutable.Seq]
      ) via new StatsFlow[Double] via Rules.rule4 runWith Sink.seq

      val seq = Await.result(future, Duration.Inf)

      assert(seq.filter(_ == true).size == 1)
    }

    it("rule5 should work") {
      val future = Source(
        (for { i <- 1 to 10 } yield i % 2 + 1.0) ++ Seq[Double](100,100)
      ) via new StatsFlow[Double] via Rules.rule5 runWith Sink.seq

      val seq = Await.result(future, Duration.Inf)

      assert(seq.filter(_ == true).size == 1)
    }

    it("rule6 should work") {
      val future1 = Source(
        (for { i <- 1 to 10 } yield i % 2 + 1.0) ++ Seq[Double](5,6,-7,8)
      ) via new StatsFlow[Double] via Rules.rule6 runWith Sink.seq
      val future2 = Source(
        (for { i <- 1 to 10 } yield i % 2 + 1.0) ++ Seq[Double](5,6,7,8)
      ) via new StatsFlow[Double] via Rules.rule6 runWith Sink.seq

      val seq1 = Await.result(future1, Duration.Inf)
      val seq2 = Await.result(future2, Duration.Inf)

      assert(seq1.filter(_ == true).size == 0)
      assert(seq2.filter(_ == true).size == 1)
    }

    it("rule7 should work") {
      val future = Source(
        (for { i <- 1 to 15 } yield i % 2 + 1.0)
      ) via new StatsFlow[Double] via Rules.rule7 runWith Sink.seq

      val seq = Await.result(future, Duration.Inf)
      assert(seq.filter(_ == true).size == 1)
    }

    it("rule8 should work") {
      val future = Source(
        (for { i <- 1 to 10 } yield i % 2 + 1.0) ++ (20 to 27).map(_.toDouble)
      ) via new StatsFlow[Double] via Rules.rule8 runWith Sink.seq

      val seq = Await.result(future, Duration.Inf)
      assert(seq.filter(_ == true).size == 2)
    }

  }

}
