package util;

/**
 * Interface used to inject condition checks into the generic consumer class<br>
 * The record method verifies whether records match the given condition<br>
 * Method only allows one record at a time so further information must be preserved via internal state
 * Return false to indicate some form of error
 * If condition is <b>strictly mandatory</b>the check method may throw instead
 */
public interface RecordCondition<T,K> extends KafkaRecordListener<T,K>{
}
