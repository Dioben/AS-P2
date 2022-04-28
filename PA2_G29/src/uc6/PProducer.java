package uc6;


import util.CustomKafkaProducer;
import util.GUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys 6 producer instances with default settings<br>
 * Each producer writers to a different partition
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
        props.put("acks","1"); //default
        props.put("max.in.flight.requests.per.connection","5"); //default
        props.put("linger.ms","0");//default
        props.put("compression.type","none");//default
        props.put("batch.size","16384");//default
        try {
            serverSocket = new ServerSocket(port);
            int i = 0;
            while (true) {
                GUI gui = new GUI("Producer " + (i+1));
                new CustomKafkaProducer(serverSocket.accept(),topic,props,gui,i).start();
                gui.start();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
