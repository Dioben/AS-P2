EXEC_PATH="${PATH_TO_KAFKA:-$HOME/kafka_2.13-3.1.0/}"
cd "$EXEC_PATH" || exit
echo "Deleting Sensor topic"
./bin/kafka-topics.sh --delete --topic Sensor --bootstrap-server localhost:9092
echo "Shutting down Kafka"
./bin/kafka-server-stop.sh
sleep 7
echo "Shutting down Zookeeper"
./bin/zookeeper-server-stop.sh
echo "Shutdown complete"
