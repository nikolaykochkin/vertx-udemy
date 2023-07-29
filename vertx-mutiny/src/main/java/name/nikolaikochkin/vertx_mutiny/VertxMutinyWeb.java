package name.nikolaikochkin.vertx_mutiny;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxMutinyWeb extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VertxMutinyWeb.class);

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new VertxMutinyWeb())
                .subscribe().with(id -> log.info("Started: {}", id));
    }

    @Override
    public Uni<Void> asyncStart() {
        Router router = Router.router(vertx);
        router.route().failureHandler(this::failureHandler);
        router.get("/users").respond(this::getUsers);
        return vertx.createHttpServer()
//                .requestHandler(request -> request.response().endAndForget("Hello!"))
                .requestHandler(router)
                .listen(8111)
                .replaceWithVoid();
    }

    private void failureHandler(RoutingContext routingContext) {
        routingContext.response()
                .setStatusCode(500)
                .endAndForget("Something went wrong :(");
    }

    private Uni<JsonArray> getUsers(RoutingContext routingContext) {
        return Uni.createFrom().item(
                JsonArray.of(
                        JsonObject.of("name", "Alice"),
                        JsonObject.of("name", "Bob")
                )
        );
    }
}
