package uc2;


import util.CustomKafkaProducer;
import util.GUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys 6 producer instances with a focus on minimizing latency and ensuring we don't lose all records relative to a given sensor ID<br>
 * Each producer writers to a different partition - this is not good for latency but it helps enforce data ordering
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
        props.put("acks",0); //disable ACKs to lower latency
        props.put("max.in.flight.requests.per.connection",1); //force 1 concurrent request at most for strict order
        props.put("linger.ms",0);//default, but useful
        props.put("compression.type","none");//default,but useful
        //TODO: SET EXTRA PROPERTIES HERE
        try {
            serverSocket = new ServerSocket(port);
            int i = 1;
            while (true) {
                new CustomKafkaProducer(serverSocket.accept(),topic,props,new GUI("Producer " + i),i-1).start();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
