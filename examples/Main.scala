object Main extends App {
  implicit val system = ActorSystem("example")
  implicit val mat = ActorMaterializer()

  Source(1 to 100)
    .map(_.toDouble)
    .via(Trafalgar.flow)
    .runWith(Sink.foreach(println))
}
