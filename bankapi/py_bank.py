#!/usr/bin/env python
import json
from flask import Flask, jsonify, request
import time
from datetime import datetime, timezone
from flask import Flask, jsonify, request, g
from confluent_kafka import KafkaException, Producer
from confluent_kafka.admin import AdminClient, NewTopic
import socket

conf = {
    'bootstrap.servers': 'localhost:9092',
    'client.id': socket.gethostname()
}

producer = Producer(conf)
app = Flask(__name__)

KAFKA_TOPIC = 'bank-requests'

admin = AdminClient(conf)

def create_kafka_topic():
    topic_metadata = admin.list_topics(timeout=5)

    if KAFKA_TOPIC in topic_metadata.topics:
        print(f"Kafka topic '{KAFKA_TOPIC}' already exists.")
        return

    new_topic = NewTopic(
        topic=KAFKA_TOPIC,
        num_partitions=1,
        replication_factor=1
    )

    futures = admin.create_topics([new_topic])

    for topic, future in futures.items():
        try:
            future.result()
            print(f"Created Kafka topic '{topic}'.")
        except KafkaException as e:
            print(f"Failed to create Kafka topic '{topic}': {e}")


def acked(err, msg):
    if err is not None:
        print(f'Failed to deliver log: {err}')
    else:
        print(f'Logged request to {msg.topic()} [{msg.partition()}]')


@app.before_request
def start_request_timer():
    g.request_start = time.perf_counter()


@app.after_request
def log_request(response):
    event = {
        'timestamp': datetime.now(timezone.utc).isoformat(),
        'method': request.method,
        'path': request.path,
        'status_code': response.status_code,
        'duration_ms': round(
            (time.perf_counter() - g.request_start) * 1000, 2
        )
    }

    try:
        producer.produce(
            KAFKA_TOPIC,
            key=request.path,
            value=json.dumps(event),
            callback=acked
        )
        producer.poll(0)
    except BufferError:
        # The producer queue is full; record the failure locally.
        print('Kafka producer queue is full; request log was not queued.')

    return response

# all_banks=['SWEDBANK','IKANOBANKEN','JPMORGAN','NORDEA','CITIBANK','HANDELSBANKEN','SBAB','HSBC','NORDNET']

@app.route('/bank/list', methods=['GET'])
def List_all_banks() -> json:   
    with open('banks.json') as banks:
        banks = json.load(banks)
    
    return banks

@app.route('/bank/find.name', methods=['GET'])
def find_bank_by_name() -> json:
    name = request.args.get('name')
    with open('banks.json') as banks:
        banks = dict(json.load(banks))
        if name in banks.values():
            key = ""
            for k in banks.keys():
                if banks[k] == name:
                    key = k
            return jsonify({"key": key, "name": name})        
    return jsonify(None)

# @app.route('/bank/find.key', methods=['GET'])
# def find_bank_by_key()-> str:
#     key = request.args.get('key')
#     with open('accounts.json') as banks:
#         banks = dict(json.load(banks))
#         if key in banks.keys():
#             return jsonify({"key": key, "name":banks[key]})
        
#     return jsonify(None)

@app.route('/bank/find.key', methods=['GET'])
def find_bank_by_key() -> json:
    key = request.args.get('key')
    with open('banks.json') as f:
        banks = json.load(f)
    if key in banks:
        return jsonify({"key": key, "name": banks[key]})
    return jsonify(None)

if __name__ == '__main__':
    create_kafka_topic()
    app.run(debug=True, port=8070)
# print(List_all_banks)