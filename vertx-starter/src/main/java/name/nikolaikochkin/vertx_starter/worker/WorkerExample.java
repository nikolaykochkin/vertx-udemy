package name.nikolaikochkin.vertx_starter.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerExample extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(WorkerExample.class);

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new WorkerExample());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        log.debug("Start Worker");
        vertx.deployVerticle(new WorkerVerticle(), new DeploymentOptions().setWorker(true));
        log.debug("Start Event Loop");
        startPromise.complete();
        vertx.executeBlocking(event -> {
            log.debug("Executing blocking code");
            try {
                Thread.sleep(5000);
                event.complete();
            } catch (InterruptedException e) {
                log.error("Failed: ", e);
                event.fail(e);
            }
            log.debug("Finish blocking code");
        }, result -> {
            if (result.succeeded()) {
                log.debug("Blocking call done");
            } else {
                log.debug("Blocking call failed due to: ", result.cause());
            }
        });
    }
}
