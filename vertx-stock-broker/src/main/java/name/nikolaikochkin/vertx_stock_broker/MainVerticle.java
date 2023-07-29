package name.nikolaikochkin.vertx_stock_broker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);
    public static final int PORT = 8888;

    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.exceptionHandler(error -> log.error("Unhandled: {}", error.getMessage(), error));
        vertx.deployVerticle(new MainVerticle(), ar -> {
            if (ar.failed()) {
                log.error("Failed to deploy: {}", ar.cause().getMessage(), ar.cause());
                return;
            }
            log.info("Deployed {}!", MainVerticle.class.getName());
        });
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router restApi = Router.router(vertx);
        restApi.route().failureHandler(failureHandler());
        AssetsRestApi.attach(restApi);
        vertx.createHttpServer()
                .requestHandler(restApi)
                .exceptionHandler(error -> log.error("HTTP Server error: {}", error.getMessage(), error))
                .listen(PORT, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        log.info("HTTP server started on port 8888");
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }

    private static Handler<RoutingContext> failureHandler() {
        return errorContext -> {
            if (errorContext.response().ended()) {
                // Ignore completed response
                return;
            }
            log.error("Route error: {}", errorContext.failure().getMessage(), errorContext.failure());
            errorContext.response()
                    .setStatusCode(500)
                    .end(JsonObject.of(
                            "message", "Something went wrong :(",
                            "error", errorContext.failure().getMessage()
                    ).toBuffer());
        };
    }
}
