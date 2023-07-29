package name.nikolaikochkin.vertx_starter.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleA extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(VerticleA.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        log.debug("Start {}", getClass().getSimpleName());
        vertx.deployVerticle(new VerticleAA(), whenDeployed -> {
            log.debug("Deployed {}", VerticleAA.class.getSimpleName());
            vertx.undeploy(whenDeployed.result());
        });
        vertx.deployVerticle(new VerticleAB(), whenDeployed -> {
            log.debug("Deployed {}", VerticleAB.class.getSimpleName());
//            vertx.undeploy(whenDeployed.result());
        });
        startPromise.complete();
    }
}
