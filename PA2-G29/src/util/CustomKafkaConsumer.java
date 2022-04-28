package util;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.*;

/**
 * Implementation of a Kafka Consumer<br>
 * Can be parallelized by the consumer process<br>
 * Reads data in (SENSOR TEMP ID) format indefinitely<br>
 * Receives a (possibly shared) UI on constructor
 */
public class CustomKafkaConsumer extends Thread{

    private final GUI gui;
    private final String topicName;
    private final Consumer<Integer,Double> consumer;
    private final List<KafkaRecordListener<Integer,Double>> listeners;

    /**
     * Instances a new producer thread
     * @param topic the topic to publish information into
     * @param properties kafka producer settings
     */
    public CustomKafkaConsumer(String topic, Properties properties, List<KafkaRecordListener<Integer,Double>> conds, GUI gui){
        this.topicName = topic;
        this.gui = gui;
        consumer = new KafkaConsumer(properties);
        listeners = conds;
    }

    @Override
    public void run() {
        ConsumerRecords<Integer,Double> data;
        ListIterator<KafkaRecordListener<Integer,Double>> listenerListIterator;
        KafkaRecordListener<Integer,Double> listener;
        consumer.subscribe(Arrays.asList(topicName));
        while(true){
           data = consumer.poll(Duration.ofMillis(100));//using long as param is deprecated
            for (ConsumerRecord datapoint:data){ //for each record received
                gui.addRecord((int) datapoint.key(), (double) datapoint.value(), datapoint.timestamp());
                listenerListIterator = listeners.listIterator();
                while (listenerListIterator.hasNext()){ //check all necessary conditions
                    listener = listenerListIterator.next();
                    if (!listener.register(datapoint)){
                        listenerListIterator.remove();//don't test a failed condition twice
                        if (RecordCondition.class.isInstance(listener))
                            gui.addCondition(listener.getName(), "Failed");
                        System.out.println("Failed condition "+listener.getName());
                    }
                }

            }
        }


    }

}
