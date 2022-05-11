package uc3;


import util.CustomKafkaProducer;
import util.GUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

//https://docs.confluent.io/cloud/current/client-apps/optimizing/throughput.html#optimizing-for-throughput
/**
 * An implementation of Kafka consumer ensemble<br>
 * Deploys 3 producer instances with a focus on maximizing throughput<br>
 * Each producer writes to a different partition and supports a large amount of concurrent requests, uses ACKs to reduce data loss
 */
public class PProducer {
    public static void main(String[] args) {
        GUI.setGUILook(new String[] { "GTK+", "Nimbus" });
        ServerSocket serverSocket;
        int port = 8000;
        String topic = "Sensor";
        Properties props = new Properties();
        props.put("bootstrap.servers","localhost:9092,localhost:9093");
        props.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.DoubleSerializer");
        props.put("acks","1"); //default
        props.put("max.in.flight.requests.per.connection","50"); //Strong concurrency but not good for ordering
        props.put("linger.ms","100");//tends towards larger batches but higher latency
        props.put("compression.type","lz4");//smaller data volume
        props.put("batch.size","150000");//larger total data per packet
        props.put("buffer.memory","33554432");//default, but with more partitions we're supposed to increase this
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
