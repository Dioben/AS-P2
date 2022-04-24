package uc1;


import util.CustomKafkaProducer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

public class PProducer {
    public static void main(String[] args) {
        ServerSocket serverSocket;
        int port = 8000;
        String topic = "Sensor";
        Properties props = new Properties();
        //TODO: SET PROPERTIES HERE
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
