## 🍽️ TCKT — Distributed Kitchen Ticket Queue (k3s Demo)

TCKT (*ticket*) is a lightweight Spring Boot + PostgreSQL web application designed to showcase **distributed application behavior** inside a Kubernetes (k3s) cluster.

Each web request is served by one of many application pods running across the cluster. When a kitchen order is created, claimed, or completed, the action is recorded in a shared PostgreSQL backend. The UI displays:

- 🖥️ Which **node** and **pod** served the request  
- 📊 Real-time **stats per node** and **stats per pod**  
- 🧾 A live **order queue** for a fictional restaurant kitchen  
- 🔁 Auto-refresh every few seconds so you can visually watch distribution happen

Perfect for:
- Cluster demos ⭐  
- Load balancing demos 🔀  
- Postgres-backed app scaling 🏗️  
- Hands-on Spring Boot + Docker + k3s deployments 🐳

---

### 🚀 Features

| Feature | Description |
|--------|-------------|
| Distributed processing | Multiple pods handle orders independently with optimistic locking |
| Shared persistence | Orders stored in a central Postgres DB with transaction management |
| Node + pod awareness | Request metadata injected via Kubernetes Downward API |
| Modern UI | Bootstrap 5 with Bootswatch Flatly theme, responsive design |
| UI auto-refresh | Order status + stats update live every 3 seconds |
| Realistic workflow | Create → Claim → Complete kitchen tickets |
| API Documentation | Interactive Swagger UI for all REST endpoints |
| CORS Support | Cross-origin requests enabled for API endpoints |
| Profile-based config | Separate configurations for test (H2) and production (PostgreSQL) |

---

### 🧱 Tech Stack

| Layer | Technology |
|------|------------|
| Backend | **Java 17**, **Spring Boot 3** |
| Database | **PostgreSQL 16** |
| Frontend | **Thymeleaf** + Vanilla JS |
| Container | Docker |
| Orchestration | **k3s / Kubernetes** |

---

### 📁 Project Layout
```
src/
├── main/java/com/kitchen/tckt
│ ├── TcktApplication.java # Spring Boot entrypoint
│ ├── config/ # Configuration (CORS, OpenAPI, etc.)
│ ├── model/ # JPA entities with @Version for optimistic locking
│ ├── repo/ # Spring Data JPA repository
│ └── web/ # Controllers (REST API + Thymeleaf)
└── resources/
    ├── templates/index.html # Bootstrap 5 UI with Thymeleaf
    ├── application.properties # PostgreSQL (default)
    └── application-test.properties # H2 in-memory (for tests)
```

### 🌐 Access Points

- **Main UI**: http://localhost:8088
- **Swagger UI**: http://localhost:8088/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8088/api-docs
- **H2 Console** (test profile): http://localhost:8088/h2-console

### 🏃 Running Locally

**With H2 (no PostgreSQL required):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

**With PostgreSQL (default):**
```bash
# Ensure PostgreSQL is running with tcktdb database
mvn spring-boot:run
```

📦 Build & Containerize

Build JAR:

```
mvn clean package -DskipTests
```

Build Docker image:
```
docker build -t YOUR_REGISTRY/tckt:latest .
docker push YOUR_REGISTRY/tckt:latest
```

☸️ Deploy to k3s / Kubernetes

Apply included manifests:

```kubectl apply -f tckt.yaml```


Check rollout:
```
kubectl -n tckt get pods -o wide
kubectl -n tckt get svc
```

Open the LoadBalancer external IP in a browser to see pod/node labels rotate as you refresh! 🎯

### 🌐 REST API Endpoints

All endpoints documented in **Swagger UI** at http://localhost:8088/swagger-ui.html

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/orders/meta` | Shows which pod/node served the request |
| GET | `/api/orders` | List all orders (sorted by newest first) |
| GET | `/api/orders/stats` | Aggregated stats by pod, node, and status |
| POST | `/api/orders` | Create a new order `{"item": "Pizza"}` |
| POST | `/api/orders/{id}/claim` | Mark order as In Progress |
| POST | `/api/orders/{id}/done` | Mark order as Done |

**Features:**
- All state-changing operations use `@Transactional`
- Optimistic locking with `@Version` prevents concurrent modification conflicts
- CORS enabled for cross-origin requests
- Full OpenAPI 3.0 documentation

🔧 Configurable via Environment Variables
```
Variable	                    Purpose
SPRING_DATASOURCE_URL	        JDBC URL for Postgres
SPRING_DATASOURCE_USERNAME	  Database user
SPRING_DATASOURCE_PASSWORD	  Database password
POD_NAME	                    Injected via Kubernetes Downward API
NODE_NAME	                    Injected via Kubernetes Downward API
```

📸 Screenshots
Coming soon.

### ⭐ Why This Demo Rocks

- **Visual proof of traffic distribution** with pod/node tracking
- **Production-ready patterns**: optimistic locking, transactions, CORS, Swagger
- **Modern responsive UI** with Bootstrap 5 and Bootswatch Flatly theme
- **Profile-based configuration** for different environments
- **Stateful backend** demonstrating shared DB usage across pods
- **Minimal code** — easy to understand and extend
- **Interactive API docs** with Swagger UI

**Scale up live:**
```bash
kubectl -n tckt scale deploy tckt --replicas=5
```
Then refresh the UI → pods will take turns serving traffic and claiming tickets! 👨‍🍳

### 🎨 UI Features

- Bootstrap 5 with Bootswatch Flatly theme
- Responsive design (works on mobile, tablet, desktop)
- Server-side rendering with Thymeleaf
- Auto-refresh every 3 seconds
- Color-coded status badges
- Error handling with dismissible alerts
- Bootstrap Icons throughout

👨‍🍳 Author

Built with ❤️ by Craig Derington.
