package uc5;

import util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys 9 consumer threads in 3 groups
 */
public class PConsumer {
    public static void main(String[] args) throws InterruptedException {
        GUI.setGUILook(new String[] { "GTK+", "Nimbus" });
        String topic = "Sensor";
        CustomKafkaConsumer[] receivers = new CustomKafkaConsumer[9];

        //present in all groups
        GUI gui = new GUI("Global stats display");
        RecordAggregate trueMinAggregate = new MinTempAggregate(gui,"Global Min. Temp");
        RecordAggregate trueMaxAggregate = new MaxTempAggregate(gui,"Global Max. Temp");
        gui.start();

        for (int group = 0;group<3;group++){
            Properties props = new Properties();
            props.put("bootstrap.servers","localhost:9092");
            props.put("key.deserializer","org.apache.kafka.common.serialization.IntegerDeserializer");
            props.put("value.deserializer","org.apache.kafka.common.serialization.DoubleDeserializer");
            props.put("group.id",String.valueOf(group));//3 consumer groups
            props.put("partition.assignment.strategy","org.apache.kafka.clients.consumer.RoundRobinAssignor"); //indifferent
            GUI groupUI = new GUI("Consumer Group" + (group+1));
            List<KafkaRecordListener<Integer,Double>> listeners = new ArrayList<>();
            listeners.add(trueMaxAggregate);
            listeners.add(trueMinAggregate);
            listeners.add(new MinTempAggregate(groupUI,"Min. Temp")); //groupwise aggregates
            listeners.add(new MaxTempAggregate(groupUI,"Max. Temp"));
            groupUI.start();
            for(int i=0;i<3;i++){
                receivers[i] = new CustomKafkaConsumer(topic,props,listeners,groupUI);
            }
        }

        for(int i=0;i<9;i++)
            receivers[i].start();

        for(int i=0;i<9;i++)
            receivers[i].join();

    }
}
