package uc3;

import util.FileData;
import util.SensorSender;

import java.util.List;

import static util.SensorParser.readFile;

/**
 * A sensor file reader implementation<br>
 * Deploys 3 senders that send 3 content chunks to 3 producers<br>
 */
public class PSource {
    public static void main(String[] args) throws InterruptedException {

        List<FileData> data = readFile("Sensor.txt");
        SensorSender.sendDataUniformSplit(data, 3,"localhost",8000);


    }


}
