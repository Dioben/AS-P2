package util;

import uc2.PSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SensorParser {
    public static List<FileData> readFile(String fileName){
        List<FileData> data = new ArrayList<>();
        InputStream file;
        Scanner scanner;

        file = PSource.class.getClassLoader().getResourceAsStream(fileName);
        scanner = new Scanner(file);

        while (scanner.hasNextLine()){

            int id = Integer.parseInt(scanner.nextLine());
            double temp = Double.parseDouble(scanner.nextLine()); //nextDouble doesn't like the input somehow
            long ts = scanner.nextLong(); //nextLong is fine though
            scanner.nextLine();//ff empty line

            data.add(new FileData(ts,id,temp));
        }
        return data;
    }
}
