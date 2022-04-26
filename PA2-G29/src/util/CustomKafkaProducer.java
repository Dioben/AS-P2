package util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;

/**
 * Implementation of a Kafka Producer<br>
 * Branches off of a server socket<br>
 * Reads data in (SENSOR TEMP ID) format until it receives an "END" message<br>
 * Deploys its own UI on constructor
 */
public class CustomKafkaProducer extends Thread{

    private final GUI gui;
    private final Socket comms;
    private final String topicName;
    private final Producer<Integer,Double> producer;
    private final Integer partition;

    /**
     * Instances a new producer thread<br>
     * This producer will post to partition 0
     * @param comms the socket that sensor information is coming in from
     * @param topic the topic to publish information into
     * @param properties kafka producer settings
     */
    public CustomKafkaProducer(Socket comms, String topic, Properties properties, int id){
        this.comms = comms;
        this.topicName = topic;
        producer = new KafkaProducer(properties);
        partition = 0;
        gui = new GUI("Producer " + id);
        gui.start();
    }

    /**
     * Instances a new producer thread
     * @param comms the socket that sensor information is coming in from
     * @param topic the topic to publish information into
     * @param properties kafka producer settings
     * @param partition partition to post data into
     */
    public CustomKafkaProducer(Socket comms, String topic, Properties properties, int id, int partition){
        this.comms = comms;
        this.topicName = topic;
        producer = new KafkaProducer(properties);
        this.partition = partition;
        gui = new GUI("Producer " + id);
        gui.start();
    }

    @Override
    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(comms.getInputStream()));
            String line;
            String[] msgSplit;
            ProducerRecord<Integer,Double> record;
            while(true){
                line = in.readLine();
                if (line.equals("END"))
                        break;
                msgSplit = line.split(" ");
                int sensId = Integer.parseInt(msgSplit[0]);
                double temp = Double.parseDouble(msgSplit[1]);
                long timestamp = Long.parseLong(msgSplit[2]);
                gui.addRecord(sensId, temp, timestamp);
                record = new ProducerRecord(topicName,partition,timestamp,sensId,temp);
                producer.send(record);
            }
            //TODO: SHOULD THE UI SHUT DOWN HERE TOO?
            in.close();
            comms.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
