package name.nikolaikochkin.vertx_mutiny;

import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloUni {

    private static final Logger log = LoggerFactory.getLogger(HelloUni.class);

    public static void main(String[] args) {
        Uni.createFrom()
                .item("Hello")
                .onItem().transform(item -> item + " Mutiny")
                .onItem().transform(String::toUpperCase)
                .subscribe().with(item -> log.info("Item: {}", item));

        Uni.createFrom().item("Ignored due to failure")
                .onItem().castTo(Integer.class)
                .subscribe().with(item -> log.info("Item: {}", item), failure -> log.error("Failure", failure));
    }
}
