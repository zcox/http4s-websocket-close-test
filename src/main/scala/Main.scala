package app

import cats.implicits._
import cats.effect._
import fs2.{Stream, StreamApp, async, Sink}
import fs2.StreamApp.ExitCode
import fs2.async.mutable.Topic
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze._
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebsocketBits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends App[IO](Slf4jLogger.unsafeFromClass(classOf[App[IO]]))

class App[F[_]](log: Logger[F])(implicit F: Effect[F]) extends StreamApp[F] with Http4sDsl[F] {

  def service(topic: Topic[F, Option[String]]) = HttpService[F] {
    case GET -> Root / "messages" => 
      val send = topic.subscribe(100).tail.unNone.map(s => Text(s)).onFinalize(log.info("Subscriber closed"))
      val receive: Sink[F, WebSocketFrame] = _.collect{case Text(s, _) => s.some}.to(topic.publish)
      val onNonWebSocketRequest: F[Response[F]] = log.info("On non-WebSocket request") *> Response[F](Status.NotImplemented).withBody("Request did not contain WebSocket headers")
      val onHandshakeFailure: F[Response[F]] = log.info("On handshake failure") *> Response[F](Status.BadRequest).withBody("Unable to complete WebSocket handshake")
      log.info("Subscriber created") *> WebSocketBuilder[F].build(send, receive, onNonWebSocketRequest = onNonWebSocketRequest, onHandshakeFailure = onHandshakeFailure)
  }

  override def stream(args: List[String], requestShutdown: F[Unit]): Stream[F, ExitCode] =
    for {
      topic <- Stream.eval(async.topic[F, Option[String]](None))
      x <- BlazeBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .mountService(service(topic), "/")
        .withIdleTimeout(30 minutes)
        .serve
    } yield x
}
