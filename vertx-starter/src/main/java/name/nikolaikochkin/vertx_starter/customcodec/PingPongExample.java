package name.nikolaikochkin.vertx_starter.customcodec;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingPongExample {

    private static final Logger log = LoggerFactory.getLogger(PingPongExample.class);
    private static final Handler<AsyncResult<String>> logOnError = event -> {
        if (event.failed()) {
            log.error("Deploy failed: {}", event.cause().getMessage(), event.cause());
        }
    };

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(Ping.class, new RecordMessageCodec<>(Ping.class));
        vertx.eventBus().registerDefaultCodec(Pong.class, new RecordMessageCodec<>(Pong.class));
        vertx.deployVerticle(new PingVerticle(), logOnError);
        vertx.deployVerticle(new PongVerticle(), logOnError);
    }

    static class PingVerticle extends AbstractVerticle {
        private static final Logger log = LoggerFactory.getLogger(PingVerticle.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            Ping message = new Ping("Hello", true);
            log.debug("Sending: {}", message);
            vertx.eventBus().<Pong>request(
                    PingVerticle.class.getName(),
                    message,
                    reply -> {
                        if (reply.succeeded()) {
                            log.debug("Response: {}", reply.result().body());
                        } else {
                            log.error("Failed", reply.cause());
                        }
                    }
            );
        }
    }

    static class PongVerticle extends AbstractVerticle {
        private static final Logger log = LoggerFactory.getLogger(PongVerticle.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<Ping>consumer(PingVerticle.class.getName(), message -> {
                log.debug("Received Message: {}", message.body());
                message.reply(new Pong(42));
            }).exceptionHandler(error -> {
                log.error("Receiving failed", error);
            });
        }
    }
}
