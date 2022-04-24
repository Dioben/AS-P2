package uc1;

import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * TODO: DESCRIBE THIS PARTICULAR VERSION'S DETAILS HERE
 */
public class PConsumer {
    public static void main(String[] args) {
        String topic = "Sensor";
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("key.deserializer","org.apache.common.serialization.StringSerializer");
        props.put("value.deserializer","org.apache.common.serialization.StringSerializer");

    }
}
