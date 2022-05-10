EXEC_PATH="${PATH_TO_KAFKA:-$HOME/kafka_2.13-3.1.0/}"
cd "$EXEC_PATH" || exit
echo "Shutting down Kafka"
./bin/kafka-server-stop.sh
sleep 7
echo "Shutting down Zookeeper"
./bin/zookeeper-server-stop.sh
echo "Deleting Zookeeper and Kafka logs" # also removes topics
rm -r /tmp/zookeeper /tmp/kafka-logs /tmp/kafka-logs-1 /tmp/kafka-logs-2 /tmp/kafka-logs-3
echo "Shutdown complete"