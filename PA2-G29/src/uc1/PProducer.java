package uc1;


import util.CustomKafkaProducer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * TODO: DESCRIBE THIS PARTICULAR VERSION'S DETAILS HERE
 */
public class PProducer {
    public static void main(String[] args) {
        ServerSocket serverSocket;
        int port = 8000;
        String topic = "Sensor";
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.DoubleSerializer");
        //TODO: SET EXTRA PROPERTIES HERE
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                new CustomKafkaProducer(serverSocket.accept(),topic,props).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
