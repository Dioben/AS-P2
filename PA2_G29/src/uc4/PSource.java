package uc4;

import util.FileData;
import util.SensorSender;

import java.util.List;

import static util.SensorParser.readFile;

/**
 * A sensor file reader implementation<br>
 * Deploys 6 senders that send 6 content chunks to 6 producers<br>
 * Identical to the UC3 version
 */
public class PSource {
    public static void main(String[] args) throws InterruptedException {

        List<FileData> data = readFile("Sensor.txt");
        SensorSender.sendDataUniformSplit(data, 6,"localhost",8000);


    }


}
