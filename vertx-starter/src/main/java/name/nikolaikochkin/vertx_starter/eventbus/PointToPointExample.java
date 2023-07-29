package name.nikolaikochkin.vertx_starter.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PointToPointExample {


    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.deployVerticle(new Sender());
        vertx.deployVerticle(new Receiver());
    }

    static class Sender extends AbstractVerticle {
        private static final Logger log = LoggerFactory.getLogger(Sender.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            log.debug("Start sending");
            vertx.setPeriodic(1000, id -> vertx.eventBus().<String>send(
                    Sender.class.getName(),
                    "Sending a message...")
            );
            log.debug("Finish sending");
        }
    }

    static class Receiver extends AbstractVerticle {
        private static final Logger log = LoggerFactory.getLogger(Receiver.class);

        @Override
        public void start(Promise<Void> startPromise) throws Exception {
            startPromise.complete();
            log.debug("Start receiving");
            vertx.eventBus().<String>consumer(Sender.class.getName(),
                    message -> log.debug("Received: {}", message.body()));
            log.debug("Finish receiving");
        }
    }
}
