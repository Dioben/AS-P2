package util;

/**
 * Simple read-only data container object to ease storage in PSource implementations
 */
public class FileData {
    private final long timestamp;
    private final int sID;
    private final double temp;

    public FileData(long ts, int id, double temp){
        timestamp = ts;
        sID = id;
        this.temp = temp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getsID() {
        return sID;
    }

    public double getTemp() {
        return temp;
    }
}
