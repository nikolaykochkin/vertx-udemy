package name.nikolaikochkin.vertx_starter.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class PublishSubscribeExample {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(Publisher.class.getName());
        vertx.deployVerticle(Subscriber.class.getName(), new DeploymentOptions().setInstances(4));
    }

    public static class Publisher extends AbstractVerticle {
        private static final Logger log = LoggerFactory.getLogger(Publisher.class);

        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            log.debug("Start sending");
            vertx.setPeriodic(1000, id ->
                    vertx.eventBus().<String>publish(Publisher.class.getName(),
                            "Publish a message for everyone! Number " + counter.incrementAndGet()));
        }
    }

    public static class Subscriber extends AbstractVerticle {
        private static final Logger log = LoggerFactory.getLogger(Subscriber.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            vertx.eventBus().<String>consumer(Publisher.class.getName(),
                    message -> log.debug("Received: {}", message.body()));
        }
    }
}
