package uc6;

import util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys 3 consumer threads in a group that attempt to stick to different partitions (6)<br>
 * Only deploys one shared UI
 */
public class PConsumer {
    public static void main(String[] args) throws InterruptedException {
        GUI.setGUILook(new String[] { "GTK+", "Nimbus" });
        String topic = "Sensor";
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("key.deserializer","org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.DoubleDeserializer");
        props.put("group.id","0");//only 1 group for all receivers
        props.put("partition.assignment.strategy","org.apache.kafka.clients.consumer.RoundRobinAssignor"); //1 partition per consumer hopefully
        CustomKafkaConsumer[] receivers = new CustomKafkaConsumer[3];
        GUI gui = new GUI("Consumer Group 1");
        gui.start();
        List<KafkaRecordListener<Integer,Double>> listeners = new ArrayList<>();
        listeners.add(new AvgTempAggregate(gui,"Group Avg. Temp"));
        for(int i=0;i<3;i++){
            //share conditions object and UI
            receivers[i] = new CustomKafkaConsumer(topic,props,listeners,gui);
        }
        for(int i=0;i<3;i++)
            receivers[i].start();

        for(int i=0;i<3;i++)
            receivers[i].join();

    }
}
