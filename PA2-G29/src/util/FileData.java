package util;

/**
 * Simple read-only data container object to ease storage in PSource implementations
 */
public class FileData {
    private final long timestamp;
    private final String sID;
    private final double temp;

    public FileData(long ts, String id, double temp){
        timestamp = ts;
        sID = id;
        this.temp = temp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getsID() {
        return sID;
    }

    public double getTemp() {
        return temp;
    }
}
