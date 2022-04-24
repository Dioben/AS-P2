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

    //TODO: CREATE A UI FOR PRODUCERS
    //private final ProducerUI ui;
    private final Socket comms;
    private final String topicName;
    private final Producer<String,Double> producer;
    private final Integer partition;

    /**
     * Instances a new producer thread
     * @param comms the socket that sensor information is coming in from
     * @param topic the topic to publish information into
     * @param properties kafka producer settings
     */
    public CustomKafkaProducer(Socket comms, String topic, Properties properties){
        this.comms = comms;
        this.topicName = topic;
        producer = new KafkaProducer(properties);
        partition = 0;
        //TODO: INSTANCE THE UI HERE
    }

    @Override
    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(comms.getInputStream()));
            String line;
            String[] msgSplit;
            ProducerRecord<String,Double> record;
            while(true){
                line = in.readLine();
                if (line.equals("END"))
                        break;
                msgSplit = line.split(" ");
                String sensId = msgSplit[0];
                double temp = Double.parseDouble(msgSplit[1]);
                long timestamp = Long.parseLong(msgSplit[2]);
                //TODO: UPDATE UI HERE
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
