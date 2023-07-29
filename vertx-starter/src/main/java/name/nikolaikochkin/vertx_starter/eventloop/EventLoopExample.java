package name.nikolaikochkin.vertx_starter.eventloop;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class EventLoopExample extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(EventLoopExample.class);

    public static void main(String[] args) {
        Vertx.vertx(
                new VertxOptions()
                        .setMaxEventLoopExecuteTime(500)
                        .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS)
                        .setBlockedThreadCheckInterval(1)
                        .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
                        .setEventLoopPoolSize(2)
        ).deployVerticle(
                EventLoopExample.class.getName(),
                new DeploymentOptions().setInstances(4)
        );
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        log.debug("Start {}", getClass().getSimpleName());
        Thread.sleep(5000);
    }
}
