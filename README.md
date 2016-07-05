trafalgar
=========

[Nelson rules](https://en.wikipedia.org/wiki/Nelson_rules) are heuristics used to determine out-of-control processes. This is an implementation of the 8 rules which exposes an `akka-stream` interface.

### Usage

```scala
object Main extends App {
  implicit val system = ActorSystem("example")
  implicit val mat = ActorMaterializer()

  Source(1 to 100)
    .map(_.toDouble)
    .via(Trafalgar.flow)
    .runWith(Sink.foreach(println))
}
```

You should see `Throwable`s being printed to `stdout`.

### License

APACHE-2
