package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
)

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

// Returns a proper map so Gin encodes it as a JSON object, not a string.
func find_person_by_key(key string) map[string]any {
	data := list_all_persons("./people.json")
	result := make(map[string]any)
	if v, ok := data[key]; ok {
		result[key] = v
	}
	return result
}

// Spec: "Find persons by name (JSON list)" → return an array of {key, name}.
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
	r := gin.Default()

	r.GET("/person/list", func(c *gin.Context) {
		c.JSON(http.StatusOK, list_all_persons("./people.json"))
	})

	r.GET("/person/find.key", func(c *gin.Context) {
		result := find_person_by_key(c.Query("key"))
		if len(result) == 0 {
			// Spec: "Find a person by key (JSON or null)" → null, not 404.
			c.JSON(http.StatusOK, nil)
			return
		}
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
