package main

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/segmentio/kafka-go"
)

// Global Kafka Writer instance
var kafkaWriter *kafka.Writer

// Config constants
const (
	kafkaBroker = "localhost:9092"
	kafkaTopic  = "person-requests"
)

func initKafka() {
	// Initialize the Kafka writer with a connection pool and auto-retries
	kafkaWriter = &kafka.Writer{
		Addr:                   kafka.TCP(kafkaBroker),
		Topic:                  kafkaTopic,
		Balancer:               &kafka.Hash{},
		AllowAutoTopicCreation: true,
	}
}

// Background worker that consumes messages from Kafka
func startKafkaConsumer() {
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers:  []string{kafkaBroker},
		Topic:    kafkaTopic,
		GroupID:  "person-service-group", // Using a group ID auto-commits offsets
		MaxBytes: 10e6,                   // 10MB
	})
	defer reader.Close()

	log.Println("Kafka background consumer started...")

	for {
		// ReadMessage blocks until a new message is available
		m, err := reader.ReadMessage(context.Background())
		if err != nil {
			log.Printf("Consumer error: %v\n", err)
			break
		}

		// In a real application, you would log to a database, send alerts, or process telemetry.
		fmt.Printf(" [Kafka Worker] Logged Action: %s at offset %d\n", string(m.Value), m.Offset)
	}
}

func list_all_persons(file_path string) map[string]any {
	content, err := os.ReadFile(file_path)
	if err != nil {
		log.Fatal("Error when opening file: ", err)
	}

	var payload map[string]any
	err = json.Unmarshal(content, &payload)
	if err != nil {
		log.Fatal("Error during Unmarshal(): ", err)
	}
	return payload
}

func find_person_by_key(key string) map[string]any {
	data := list_all_persons("./people.json")
	result := make(map[string]any)
	if v, ok := data[key]; ok {
		result[key] = v
	}
	return result
}

func find_person_by_name(name string) []map[string]string {
	data := list_all_persons("./people.json")
	matches := []map[string]string{}
	for k, v := range data {
		if v == name {
			matches = append(matches, map[string]string{"key": k, "name": name})
		}
	}
	return matches
}

func main() {
	// 1. Initialize Kafka configuration
	initKafka()
	defer kafkaWriter.Close()

	// 2. Spin up the Kafka consumer as an asynchronous background routine
	go startKafkaConsumer()

	r := gin.Default()

	r.GET("/person/list", func(c *gin.Context) {
		c.JSON(http.StatusOK, list_all_persons("./people.json"))
	})

	r.GET("/person/find.key", func(c *gin.Context) {
		key := c.Query("key")
		result := find_person_by_key(key)

		if len(result) == 0 {
			c.JSON(http.StatusOK, nil)
			return
		}

		// Asynchronously push a tracking event to Kafka so the HTTP client doesn't block
		go func(k string) {
			ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
			defer cancel()

			eventMsg := fmt.Sprintf("Person look up initiated for key: %s", k)
			err := kafkaWriter.WriteMessages(ctx, kafka.Message{
				Key:   []byte(k),
				Value: []byte(eventMsg),
			})
			if err != nil {
				log.Printf("Failed to publish event to Kafka: %v\n", err)
			}
		}(key)

		c.JSON(http.StatusOK, result)
	})

	r.GET("/person/find.name", func(c *gin.Context) {
		name := c.Query("name")
		if name == "" {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Query parameter 'name' is required"})
			return
		}
		c.JSON(http.StatusOK, find_person_by_name(name))
	})

	fmt.Println("Server running on http://localhost:8060")
	r.Run(":8060")
}
