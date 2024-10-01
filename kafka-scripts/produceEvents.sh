#bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
bin/kafka-console-producer.sh --topic $1  --bootstrap-server localhost:9092
