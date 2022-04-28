package uc1;

import util.FileData;
import util.ProducerSender;
import java.util.List;

import static util.SensorParser.readFile;

/**
 * A sensor file reader implementation
 * Deploys a single sender that sends the file's entire contents to a single producer
 */
public class PSource {
    public static void main(String[] args) throws InterruptedException {

        List<FileData> data = readFile("Sensor.txt");

        ProducerSender sender = new ProducerSender(data,0,data.size(),"localhost",8000);
        sender.run();
        sender.join();

    }


}
