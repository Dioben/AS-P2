package uc1;


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
        props.put("acks","1");//default
        props.put("max.in.flight.requests.per.connection","5");//default
        props.put("linger.ms","0");//default
        props.put("compression.type","none");//default
        //TODO: SET EXTRA PROPERTIES HERE
        try {
            serverSocket = new ServerSocket(port);
            int i = 1;
            while (true) {
                GUI gui = new GUI("Producer " + i++);
                new CustomKafkaProducer(serverSocket.accept(),topic,props,gui).start();
                gui.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
