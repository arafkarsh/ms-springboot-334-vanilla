#bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic $1 --bootstrap-server localhost:9092
