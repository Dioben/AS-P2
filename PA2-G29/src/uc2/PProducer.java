package uc2;


import util.CustomKafkaProducer;
import util.GUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys a single producer instance with "default" settings
 */
public class PProducer {
    public static void main(String[] args) {
        GUI.setGUILook(new String[] { "GTK+", "Nimbus" });
        ServerSocket serverSocket;
        int port = 8000;
        String topic = "Sensor";
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092");
        props.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.DoubleSerializer");
        //TODO: SET EXTRA PROPERTIES HERE
        try {
            serverSocket = new ServerSocket(port);
            int i = 1;
            while (true) {
                new CustomKafkaProducer(serverSocket.accept(),topic,props,new GUI("Producer " + i++)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
