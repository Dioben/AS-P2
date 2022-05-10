EXEC_PATH="${PATH_TO_KAFKA:-$HOME/kafka_2.13-3.1.0/}"
cd "$EXEC_PATH" || exit
nohup  ./bin/zookeeper-server-start.sh config/zookeeper.properties &
echo "Instancing Zookeeper"
sleep 0.1
nohup ./bin/kafka-server-start.sh config/server.properties &
echo "Instancing Kafka"
sleep 10
./bin/kafka-topics.sh --create --topic Sensor --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092
echo "Setup complete"