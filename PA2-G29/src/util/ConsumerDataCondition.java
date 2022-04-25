package util;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Interface used to inject condition checks into the generic consumer class<br>
 * Provides a check method to verify whether records match the given condition<br>
 * Method only allows one record at a time so further information must be preserved via internal state
 * If condition is <b>strictly mandatory</b>the check method may throw instead
 */
public interface ConsumerDataCondition<T,K> {
    /**
     * Provide a new record and see whether constraints are still met
     * If condition is <b>strictly mandatory</b> may throw instead
     * @param record new record, considered as having been received after all previous ones
     * @return True if constraints are still met, false otherwise
     */
    boolean conditionOk(ConsumerRecord<T,K> record);
    String getName();
}
