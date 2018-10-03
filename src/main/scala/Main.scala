package app

import cats.implicits._
import cats.effect._
import fs2.{Stream, Sink}
import fs2.concurrent.Topic
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze._
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebsocketBits._
import scala.concurrent.duration._
import scala.language.postfixOps
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    new App[IO](Slf4jLogger.unsafeFromClass(classOf[App[IO]])).stream.compile.drain.as(ExitCode.Success)
}

class App[F[_]: ConcurrentEffect : Timer](log: Logger[F]) extends Http4sDsl[F] {

  def service(topic: Topic[F, Option[String]]) = HttpRoutes.of[F] {
    case GET -> Root / "messages" => 
      val send = topic.subscribe(100).tail.unNone.map(s => Text(s)).onFinalize(log.info("Subscriber closed"))
      val receive: Sink[F, WebSocketFrame] = _.collect{case Text(s, _) => s.some}.to(topic.publish)
      val onNonWebSocketRequest: F[Response[F]] = log.info("On non-WebSocket request") *> Response[F](Status.NotImplemented).withEntity("Request did not contain WebSocket headers").pure[F]
      val onHandshakeFailure: F[Response[F]] = log.info("On handshake failure") *> Response[F](Status.BadRequest).withEntity("Unable to complete WebSocket handshake").pure[F]
      val onClose: F[Unit] = log.info("On close")
      log.info("Subscriber created") *> WebSocketBuilder[F].build(send, receive, onNonWebSocketRequest = onNonWebSocketRequest, onHandshakeFailure = onHandshakeFailure, onClose = onClose)
  }

  def stream: Stream[F, ExitCode] =
    for {
      topic <- Stream.eval(Topic[F, Option[String]](None))
      x <- BlazeServerBuilder[F]
        .bindHttp(8080)
        .withWebSockets(true)
        .withHttpApp(service(topic).orNotFound)
        .withIdleTimeout(30 minutes)
        .serve
    } yield x
}
