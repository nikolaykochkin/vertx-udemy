package name.nikolaikochkin.vertx_stock_broker;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetsRestApi {

    private static final Logger log = LoggerFactory.getLogger(AssetsRestApi.class);

    static void attach(Router parent) {
        parent.get("/assets")
                .handler(context -> {
                    var response = JsonArray.of(
                            new Asset("AAPL"),
                            new Asset("AMZN"),
                            new Asset("NFLX"),
                            new Asset("TSLA")
                    );
                    log.info("Path {} responds with {}", context.normalizedPath(), response.encode());
                    context.response().end(response.toBuffer());
                });
    }
}
