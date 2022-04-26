package util;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.locks.Condition;

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
    private final List<ConsumerDataCondition<Integer,Double>> conditions;

    /**
     * Instances a new producer thread
     * @param topic the topic to publish information into
     * @param properties kafka producer settings
     */
    public CustomKafkaConsumer(String topic, Properties properties, List<ConsumerDataCondition<Integer,Double>> conds, GUI gui){
        this.topicName = topic;
        this.gui = gui;
        consumer = new KafkaConsumer(properties);
        conditions = conds;
    }

    @Override
    public void run() {
        ConsumerRecords<Integer,Double> data;
        ListIterator<ConsumerDataCondition<Integer,Double>> conditionsIterator;
        ConsumerDataCondition<Integer,Double> condition;
        consumer.subscribe(Arrays.asList(topicName));
        while(true){
           data = consumer.poll(Duration.ofMillis(100));//using long as param is deprecated
            for (ConsumerRecord datapoint:data){ //for each record received
                gui.addRecord((int) datapoint.key(), (double) datapoint.value(), datapoint.timestamp());
                conditionsIterator = conditions.listIterator();
                while (conditionsIterator.hasNext()){ //check all necessary conditions
                    condition = conditionsIterator.next();
                    if (!condition.conditionOk(datapoint)){
                        //TODO: ADD A UI UPDATE METHOD HERE(OPTIONAL)
                        conditionsIterator.remove();//don't test a failed condition twice
                        System.out.println("Failed condition "+condition.getName());
                    }
                }

            }
        }


    }

}
