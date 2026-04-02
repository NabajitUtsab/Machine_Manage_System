# 🏭 Four H Group — Machine Management System

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

**A full-stack Machine Management System built for Four H Group, Bangladesh.**
Track, transfer, and manage industrial machines across multiple business concerns — with QR code scanning, role-based dashboards, and real-time notifications.

</div>

---

## 📌 What Is This Project?

Four H Group operates multiple subsidiary companies called **concerns**. Each concern owns and operates industrial machines. These machines are sometimes sent to other concerns temporarily, and tracking them manually was error-prone and time-consuming.

**This system solves that by:**
- Giving every machine a **unique QR code** that can be scanned from any phone
- Tracking exactly **where each machine is** at any point in time
- Recording a **complete transfer history** every time a machine moves between concerns
- Sending **real-time alerts** when machines are ready to transfer or waiting to be received

---

## 🎬 How The System Works — End to End

### Step 1 · Login & Role-Based Routing
A user visits the login page, enters credentials, and is automatically redirected to their role-specific dashboard. The backend issues a **JWT token** which the frontend stores in `localStorage` and attaches to every subsequent API call via `Authorization: Bearer <token>`.

```
Login → JWT issued → Role detected → Redirect
  SUPER_ADMIN  →  superadmin-dashboard.html
  ADMIN        →  admin-dashboard.html
  USER         →  user-dashboard.html
```

---

### Step 2 · Organization Setup (Super Admin)
The Super Admin creates **Concerns** (e.g. "Four H Textiles", "Four H Steel"). Then registers **Admin** users and assigns each to a concern. Admins in turn register **Users** (field operators) within their concern.

```
Super Admin creates Concerns
        ↓
Super Admin registers Admins → assigns to concerns
        ↓
Each Admin registers Users → auto-assigned to their own concern
```

---

### Step 3 · Machine Registration
When a machine is created, the system automatically:
- Saves it with a unique code and group name
- Records the **origin concern** — who bought it — this **never changes**
- Sets the **current concern** — where it physically is — this updates on every transfer
- Generates a **QR code PNG** (via ZXing) saved to `./qrcodes/{machineId}.png`

---

### Step 4 · The Transfer Workflow (Core Feature)

```
╔══════════════════════════════════════════════════════════╗
║               TRANSFER LIFECYCLE                         ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  Field User / Admin                                      ║
║    └─ Marks machine → READY_TO_TRANSFER                  ║
║                                                          ║
║  Admin / User initiates transfer                         ║
║    └─ Selects target concern                             ║
║    └─ Machine status → TRANSFERRED (locked)              ║
║    └─ Transfer record created: INITIATED                 ║
║                                                          ║
║  Destination concern Admin receives notification         ║
║    └─ Clicks "Receive" in notification panel             ║
║    └─ machine.currentConcern updated to destination      ║
║    └─ Machine status → ACTIVE                            ║
║    └─ Transfer status → COMPLETED                        ║
║                                                          ║
║  (Optional) Machine returned to sender                   ║
║    └─ machine.currentConcern reset to origin concern     ║
║    └─ Transfer status → RETURNED                         ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

---

### Step 5 · QR Code Scanning
Any user opens `scan.html`, points their phone camera at a machine's QR sticker, and instantly sees that machine's status, location, group, and origin — no manual searching needed.

The QR code contains only the machine's **numeric ID**. The frontend calls the public endpoint `/machines/{id}` to fetch the full details.

---

### Step 6 · Notifications & Live Alerts
Admin and Super Admin dashboards **auto-poll every 30 seconds** for:

| Alert Type | Meaning |
|------------|---------|
| ⚠️ Ready to Transfer | Machine is flagged and waiting to be sent |
| 📥 Incoming Transfers | A machine is in transit to your concern, awaiting receipt |
| 📤 Outgoing Transfers | Your concern sent a machine, still in transit |

Admins can click **"Receive"** directly from the notification card — no need to navigate elsewhere.

---

## 🏗️ System Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                        FRONTEND                              │
│              HTML5 · CSS3 · JavaScript                       │
│                                                              │
│  login.html     → authenticates, stores JWT in localStorage  │
│  api.js         → attaches Bearer token to every request     │
│  ui.js          → builds header, polls notification count    │
│  scan.js        → html5-qrcode camera-based QR scanner       │
│  22 HTML pages  → role-specific dashboards                   │
└──────────────────────┬───────────────────────────────────────┘
                       │  REST API over HTTP
                       │  Authorization: Bearer <JWT>
┌──────────────────────▼───────────────────────────────────────┐
│                   SPRING BOOT  BACKEND                      │
│                     localhost:8081                           │
│                                                              │
│  JwtAuthenticationFilter   validates token on every request  │
│  SecurityConfiguration     route-level role enforcement      │
│                                                              │
│  /auth/**          AuthController         (public + gated)   │
│  /superadmin/**    SuperAdminController   (SUPER_ADMIN)      │
│  /admin/**         AdminController        (ADMIN+)           │
│  /user/**          UserController         (USER+)            │
│  /transfer/**      TransferController     (USER+)            │
│  /machines/**      Scan + QR Controllers  (PUBLIC)           │
└──────────────────────┬───────────────────────────────────────┘
                       │  Spring Data JPA / Hibernate
┌──────────────────────▼───────────────────────────────────────┐
│                      PostgreSQL                              │
│                                                              │
│   concerns  │  users  │  machines  │  transfer              │
└──────────────────────────────────────────────────────────────┘
                       │
              ./qrcodes/{id}.png       ← QR files on disk
```

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Backend Framework | Spring Boot  · Java 21 | REST API, dependency injection, auto-configuration |
| Security | Spring Security + JWT (HS256) | Stateless authentication and role-based authorization |
| Database | PostgreSQL + Spring Data JPA | Relational data storage with repository abstraction |
| QR Generation | ZXing (Google) | Generates QR code PNG files server-side |
| QR Scanning | html5-qrcode | Camera-based scanning in the browser |
| Password Security | BCrypt | One-way hashing with automatic salting |
| Frontend | HTML5 · CSS3 ·  JavaScript | No build step — deployable as static files anywhere |

---

## 🗄️ Database Design

### Entity Relationships

```
Concern  1 ──────── ∞  User
Concern  1 ──────── ∞  Machines  (as originConcern)
Concern  1 ──────── ∞  Machines  (as currentConcern)
Machines 1 ──────── ∞  Transfer
Concern  1 ──────── ∞  Transfer  (as fromConcern)
Concern  1 ──────── ∞  Transfer  (as toConcern)
```

### Tables

**`concerns`** — The subsidiary companies  
**`users`** — System users with roles, linked to a concern  
**`machines`** — Every machine, with two concern references (origin + current)  
**`transfer`** — Every movement record between concerns, with timestamps

### Machine Status Lifecycle

```
   IDLE ──→ ACTIVE ──→ READY_TO_TRANSFER ──→ TRANSFERRED
    ↑                                              │
    │                    receiveTransfer()          │
    └──────────── ACTIVE ←──────────────────────────┘
                    │
                    │   returnMachine()
                    └──→ ACTIVE (back at origin concern)
```

### Transfer Status Lifecycle

```
  INITIATED ──→ COMPLETED ──→ RETURNED
```

---

## 🔐 Security Design

### How Authentication Works

```
1. POST /auth/login  →  credentials verified against BCrypt hash
2. JWT token issued  →  contains { username, role, expiry: 24h }
3. Client stores JWT in localStorage
4. Every request:   Authorization: Bearer <token>
5. JwtAuthenticationFilter intercepts, validates signature + expiry
6. Sets Spring SecurityContext → controllers see the authenticated user
7. Spring Security checks route-level hasRole() rules
8. Controller extracts user's concern from DB → scopes all queries
```

### Route Permission Matrix

| Route Pattern | SUPER_ADMIN | ADMIN | USER | Public |
|--------------|:-----------:|:-----:|:----:|:------:|
| `POST /auth/login` | ✅ | ✅ | ✅ | ✅ |
| `/superadmin/**` | ✅ | ❌ | ❌ | ❌ |
| `/admin/**` | ✅ | ✅ | ❌ | ❌ |
| `/user/**` | ✅ | ✅ | ✅ | ❌ |
| `/transfer/**` | ✅ | ✅ | ✅ | ❌ |
| `/machines/**` | ✅ | ✅ | ✅ | ✅ |

### Data Isolation
Admin controllers use Spring's `Authentication` object to get the logged-in user, then look up their concern. All queries are filtered by that concern's ID — **an admin from Concern A physically cannot access Concern B's data**, even with a valid token.

---

## 👥 The Three User Roles

### 🔴 Super Admin
Has full access to everything in the system. Creates the organizational structure (concerns), registers admins, creates machines, and has a bird's-eye view of all transfers and notifications across every concern.

### 🟡 Admin
Operates entirely within their assigned concern. Views machines from multiple angles (owned, in-house, received, sent out), manages incoming/outgoing transfers, and registers field users. The notification panel is their command center for pending actions.

### 🟢 User (Field Operator)
The simplest role, designed for warehouse/floor workers. Sees their concern's machines, can mark a machine as ready for transfer, and initiate the transfer to another concern. Most importantly — can scan any machine's QR code for instant lookup.

---

## 📡 Complete API Reference

### Authentication
```
POST /auth/login                     { username, password } → { jwt, role, concernId, concernName }
POST /auth/superadmin/register       { username, password, role, concernName }  [SUPER_ADMIN]
POST /auth/admin/register            { username, password, role: "USER" }       [ADMIN]
```

### Super Admin
```
GET    /superadmin/concerns                          → all concerns
POST   /superadmin/concerns?name=                    → create concern
PUT    /superadmin/concerns/{id}?name=               → rename concern
DELETE /superadmin/concerns/{id}                     → delete concern

GET    /superadmin/machines                          → all machines
POST   /superadmin/machines?code=&groupName=&concernName=&status=  → create + QR
PUT    /superadmin/machines/{id}?groupName=&status=&concernName=   → update
DELETE /superadmin/machines/{id}                     → delete + remove QR file

GET    /superadmin/users                             → all users
DELETE /superadmin/users/{id}                        → delete user

GET    /superadmin/transfers                         → all transfers
DELETE /superadmin/transfers/{id}                    → delete transfer

GET    /superadmin/notifications → { readyToTransfer[], initiatedTransfers[], completedTransfers[] }
```

### Admin *(all scoped to own concern automatically)*
```
GET    /admin/machines                → machines where currentConcern = admin's concern
GET    /admin/machines/owned          → machines where originConcern = admin's concern
GET    /admin/machines/inhouse        → machines present (excludes TRANSFERRED status)
GET    /admin/machines/received       → COMPLETED/RETURNED transfers received from others
GET    /admin/machines/transferred-out → all outgoing transfers

PUT    /admin/machines/{id}?groupName=&status=       → update (ownership checked)
DELETE /admin/machines/{id}                          → delete (ownership checked)

GET    /admin/users                  → users in own concern
GET    /admin/notifications → { readyToTransfer[], incomingTransfers[], outgoingTransfers[] }
```

### User Actions
```
PUT  /user/machines/{id}/ready                        → mark READY_TO_TRANSFER
POST /user/machines/{id}/transfer?toConcernName=      → auto-ready then initiate transfer
```

### Transfer Operations
```
POST /transfer/initiate?machineId=&toConcernName=     → create transfer (machine must be READY)
POST /transfer/receive?transferId=                    → confirm receipt, update currentConcern
POST /transfer/return?transferId=                     → return machine to fromConcern
```

### Public (No Auth Required)
```
GET /machines/{id}                   → machine details (used after QR scan)
GET /machines/qr/{machineId}         → QR code as image/png
GET /machines/qr/base64/{machineId}  → QR code as { machineId, base64 }
```

---

## 🚀 Running Locally

### Prerequisites
- Java 21+
- Maven 3.8+
- MySql 

### 1 · Clone & Configure

```bash
git clone https://github.com/NabajitUtsab/Machine_Manage_System.git
cd Machine_Manage_System/Machine_Management_System_Backend
```

Create the database:
```sql
CREATE DATABASE machine_db;
```

Edit `src/main/resources/application.properties`:
```properties
server.port=8081

spring.datasource.url=jdbc:mysql://localhost:3306/machine_db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD


spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### 2 · Start the Backend

```bash
mvn spring-boot:run
```

✅ API running at `http://localhost:8081`  
✅ Database tables auto-created by Hibernate  
✅ Default Super Admin seeded automatically

### 3 · Start the Frontend

```bash
cd frontend/

# VS Code: install Live Server extension → click "Go Live"
# or Python:
python -m http.server 5500
```

✅ Open `http://localhost:5500/login.html`

### 4 · First Login

| Username | Password | Role |
|----------|----------|------|
| `superadmin` | `admin123` | SUPER_ADMIN |

> ⚠️ Change this password after first login!

---

## 💡 Key Design Decisions

**Stateless JWT over Sessions**
Sessions require server memory and don't scale. JWT tokens carry all needed identity info — the server just validates the signature. No shared state between requests.

**`/machines/**` is Public**
QR scanning is a primary feature for warehouse workers. Requiring login before seeing a scanned machine's details creates friction. The data returned is read-only and non-sensitive.

**QR codes stored as files, not generated on-the-fly**
Pre-generating QR PNGs at machine creation time means every subsequent QR request is just a file read — instant and zero CPU cost. Clean deletion when a machine is removed.

**Two concern references on Machine (`originConcern` + `currentConcern`)**
`originConcern` answers "who owns this machine?" and never changes. `currentConcern` answers "where is it right now?" and updates on every transfer. This separation makes ownership tracking trivial.

**Vanilla JS — no framework**
The frontend deploys as plain HTML files with zero build tooling. This works in any environment — local file system, any HTTP server, or served from Spring Boot's static resources folder.

---

## 🔮 Future Improvements

- [ ] Implement change-password endpoint in the backend
- [ ] WebSocket-based live notifications instead of polling
- [ ] Machine maintenance scheduling and history log
- [ ] PDF/Excel report export
- [ ] Docker Compose for one-command deployment
- [ ] Unit tests (JUnit 5) and integration tests (MockMvc)
- [ ] Pagination for large machine/transfer lists

---

## 📂 Project Structure Summary

```
backend/
├── configuration/    → Security, JWT filter, data seeder
├── controller/       → 7 REST controllers
├── service/          → Business logic (4 services)
├── entity/           → 4 JPA entities
├── enumerations/     → Role, MachineStatus, TransferStatus
├── dto/              → 4 request DTOs
├── repositories/     → 4 JPA repositories
└── utility/          → JwtUtil, QRCodeGenerator

frontend/
├── css/style.css     → Complete design system (Navy + Gold theme)
├── js/               → api.js, auth.js, ui.js, scan.js
└── *.html            → 22 role-specific pages
```

---

## 👨‍💻 Author

**[NABAJIT DEY]**  
Backend Developer  
📧 noboutsab@gmail.com  
🔗 [LinkedIn](https://www.linkedin.com/in/nabajit-dey-utsab-1b12431b5/) · [https://github.com/NabajitUtsab)

---

<div align="center">
  <sub>Built with ❤️ for Four H Group Bangladesh &nbsp;·&nbsp; © 2025 All Rights Reserved</sub>
</div>
