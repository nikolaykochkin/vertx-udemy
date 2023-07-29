package name.nikolaikochkin.vertx_mutiny.db;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class VertxMutinyReactiveSQL extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VertxMutinyReactiveSQL.class);
    public static final int HTTP_SERVER_PORT = 8111;

    public static void main(String[] args) {
        int port = EmbeddedPostgres.startPostgres();
        var options = new DeploymentOptions()
                .setConfig(JsonObject.of("port", port));
        Vertx.vertx().deployVerticle(new VertxMutinyReactiveSQL(), options)
                .subscribe().with(id -> log.info("Started: {}", id));
    }

    @Override
    public Uni<Void> asyncStart() {
        var db = createPgPool(config());
        Router router = Router.router(vertx);
        router.route().failureHandler(this::failureHandler);
        router.get("/users").respond(context -> getUsers(db));
        return vertx.createHttpServer()
                .requestHandler(router)
                .listen(HTTP_SERVER_PORT)
                .replaceWithVoid();
    }

    private PgPool createPgPool(JsonObject config) {
        var connectOptions = new PgConnectOptions()
                .setPort(config.getInteger("port"))
                .setDatabase(EmbeddedPostgres.DATABASE_NAME)
                .setUser(EmbeddedPostgres.USERNAME)
                .setPassword(EmbeddedPostgres.PASSWORD);

        var poolOptions = new PoolOptions();

        return PgPool.pool(vertx, connectOptions, poolOptions);
    }

    private void failureHandler(RoutingContext routingContext) {
        routingContext.response()
                .setStatusCode(500)
                .endAndForget("Something went wrong :(");
    }

    private Uni<JsonArray> getUsers(PgPool db) {
        return db.query("SELECT * FROM users")
                .execute()
                .onItem().transform(rows ->
                        StreamSupport.stream(rows.spliterator(), false)
                                .map(Row::toJson)
                                .collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
    }
}
