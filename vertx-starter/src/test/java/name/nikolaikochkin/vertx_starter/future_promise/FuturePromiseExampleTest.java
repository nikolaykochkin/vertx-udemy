package name.nikolaikochkin.vertx_starter.future_promise;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(VertxExtension.class)
public class FuturePromiseExampleTest {

    private static final Logger log = LoggerFactory.getLogger(FuturePromiseExampleTest.class);

    @Test
    void shouldPromiseSuccess(Vertx vertx, VertxTestContext context) {
        Promise<String> promise = Promise.promise();
        log.debug("Start");
        vertx.setTimer(500, id -> {
            promise.complete("Success");
            log.debug("Success");
            context.completeNow();
        });
        log.debug("End");
    }

    @Test
    void shouldPromiseFail(Vertx vertx, VertxTestContext context) {
        Promise<String> promise = Promise.promise();
        log.debug("Start");
        vertx.setTimer(500, id -> {
            promise.fail(new RuntimeException("Failed!"));
            log.debug("Failure");
            context.completeNow();
        });
        log.debug("End");
    }

    @Test
    void shouldFutureSuccess(Vertx vertx, VertxTestContext context) {
        Promise<String> promise = Promise.promise();
        log.debug("Start");
        vertx.setTimer(500, id -> {
            promise.complete("Success");
            log.debug("Success");
        });
        promise.future()
                .onSuccess(result -> {
                    log.debug("End {}", result);
                    context.completeNow();
                })
                .onFailure(context::failNow);
    }

    @Test
    void shouldFutureFailure(Vertx vertx, VertxTestContext context) {
        Promise<String> promise = Promise.promise();
        log.debug("Start");
        vertx.setTimer(500, id -> {
            promise.fail(new RuntimeException("Failed!"));
            log.debug("Timer done");
        });
        promise.future()
                .onSuccess(result -> {
                    log.debug("Result: {}", result);
                    context.completeNow();
                })
                .onFailure(result -> {
                    log.error("Failed: ", result);
                    context.completeNow();
                });
    }

    @Test
    void shouldFutureSuccessMap(Vertx vertx, VertxTestContext context) {
        Promise<String> promise = Promise.promise();
        log.debug("Start");
        vertx.setTimer(500, id -> {
            promise.complete("Success");
            log.debug("Success");
        });
        promise.future()
                .map(s -> {
                    log.debug("String to JsonObject");
                    return new JsonObject().put("key", s);
                })
                .map(jsonObject -> new JsonArray().add(jsonObject))
                .onSuccess(result -> {
                    log.debug("End {}", result);
                    context.completeNow();
                })
                .onFailure(context::failNow);
    }

    @Test
    void shouldFutureCoordinate(Vertx vertx, VertxTestContext context) {
        vertx.createHttpServer()
                .requestHandler(request -> log.debug("{}", request))
                .listen(10_000)
                .compose(server -> {
                    log.info("Another task");
                    return Future.succeededFuture(server);
                })
                .compose(server -> {
                    log.info("Even more");
                    return Future.succeededFuture(server);
                })
                .onFailure(context::failNow)
                .onSuccess(server -> {
                    log.debug("Server started on port {}", server.actualPort());
                    context.completeNow();
                });
    }

    @Test
    void shouldFutureCompose(Vertx vertx, VertxTestContext context) {
        var one = Promise.<Void>promise();
        var two = Promise.<Void>promise();
        var three = Promise.<Void>promise();

        var futureOne = one.future();
        var futureTwo = two.future();
        var futureThree = three.future();

        Future.all(futureOne, futureTwo, futureThree)
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    log.debug("Success {}", result);
                    context.completeNow();
                });

        vertx.setTimer(500, id -> {
            one.complete();
            two.complete();
            three.complete();
        });
    }
}
