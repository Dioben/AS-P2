package util;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Implementation of Producer Server client<br>
 * Contacts a given server and dumps all information in (SENSOR TEMP ID) format followed by an "END" message
 */
public class ProducerSender extends Thread{
    private  Socket comms;
    private final List<FileData> data;
    private final int startIdx;
    private final int  stopIdx;
    private final String  host;
    private final int port;

    /**
     * Instances a sender client thread
     * @param data The full list of required data, pre-sorted
     * @param startIdx When to start sending data
     * @param stopIdx When to stop sending data
     * @param host Host server name
     * @param port Host server port
     */
        public ProducerSender(List<FileData> data, int startIdx, int stopIdx, String host, int port){
        this.data = data;
        this.startIdx = startIdx;
        this.stopIdx = stopIdx;
        this.host = host;
        this.port = port;
        }

        public void run(){
            PrintWriter out;
            FileData sent;
            try {
                comms = new Socket(host, port);
                out = new PrintWriter(comms.getOutputStream(), true);
                for (int i = startIdx;i<stopIdx;i++){
                    sent = data.get(i);
                    out.println(sent.getsID() + " " + sent.getTemp() + " " +sent.getTimestamp());
                }
                out.println("END");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

}
