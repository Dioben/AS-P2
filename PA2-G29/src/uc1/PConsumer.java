package uc1;

import util.ConsumerDataCondition;
import util.CustomKafkaConsumer;
import util.OrderedDataCondition;
import util.ProducerSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * TODO: DESCRIBE THIS PARTICULAR VERSION'S DETAILS HERE
 */
public class PConsumer {
    public static void main(String[] args) throws InterruptedException {
        String topic = "Sensor";
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("key.deserializer","org.apache.common.serialization.StringSerializer");
        props.put("value.deserializer","org.apache.common.serialization.StringSerializer");
        //TODO: MISSING PROPERTIES

        List<ConsumerDataCondition<String,Double>> conditions = new ArrayList<>();
        conditions.add(new OrderedDataCondition((previous,current)-> (int) (current.timestamp()-previous.timestamp()),"Order by timestamp ascending"));

        CustomKafkaConsumer receiver = new CustomKafkaConsumer(topic,props,conditions);
        receiver.run();
        receiver.join();
    }
}
