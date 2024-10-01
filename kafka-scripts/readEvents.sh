#bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
bin/kafka-console-consumer.sh --topic $1 --from-beginning --bootstrap-server localhost:9092
