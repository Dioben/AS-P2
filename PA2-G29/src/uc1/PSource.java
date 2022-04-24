package uc1;

import util.FileData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PSource {
    public static void main(String[] args) {

        List<FileData> data = new ArrayList<>();
        InputStream file;
        Scanner scanner;

        file = PSource.class.getClassLoader().getResourceAsStream("Sensor.txt");
        scanner = new Scanner(file);

        while (scanner.hasNextLine()){

            String id = scanner.nextLine();
            double temp = Double.parseDouble(scanner.nextLine()); //nextDouble doesn't like the input somehow
            int ts = scanner.nextInt();
            scanner.nextLine();//ff empty line

            data.add(new FileData(ts,id,temp));
        }


        System.out.println("Got "+ data.size() +" records");

    }

}
