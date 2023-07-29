package name.nikolaikochkin.vertx_starter.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleB extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VerticleB.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        log.debug("Start {}", getClass().getSimpleName());
        startPromise.complete();
    }
}
