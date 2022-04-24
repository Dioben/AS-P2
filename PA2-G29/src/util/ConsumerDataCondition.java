package util;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Interface used to inject condition checks into the generic consumer class<br>
 * Provides a check method to verify whether records match the given condition<br>
 * Method only allows one record at a time so further information must be preserved via internal state
 */
public interface ConsumerDataCondition {
    /**
     * Provide a new record and see whether constraints are still met
     * @param record new record, considered as having been received after all previous ones
     * @return True if constraints are still met, false otherwise
     */
    boolean check(ConsumerRecord record);
}
