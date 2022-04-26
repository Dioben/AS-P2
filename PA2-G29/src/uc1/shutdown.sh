EXEC_PATH="${PATH_TO_KAFKA:-$HOME/kafka_2.13-3.1.0/}"
cd "$EXEC_PATH"
echo "Shutting down Kafka"
./bin/kafka-server-stop.sh config/server.properties
sleep 7
echo "Shutting down Zookeeper"
./bin/zookeeper-server-stop.sh
echo "Shutdown complete"
