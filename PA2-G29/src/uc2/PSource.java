package uc2;

import util.FileData;
import util.ProducerSender;
import java.util.List;
import static util.SensorParser.readFile;

/**
 * A sensor file reader implementation
 * Deploys a single sender that sends the file's entire contents to a single producer<br>
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


        ProducerSender sender = new ProducerSender(data,0,data.size(),"localhost",8000);
        sender.run();
        sender.join();

    }


}
