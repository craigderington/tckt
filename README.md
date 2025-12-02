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
| Distributed processing | Multiple pods handle orders independently |
| Shared persistence | Orders stored in a central Postgres DB |
| Node + pod awareness | Request metadata injected via Kubernetes Downward API |
| UI auto-refresh | Order status + stats update live |
| Realistic workflow | Create → Claim → Complete kitchen tickets |

---

### 🧱 Tech Stack

| Layer | Technology |
|------|------------|
| Backend | **Java 17**, **Spring Boot 3** |
| Database | **PostgreSQL 16** |
| Frontend | Static HTML + Vanilla JS |
| Container | Docker |
| Orchestration | **k3s / Kubernetes** |

---

### 📁 Project Layout
```
src/
├── main/java/com/kitchen/tckt
│ ├── TcktApplication.java # Spring Boot entrypoint
│ ├── model/ # JPA entities & enums
│ ├── repo/ # Spring Data JPA repository
│ └── web/ # REST API controllers
└── resources/static/index.html # UI (no frameworks required)
```

👉 NAVIGATE: http://localhost:8088

⚠️ Local mode defaults to Postgres — update application.properties to use local DB or H2 if testing standalone.

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

🌐 REST API Endpoints
```
Method	Endpoint	                       Description
GET	    /api/orders/meta	               Shows which pod/node served the request
GET	    /api/orders                        List all orders
POST    /api/orders                        Create a new order ({"item": "Pizza"})
POST    /api/orders/{id}/claim             Mark order as In Progress
POST    /api/orders/{id}/done	           Mark order as Done
GET	    /api/orders/stats                  Aggregated stats by pod & node
```

The UI consumes all of these automatically.

🔧 Configurable via Environment Variables
```
Variable	                     Purpose
SPRING_DATASOURCE_URL	         JDBC URL for Postgres
SPRING_DATASOURCE_USERNAME       Database user
SPRING_DATASOURCE_PASSWORD       Database password
POD_NAME	                     Injected via Kubernetes Downward API
NODE_NAME	                     Injected via Kubernetes Downward API
```

📸 Screenshots
Coming soon.

⭐ Why This Demo Rocks

- Visual proof of traffic distribution

- Stateful backend demonstrating shared DB usage

- Minimal code — easy to understand and extend

- Pure Spring Boot + PostgreSQL — no magic, no fluff

- Scale up live:

```kubectl -n tckt scale deploy tckt --replicas=5```

Then refresh the UI → pods will take turns serving traffic and claiming tickets 👨‍🍳

🛠 Future Ideas

Metrics per order status per node

Requeue abandoned orders

Grafana dashboard integration

WebSockets for push updates

👨‍🍳 Author

Built with ❤️ by Craig Derington.
