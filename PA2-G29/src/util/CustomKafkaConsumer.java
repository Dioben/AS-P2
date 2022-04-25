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

    //TODO: CREATE A UI FOR PRODUCERS
    //private final ConsumerUI ui;
    private final String topicName;
    private final Consumer<String,Double> consumer;
    private final List<ConsumerDataCondition<String,Double>> conditions;

    /**
     * Instances a new producer thread
     * @param topic the topic to publish information into
     * @param properties kafka producer settings
     */
    public CustomKafkaConsumer(String topic, Properties properties, List<ConsumerDataCondition<String,Double>> conds){
        this.topicName = topic;
        consumer = new KafkaConsumer(properties);
        //TODO: Add Received UI here
        conditions = conds;
    }

    @Override
    public void run() {
        ConsumerRecords<String,Double> data;
        ListIterator<ConsumerDataCondition<String,Double>> conditionsIterator;
        ConsumerDataCondition<String,Double> condition;
        consumer.subscribe(Arrays.asList(topicName));
        while(true){
           data = consumer.poll(Duration.ofMillis(100));//using long as param is deprecated

            for (ConsumerRecord datapoint:data){ //for each record received
                //TODO: UPDATE UI WITH DATA
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
