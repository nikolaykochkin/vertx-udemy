package name.nikolaikochkin.vertx_starter.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleN extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VerticleN.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        log.debug("Start {} with config {}", getClass().getSimpleName(), config().toString());
        startPromise.complete();
    }
}
