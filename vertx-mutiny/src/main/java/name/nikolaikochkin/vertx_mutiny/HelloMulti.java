package name.nikolaikochkin.vertx_mutiny;

import io.smallrye.mutiny.Multi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class HelloMulti {

    private static final Logger log = LoggerFactory.getLogger(HelloMulti.class);

    public static void main(String[] args) {
        Multi.createFrom().items(IntStream.range(0, 10).boxed())
                .onItem().transform(integer -> integer * 2)
                .onItem().transform(String::valueOf)
                .select().last(2)
                .subscribe().with(log::info);
    }
}
