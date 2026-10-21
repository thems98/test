#!/usr/bin/env python
from kafka import KafkaConsumer
import json

# Kafka Consumer
consumer = KafkaConsumer(
    'driver-location',
    bootstrap_servers=['localhost:9092'],
    auto_offset_reset='earliest',  # Start from the earliest message
    enable_auto_commit=True,
    group_id='location-group',
    value_deserializer=lambda x: json.loads(x.decode('utf-8'))  # Deserialize data from JSON
)

def process_location_updates():
    print("Waiting for location updates...")
    for message in consumer:
        location = message.value
        driver_id = location['driver_id']
        latitude = location['latitude']
        longitude = location['longitude']
        timestamp = location['timestamp']
        print(f"Received location update for Driver {driver_id}: ({latitude}, {longitude}) at {timestamp}")

# Start consuming location updates
if __name__ == "__main__":
    process_location_updates()