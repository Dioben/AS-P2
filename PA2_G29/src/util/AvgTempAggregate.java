package util;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.locks.ReentrantLock;


public class AvgTempAggregate implements RecordAggregate<Integer,Double>{
    private final String name;
    private double total = 0;
    private double count = 0;
    private final GUI ui;
    private final ReentrantLock lock;

    public AvgTempAggregate(GUI gui, String name){
        this.name  = name;
        this.ui = gui;
        lock = new ReentrantLock();
    }
    @Override
    public boolean register(ConsumerRecord<Integer, Double> record) {
        lock.lock();
        total+=record.value();
        count++;
        ui.addExtraInfo(name,String.format("%.2f",total/count)+ "ºC");
        lock.unlock();
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}
