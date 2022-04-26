package util;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Comparator;

public class OrderedDataCondition implements ConsumerDataCondition<Integer,Double>{
    ConsumerRecord<Integer,Double> previous = null;
    Comparator<ConsumerRecord<Integer,Double>> condition;
    private String name;

    /**
     * Instance an ordered data condition
     * @param condition Compares previous record to current one, returns 0 if equal, less than 0 if wrong order, more than 0 if good order
     * @param name This Condition's name
     */
    public OrderedDataCondition(Comparator<ConsumerRecord<Integer,Double>> condition, String name){
        this.condition = condition;
        this.name = name;
    }

    @Override
    public boolean conditionOk(ConsumerRecord<Integer,Double> record) {
        ConsumerRecord<Integer,Double> hold = previous;
        previous = record;
        if (hold==null)
            return true;
        if ( condition.compare(hold,previous)<0)
            return false;
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}
