package uc2;

import util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys 6 consumer threads that attempt to stick to different partitions
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

        CustomKafkaConsumer[] receivers = new CustomKafkaConsumer[6];

        for(int i=0;i<6;i++){
            //don't share conditions object
            List<KafkaRecordListener<Integer,Double>> conditions = new ArrayList<>();
            conditions.add(new OrderedDataCondition((previous,current)-> !(current.key().equals(previous.key()))?1:(int) (current.timestamp() - previous.timestamp()),"Same Sensor order by timestamp ascending"));
            conditions.add(new OrderedDataCondition((previous,current)-> (current.key().equals(previous.key())?0:-1 ),"Unique Sensor ID"));
            GUI gui = new GUI("Consumer " + (i+1));
            receivers[i] = new CustomKafkaConsumer(topic,props,conditions,gui);
            gui.start();
            for (KafkaRecordListener<Integer,Double> condition : conditions)
                gui.addCondition(condition.getName(), "Successful");
        }
        for(int i=0;i<6;i++)
            receivers[i].start();

        for(int i=0;i<6;i++)
            receivers[i].join();

    }
}
