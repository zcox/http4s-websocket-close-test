```
sbt run

wscat -c localhost:8080/messages
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
