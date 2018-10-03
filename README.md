### http4s 0.18.19

```
sbt run

$ wscat -c localhost:8080/messages
connected (press CTRL+C to quit)
>

2018-10-02 17:05:43.115 [scala-execution-context-global-57] INFO  app.App - Subscriber created
2018-10-02 17:05:43.116 [scala-execution-context-global-57] INFO  app.App - On non-WebSocket request

CTRL+C

2018-10-02 17:06:37.303 [scala-execution-context-global-70] INFO  app.App - Subscriber closed

$ curl localhost:8080/messages
Request did not contain WebSocket headers

2018-10-02 17:06:51.676 [scala-execution-context-global-68] INFO  app.App - Subscriber created
2018-10-02 17:06:51.677 [scala-execution-context-global-68] INFO  app.App - On non-WebSocket request
```

Observations:
1. `On non-WebSocket request` is logged on valid WebSocket request
2. `Subscriber created` is logged, but `Subscriber closed` is not logged, on invalid WebSocket request

### http4s 0.19.0-M3

```
sbt run

$ wscat -c localhost:8080/messages
connected (press CTRL+C to quit)
>

http4s-websocket-close-test 2018-10-03 13:20:24.733 [scala-execution-context-global-22] INFO  app.App - Subscriber created
http4s-websocket-close-test 2018-10-03 13:20:24.733 [scala-execution-context-global-22] INFO  app.App - On non-WebSocket request

CTRL+C

http4s-websocket-close-test 2018-10-03 13:20:43.002 [blaze-selector-0-1] INFO  app.App - Subscriber closed
http4s-websocket-close-test 2018-10-03 13:20:43.005 [blaze-selector-0-1] INFO  app.App - On close
http4s-websocket-close-test 2018-10-03 13:20:43.012 [blaze-selector-0-1] ERROR o.h.b.websocket.Http4sWSStage - Error closing Web Socket
http4s-websocket-close-test org.http4s.blaze.pipeline.Command$EOF$: EOF

$ curl localhost:8080/messages
Request did not contain WebSocket headers

http4s-websocket-close-test 2018-10-03 13:21:22.484 [scala-execution-context-global-26] INFO  app.App - Subscriber created
http4s-websocket-close-test 2018-10-03 13:21:22.484 [scala-execution-context-global-26] INFO  app.App - On non-WebSocket request
```

Observations:
1. `On non-WebSocket request` is logged on valid WebSocket request
2. `Subscriber created` is logged, but neither `Subscriber closed` nor `On close` are logged, on invalid WebSocket request
