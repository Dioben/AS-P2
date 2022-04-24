EXEC_PATH="${PATH_TO_KAFKA:-$HOME/kafka_2.13-3.1.0/}"
cd "$EXEC_PATH"
nohup  ./bin/zookeeper-server-start.sh config/zookeeper.properties &
echo "Instancing Zookeeper"
nohup ./bin/kafka-server-start.sh config/server.properties &
echo "Instancing Kafka"
sleep 10
./bin/kafka-topics.sh --create --topic Sensor --bootstrap-server localhost:9092
echo "Setup complete"