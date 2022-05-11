package uc4;


import util.CustomKafkaProducer;
import util.GUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys 6 producer instances with a focus on ordering and avoiding data loss<br>
 * Each producer writes to a different partition, ordering is intra partition rather than global
 */
public class PProducer {
    public static void main(String[] args) {
        GUI.setGUILook(new String[] { "GTK+", "Nimbus" });
        ServerSocket serverSocket;
        int port = 8000;
        String topic = "Sensor";
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092,localhost:9093,localhost:9094");
        props.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.DoubleSerializer");
        props.put("acks","all"); //maximum ACKs to ensure linearity
        props.put("max.in.flight.requests.per.connection","1"); //force 1 concurrent request at most for strict order
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
