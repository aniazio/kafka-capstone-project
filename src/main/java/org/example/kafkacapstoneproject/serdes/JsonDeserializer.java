package org.example.kafkacapstoneproject.serdes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class JsonDeserializer<T> implements Deserializer<T> {

    private final Gson gson = new GsonBuilder().create();

    private Class<T> destinationClass;

    public JsonDeserializer(Class<T> destinationClass) {
        this.destinationClass = destinationClass;
    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        // nothing to do
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;

        try {
            Type type = destinationClass;
            return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), type);
        } catch (Exception e) {
            log.error(destinationClass.getName());
            log.error("Error deserializing {}", e);
            throw new SerializationException("Error deserializing message", e);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
