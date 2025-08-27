# Vehicle & Operation Management API

This project is a Spring Boot application for managing vehicles, operations, and suggestions. It exposes RESTful APIs for CRUD operations, searching, and recommendations.

---

## Base URL

```
http://localhost:8080/
```

---

## Notes

- All endpoints requiring a request body accept JSON.
- Validation annotations ensure proper input; errors will return `400 Bad Request`.
- Suggestions automatically convert distances to kilometers internally.

---

## Running

1. Clone the repository
```bash
git clone https://github.com/ryanwibowo/vehicle.git 
```
2. Check Java version
   Make sure you have the required Java version installed (e.g., Java 17):
```bash
java -version
```
3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```
   or generate JAR
```bash
java -jar target/your-app-name-0.0.1-SNAPSHOT.jar
```
4. Access API via `http://localhost:8080/`
---
5. H2 console at `/h2-console` (JDBC URL: `jdbc:h2:mem:vehicledb`).

## Testing

Unit and integration tests are included. Use Maven to run:

```bash
./mvnw test
```

## Endpoints

### **1. Operations**

#### Create Operation
```
POST /operations
```
**Request Body**
```json
{
  "brand": "Toyota",
  "model": "Alphard",
  "engine": "2.5L",
  "yearStart": 2024,
  "yearEnd": 2025,
  "name": "Service Alphard",
  "description": "Oil change and filter replacement",
  "distance": 15000,
  "operationDate": "2025-08-25",
  "time": "60",
  "distanceStart": 12000,
  "distanceEnd": 15000,
  "approxCost": 250.0
}
```
**cURL**
```bash
curl --location 'http://localhost:8080/operations' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{
    "brand": "Toyota",
    "model": "Alphard",
    "engine": "2.5L",
    "yearStart": -2024,
    "yearEnd": 2025,
    "name": "Service Alphard",
    "description": "Oil change and filter replacement",
    "distance": 15000,
    "operationDate": "2025-08-25",
    "time": "-1",
    "distanceStart": 12000,
    "distanceEnd": 15000,
    "approxCost": 250.0
  }'
```

#### Update Operation
```
PUT /operations/{id}
```
**cURL**
```bash
curl --location --request PUT 'http://localhost:8080/operations/51' \
--header 'Content-Type: application/json' \
--data '{
    "brand": "Toyota",
    "model": "Alphard",
    "engine": "2.5L",
    "yearStart": 2018,
    "yearEnd": 2019,
    "name": "Service Alphard",
    "description": "Replace timing belt and inspect pulleys",
    "distance": 15000,
    "operationDate": "2025-08-25",
    "time": "09",
    "distanceStart": 12000,
    "distanceEnd": 15000,
    "approxCost": 250.0
  }'
```

#### Search Operations
```
GET /operations/search
```
**Query Parameters**
- brand
- model
- engine
- yearStart
- yearEnd
- distanceStart
- distanceEnd
- page
- size
- sort

**cURL**
```bash
curl --location 'http://localhost:8080/operations/search?brand=Toyota&model=Alphard&engine=2.5L&yearStart=2017&yearEnd=2020&distanceStart=10000&distanceEnd=50000&page=0&size=20&sort=id%2Cdesc' \
--header 'Accept: application/json'
```

---

### **2. Suggestion**

```
POST /suggestions
```
**Request Body**
```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "engine": "2.0 Turbo",
  "makeYear": 2020,
  "totalDistance": 30000,
  "unit": "km"
}
```

**cURL**
```bash
curl --location 'http://localhost:8080/suggestions' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{
    "brand": "Toyota",
    "model": "Corolla",
    "engine": "2.0 Turbo",
    "makeYear": 2020,
    "totalDistance": 30000
  }'
```

---

### **3. Vehicle**

#### Create Vehicle
```
POST /vehicles
```
**cURL**
```bash
curl --location 'http://localhost:8080/vehicles' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--data '{
    "brand": "Toyota",
    "model": "Corolla",
    "engine": "1.8L",
    "makeYear": 2020
  }'
```

#### List Vehicles
```
GET /vehicles
```
**cURL**
```bash
curl --location 'http://localhost:8080/vehicles?page=0&size=20&sort=id%2Cdesc'
```

---

## Pagination

All list endpoints support Spring `Pageable` parameters:

- `page` – zero-based page index
- `size` – page size
- `sort` – e.g. `sort=brand,asc`

---
