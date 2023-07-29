package name.nikolaikochkin.vertx_starter.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseJSONExample {


    public static final String ADDRESS = "my.request.address";

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RequestVerticle());
        vertx.deployVerticle(new ResponseVerticle());
    }

    static class RequestVerticle extends AbstractVerticle {
        private static final Logger log = LoggerFactory.getLogger(RequestVerticle.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            JsonObject message = new JsonObject()
                    .put("message", "Hello World!")
                    .put("version", 1);
            log.debug("Sending: {}", message);
            vertx.eventBus().<JsonObject>request(ADDRESS, message, reply -> log.debug("Response: {}", reply.result().body()));
        }
    }

    static class ResponseVerticle extends AbstractVerticle {
        private static final Logger log = LoggerFactory.getLogger(ResponseVerticle.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<JsonObject>consumer(ADDRESS, message -> {
                log.debug("Received Message: {}", message.body());
                message.reply(new JsonArray().add("one").add("two").add("three"));
            });
        }
    }
}
