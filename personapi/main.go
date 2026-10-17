package main

// import "github.com/gin-gonic/gin"

// import (
//   "net/http"

//   "github.com/gin-gonic/gin"
// )
import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
)

// func read_json_file(file_path string) map[string]any {
// 	content, err := os.ReadFile(file_path)
// 	if err != nil {
// 		log.Fatal("Error when opening file: ", err)
// 	}

// 	var payload map[string]any
// 	err = json.Unmarshal(content, &payload)
// 	if err != nil {
// 		log.Fatal("Error during Unmarshal(): ", err)
// 	}
// 	return payload
// }

func convert_map_to_json(data map[string]any) string {
	jsonData, err := json.Marshal(data)
	if err != nil {
		log.Fatal("Error during Marshal(): ", err)
	}
	return string(jsonData)
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

func find_person_by_name(name string) string {
	var data = list_all_persons("./people.json")
	result := make(map[string]any)
	for key, value := range data {
		if value == name {
			result[key] = value
		}
	}
	var people = convert_map_to_json(result)
	return people
}

func find_person_by_key(key string) string {
	var data = list_all_persons("./people.json")
	result := make(map[string]any)
	for k, v := range data {
		if k == key {
			result[k] = v
		}
	}
	var people = convert_map_to_json(result)
	fmt.Println(people)
	return people
}

// func main() {
// 	// Create a Gin router with default middleware (logger and recovery)
// 	r := gin.Default()

// 	// // Define a simple GET endpoint
// 	r.GET("/ping", func(c *gin.Context) {
// 	// 	// Return JSON response
// 		c.JSON(http.StatusOK, gin.H{
// 			"message": "pong",
// 		})
// 	})

// 	// Start server on port 8060 (default)
// 	// Server will listen on 0.0.0.0:8060 (localhost:8060 on Windows)
// 	r.Run()
// 	// list_all_persons()
// 	// find_person_by_name("Xena")
// 	// find_person_by_key("3Wytrt5638HUTyPWert")
// }
// go mod init gin-api
// go get -u ://github.com

func main() {
	// Initiera Gins standard-router
	r := gin.Default()

	// API-endpoint: Hämta alla personer
	// Testa:  curl -i http://localhost:8060/person/list
	r.GET("/person/list", func(c *gin.Context) {
		data := list_all_persons("./people.json")
		// if data == nil {
		// 	c.JSON(http.StatusInternalServerError, gin.H{"error": "Could not read data"})
		// 	return
		// }
		c.JSON(http.StatusOK, data)
	})

	// API-endpoint: Sök på nyckel via URL-parameter
	// Testa:  curl -i 'http://localhost:8060/person/find.key?key=<key>'
	r.GET("/person/find.key/:key", func(c *gin.Context) {
		key := c.Param("key")
		result := find_person_by_key(key)

		if len(result) == 0 {
			c.JSON(http.StatusNotFound, gin.H{"message": "Person not found"})
			return
		}
		c.JSON(http.StatusOK, result)
	})

	// API-endpoint: Sök på namn via Query-parameter
	// Testa: curl 'http://localhost:8060/person/find.name?name=<name>'
	r.GET("/person/find.name/:name", func(c *gin.Context) {
		name := c.Query("name")
		if name == "" {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Query parameter 'name' is required"})
			return
		}

		result := find_person_by_name(name)
		c.JSON(http.StatusOK, result)
	})

	// Starta servern på port 8060
	fmt.Println("Server running on http://localhost:8060")
	r.Run(":8060")
}
