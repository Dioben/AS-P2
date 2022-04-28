package util;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Interface used to inject condition checks and aggregate operations into the generic consumer class<br>
 * Method only allows one record at a time so further information must be preserved via internal state
 */
public interface KafkaRecordListener<T,K> {
    /**
     * Provide a new record to listener
     * @param record new record, considered as having been received after all previous ones
     * @return True if constraints are still met, false otherwise
     */
    boolean register(ConsumerRecord<T,K> record);
    String getName();
}
