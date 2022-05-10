EXEC_PATH="${PATH_TO_KAFKA:-$HOME/kafka_2.13-3.1.0/}"
cd "$EXEC_PATH" || exit
nohup  ./bin/zookeeper-server-start.sh config/zookeeper.properties &
echo "Instancing Zookeeper"
sleep 0.1
nohup ./bin/kafka-server-start.sh config/server.properties --override broker.id=1 --override port=9092 --override listeners=PLAINTEXT://localhost:9092 --override log.dirs=/tmp/kafka-logs-1 &
nohup ./bin/kafka-server-start.sh config/server.properties --override broker.id=2 --override port=9093 --override listeners=PLAINTEXT://localhost:9093 --override log.dirs=/tmp/kafka-logs-2 &
echo "Instancing Kafka"
sleep 10
./bin/kafka-topics.sh --create --topic Sensor --partitions 3 --replication-factor 2 --bootstrap-server localhost:9092,localhost:9093
echo "Setup complete"