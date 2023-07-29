package name.nikolaikochkin.vertx_starter.customcodec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class RecordMessageCodec<T extends Record> implements MessageCodec<T, T> {
    private final Class<T> type;

    public RecordMessageCodec(Class<T> type) {
        this.type = type;
    }

    @Override
    public void encodeToWire(Buffer buffer, T record) {
        JsonObject jsonObject = JsonObject.mapFrom(record);
        Buffer encoded = jsonObject.toBuffer();
        buffer.appendInt(encoded.length());
        buffer.appendBuffer(encoded);
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        pos += 4;
        JsonObject jsonObject = new JsonObject(buffer.slice(pos, pos + length));
        return jsonObject.mapTo(type);
    }

    @Override
    public T transform(T record) {
        return record;
    }

    @Override
    public String name() {
        return type.getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
