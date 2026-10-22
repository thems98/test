#!/usr/bin/env python3

import signal
import threading

from confluent_kafka import Consumer, KafkaError, KafkaException

conf = {
    "bootstrap.servers": "localhost:9092",
    "group.id": "api-logger",
    "auto.offset.reset": "latest",
    "enable.auto.commit": True,
}

consumer = Consumer(conf)
stop_event = threading.Event()


def consume(consumer, topics):
    try:
        consumer.subscribe(topics)
        print(f"Listening for Kafka messages on: {topics}")

        while not stop_event.is_set():
            msg = consumer.poll(timeout=1.0)

            if msg is None:
                continue

            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    continue

                raise KafkaException(msg.error())

            key = msg.key()
            value = msg.value()

            # Decode messages without assuming a particular format.
            key = key.decode("utf-8", errors="replace") if key else None
            value = value.decode("utf-8", errors="replace") if value else ""

            print(
                f"Topic: {msg.topic()} | "
                f"Partition: {msg.partition()} | "
                f"Offset: {msg.offset()} | "
                f"Key: {key} | "
                f"Message: {value}"
            )

    except KeyboardInterrupt:
        print("\nStopping consumer...")
    finally:
        consumer.close()
        print("Consumer closed.")


def request_stop(signum, frame):
    stop_event.set()


if __name__ == "__main__":
    signal.signal(signal.SIGINT, request_stop)
    signal.signal(signal.SIGTERM, request_stop)

    # If all three APIs use the same topic, use ["bankAPI"] instead.
    consume(consumer, ["bank-requests", "person-requests", "rest-requests", "transctions"])
