package uc2;

import util.FileData;
import util.ProducerSender;
import java.util.List;
import static util.SensorParser.readFile;

/**
 * A sensor file reader implementation<br>
 * Deploys 6 senders that send 6 content chunks to 6 producers<br>
 * Sorts data by ID first
 */
public class PSource {
    public static void main(String[] args) throws InterruptedException {

        List<FileData> data = readFile("Sensor.txt");
        //sort by ID and then TS
        data.sort((o1, o2) -> {
            int value = o1.getsID()-o2.getsID();
            if (value==0)
                value = (int) (o1.getTimestamp()-o2.getTimestamp());
            return value;
        });


        ProducerSender[] senders = new ProducerSender[6];

        for(int i=0;i<6;i++){
            int startIdx = (int) ((i/6.0)*data.size());
            int stopIdx = (int) (((i+1)/6.0)*data.size());
            senders[i] = new ProducerSender(data,startIdx,stopIdx,"localhost",8000);
            senders[i].start();
        }

        for(int i=0;i<6;i++)
            senders[i].join();


    }


}
