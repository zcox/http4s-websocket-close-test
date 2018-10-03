```
$ sbt run

$ wscat -c localhost:8080/messages
connected (press CTRL+C to quit)
>

2018-10-03 16:51:10.707 [scala-execution-context-global-56] INFO  app.App - Evaluated response effect
2018-10-03 16:51:10.708 [scala-execution-context-global-56] INFO  app.App - On non-WebSocket request
2018-10-03 16:51:10.777 [scala-execution-context-global-63] INFO  app.App - Subscriber started

> CTRL+C

2018-10-03 16:51:20.792 [scala-execution-context-global-62] INFO  app.App - Subscriber closed

$ curl localhost:8080/messages
Request did not contain WebSocket headers

2018-10-03 16:51:30.814 [scala-execution-context-global-61] INFO  app.App - Evaluated response effect
2018-10-03 16:51:30.814 [scala-execution-context-global-61] INFO  app.App - On non-WebSocket request
```

Observations:
1. `On non-WebSocket request` is logged on valid WebSocket request. Probably not a big deal, just odd that the effect that creates the response for non-WebSocket requests is evaluated even on valid WebSocket requests.
2. `Evaluated response effect` is logged on invalid WebSocket request. Just have to be careful not to allocate resources in this effect, because they are not closed on non-WebSocket requests.
