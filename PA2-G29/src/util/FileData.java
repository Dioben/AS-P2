package util;

public class FileData {
    private final int timestamp;
    private final String sID;
    private final double temp;

    public FileData(int ts, String id, double temp){
        timestamp = ts;
        sID = id;
        this.temp = temp;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getsID() {
        return sID;
    }

    public double getTemp() {
        return temp;
    }
}
