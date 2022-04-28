EXEC_PATH="${PATH_TO_KAFKA:-$HOME/kafka_2.13-3.1.0/}"
cd "$EXEC_PATH" || exit
echo "Deleting Sensor topic"
timeout 3 ./bin/kafka-topics.sh --delete --topic Sensor --if-exists --bootstrap-server localhost:9092
echo "Shutting down Kafka"
./bin/kafka-server-stop.sh
sleep 7
echo "Shutting down Zookeeper"
./bin/zookeeper-server-stop.sh
echo "Deleting Kafka logs"
rm -r /tmp/kafka-logs
echo "Shutdown complete"