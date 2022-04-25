package uc1;

import util.ConsumerDataCondition;
import util.CustomKafkaConsumer;
import util.GUI;
import util.OrderedDataCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * TODO: DESCRIBE THIS PARTICULAR VERSION'S DETAILS HERE
 */
public class PConsumer {
    public static void main(String[] args) throws InterruptedException {
        GUI.setGUILook(new String[] { "GTK+", "Nimbus" });
        String topic = "Sensor";
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.DoubleDeserializer");
        props.put("group.id","0");
        //TODO: MISSING PROPERTIES

        List<ConsumerDataCondition<String,Double>> conditions = new ArrayList<>();
        conditions.add(new OrderedDataCondition((previous,current)-> (int) (current.timestamp()-previous.timestamp()),"Order by timestamp ascending"));

        CustomKafkaConsumer receiver = new CustomKafkaConsumer(topic,props,conditions,new GUI("Consumer 1"));
        receiver.run();
        receiver.join();
    }
}
