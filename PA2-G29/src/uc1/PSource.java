package uc1;

import util.FileData;
import util.ProducerSender;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A sensor file reader implementation
 * Deploys a single sender that sends the file's entire contents to a single producer
 */
public class PSource {
    public static void main(String[] args) throws InterruptedException {

        List<FileData> data = new ArrayList<>();
        InputStream file;
        Scanner scanner;

        file = PSource.class.getClassLoader().getResourceAsStream("Sensor.txt");
        scanner = new Scanner(file);

        while (scanner.hasNextLine()){

            int id = Integer.parseInt(scanner.nextLine());
            double temp = Double.parseDouble(scanner.nextLine()); //nextDouble doesn't like the input somehow
            long ts = scanner.nextLong(); //nextLong is fine though
            scanner.nextLine();//ff empty line

            data.add(new FileData(ts,id,temp));
        }

        ProducerSender sender = new ProducerSender(data,0,data.size(),"localhost",8000);
        sender.run();
        sender.join();

    }


}
