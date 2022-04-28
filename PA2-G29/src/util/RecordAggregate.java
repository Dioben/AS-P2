package util;

/**
 * Interface used to run aggregate operations from the generic consumer class<br>
 * Method only allows one record at a time so further information must be preserved via internal state
 * Return false to indicate removal request
 */
public interface RecordAggregate<T,K> extends KafkaRecordListener<T,K>{
}
