# AS-P2
Repository for a project built around exploring Apache Kafka

## How to run

### **First time setup**
Add these executables to the project root directory and add them to the project:
- kafka-clients-3.1.0.jar
- kafka-log4j-appender-3.1.0.jar
- log4j-1.2.17.jar
- slf4j-api-1.7.30.jar
- slf4j-log4j12-1.7.30.jar
- lz4-java-1.8.0.jar

Move the **kafka_2.13-3.1.0** folder to the home directory or export EXEC_PATH to the folder in another location (2nd option is untested).

### **Running the UCs**

Run **shutdown.sh**.

Run **setup.sh**.

Run PConsumer, PProducer and PSource, but don't start PSource before PProducer.
