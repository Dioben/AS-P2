package uc3;

import util.ConsumerDataCondition;
import util.CustomKafkaConsumer;
import util.GUI;
import util.OrderedDataCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys 3 consumer threads in a group that attempt to stick to different partitions
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
        //TODO: MISSING PROPERTIES

        int threads = 3;
        CustomKafkaConsumer[] receivers = new CustomKafkaConsumer[threads];

        for(int i=0;i<threads;i++){
            //don't share conditions object
            List<ConsumerDataCondition<Integer,Double>> conditions = new ArrayList<>();
           //TODO: ADD A DUPLICATE CHECK CONDITION IF WE FEEL LIKE IT
            GUI gui = new GUI("Consumer " + (i+1));
            receivers[i] = new CustomKafkaConsumer(topic,props,conditions,gui);
            gui.start();
        }
        for(int i=0;i<threads;i++)
            receivers[i].start();

        for(int i=0;i<threads;i++)
            receivers[i].join();

    }
}
