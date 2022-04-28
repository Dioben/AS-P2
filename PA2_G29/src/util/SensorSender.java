package util;

import java.util.List;

public class SensorSender {
    /**
     * Instances a number of threads that send data to a server, with a uniform split between threads
     * @param data Total data to be sent
     * @param threads Number of threads
     * @param host Server host name
     * @param port Server port
     * @throws InterruptedException function blocks until senders are done, can be interrupted
     */
    public static void sendDataUniformSplit(List<FileData> data, int threads, String host, int port) throws InterruptedException {
        ProducerSender[] senders = new ProducerSender[threads];

        for(int i=0;i<threads;i++){
            int startIdx = i* data.size() / threads;
            int stopIdx = (i+1)* data.size() / threads;
            senders[i] = new ProducerSender(data,startIdx,stopIdx,host,port);
            senders[i].start();
        }

        for(int i=0;i<threads;i++)
            senders[i].join();
    }
}
