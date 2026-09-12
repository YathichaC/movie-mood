# MovieMood

ระบบจัดการและแนะนำภาพยนตร์ตามอารมณ์และความชอบของผู้ใช้

## รายละเอียดโปรเจกต์

MovieMood เป็น Web Application สำหรับจัดการข้อมูลภาพยนตร์และแนะนำภาพยนตร์ให้เหมาะกับอารมณ์และความชอบของผู้ใช้ โดยผู้ใช้สามารถเลือกอารมณ์ ค้นหาและดูรายละเอียดภาพยนตร์ ให้คะแนนภาพยนตร์ และดูประวัติการรับชมได้

ระบบจะนำข้อมูลเกี่ยวกับ Mood, Genre และความชอบของผู้ใช้มาใช้ในการแนะนำภาพยนตร์ที่เหมาะสม

## สมาชิกกลุ่ม

| ลำดับ | ชื่อ - นามสกุล | รหัสนักศึกษา | Sec | Email | Branch | หน้าที่รับผิดชอบ |
|---:|---|---|---|---|---|---|
| 1 | กนกพร บุญครอง | 673380024-0  | SEC 1 | kanokporn.bo@kkumail.com | kanokporn_6733800240_01 | **User Management & Authentication** — พัฒนา User Entity, User Profile, Registration/Login และจัดการข้อมูล Preference ของผู้ใช้ |
| 2 | ชิดชนก ชนะพา | 673380033-9| SEC 1 | chidchanok.cha@kkumail.com | chidchanok_6733800339_01 | **Movie & Mood Management** — พัฒนา Movie, Genre, Mood Entity รวมถึง CRUD, Search, Filter และจัดการความสัมพันธ์ระหว่าง Movie กับ Mood |
| 3 | ญาทิชา จันทรศรีสุริยวงศ์ | 673380034-7 | SEC 2 | yathicha.c@kkumail.com | yathicha_6733800347_02 | **Recommendation Engine & Design Patterns** — พัฒนา Recommendation Logic, Match Score และประยุกต์ใช้ Strategy Pattern สำหรับการคำนวณ Recommendation |
| 4 | อรปรีญา แซ่โซ้ง | 673380070-3 | SEC 1 | onpriya122549@gmail.com | onpriya_6733800703_01 | **Rating, Watch History & Frontend Integration** — พัฒนา Rating API, Watch History, Dashboard และเชื่อมต่อ Frontend กับ REST API |

---

##  Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven

### Frontend
- HTML
- CSS
- JavaScript
- Thymeleaf

### Database
- Supabase (PostgreSQL)

### Testing
- JUnit
- Mockito

### Version Control
- Git
- GitHub

---

##  System Architecture

MovieMood ใช้แนวคิด **Layered Architecture** โดยแบ่งระบบออกเป็นแต่ละ Layer เพื่อแยกหน้าที่และลดการเชื่อมโยงระหว่างส่วนต่าง ๆ ของระบบ

```text
┌─────────────────────────────┐
│          Frontend           │
│      HTML / CSS / JS        │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│        Controller Layer     │
│      REST API / Routing     │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│         Service Layer       │
│        Business Logic       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│       Repository Layer      │
│       Spring Data JPA       │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│    Supabase (PostgreSQL)    │
│          Database           │

└─────────────────────────────┘
```

---

##  โครงสร้างโปรเจกต์ (Project Structure)

```text
MovieMood/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.example.moviemood/
│   │   │       ├── controller/        # REST Controllers สำหรับจัดการ HTTP Request และ Routing
│   │   │       ├── service/           # Business Logic ของระบบ
│   │   │       ├── repository/        # Interface เชื่อมต่อกับ Database (Spring Data JPA)
│   │   │       ├── entity/            # Database Models / Entities
│   │   │       ├── dto/               # Data Transfer Objects
│   │   │       ├── strategy/          # Strategy Pattern สำหรับอัลกอริทึมการแนะนำ
│   │   │       └── factory/           # Factory Pattern สำหรับการสร้าง Object
│   │   │
│   │   └── resources/
│   │       ├── static/                # Static assets (CSS, JS, Images)
│   │       │   ├── css/
│   │       │   └── js/
│   │       ├── templates/             # HTML Templates (Thymeleaf/UI)
│   │       │   ├── index.html         # หน้าแรกของเว็บไซต์
│   │       │   ├── login.html         # หน้าเข้าสู่ระบบ
│   │       │   ├── movies.html        # หน้าแสดงรายการภาพยนตร์ทั้งหมด
│   │       │   ├── movie-detail.html  # หน้ารายละเอียดภาพยนตร์
│   │       │   ├── mood.html          # หน้าเลือกอารมณ์/ความรู้สึก
│   │       │   ├── recommendation.html# หน้าแสดงผลการแนะนำภาพยนตร์
│   │       │   ├── rating.html        # หน้าให้คะแนนภาพยนตร์
│   │       │   ├── history.html       # หน้าประวัติการใช้งาน
│   │       │   └── profile.html       # หน้าโปรไฟล์ผู้ใช้
│   │       └── application.properties # ไฟล์การตั้งค่า Spring Boot และ Database
│   │
│   └── test/                          # Unit Tests และ Integration Tests
│
├── README.md                          # เอกสารอธิบายโปรเจกต์
├── pom.xml                            # ไฟล์จัดการ Dependencies ของ Maven
└── .gitignore                         # รายชื่อไฟล์ที่ยกเว้นการ Upload บน Git
```

---

## ⚙️ Installation & Setup

### Requirements
- Java 17 หรือสูงกว่า
- Maven
- Git
- GitHub Account
- Supabase Account

### 1. Clone Repository
```bash
git clone <repository-url>
cd MovieMood
```

### 2. Setup Supabase
1. สร้าง Project บน Supabase
2. สร้าง Database Tables ตาม ER Diagram
3. เตรียมข้อมูล Database Connection ของ Supabase
4. ตั้งค่าการเชื่อมต่อ Database ใน `application.properties`

### 3. Configure Database
เปิดไฟล์ `src/main/resources/application.properties` และกำหนดค่า:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **คำเตือน:** ไม่ควรใส่ Password จริงในไฟล์หรือ Commit ข้อมูลสำคัญขึ้น GitHub

---

## ▶️ How to Run

### สำหรับ Windows / macOS / Linux
เปิด Terminal ในโฟลเดอร์ของโปรเจกต์ แล้วใช้คำสั่งสำหรับ **Windows**:

```cmd
mvnw.cmd spring-boot:run
```

หรือหากติดตั้ง Maven ไว้ในเครื่องแล้ว สามารถใช้คำสั่ง:

```bash
mvn spring-boot:run
```

เมื่อ Application ทำงานสำเร็จ สามารถเข้าใช้งานผ่าน Web Browser ได้ที่:
--> **[http://localhost:8080](http://localhost:8080)**

### Stop Application
กด `Ctrl + C` ใน Terminal เพื่อหยุดการทำงานของ Application

---

## 📚 API Documentation

### User API
- `POST   /api/users/register`
- `POST   /api/users/login`
- `GET    /api/users/{id}`
- `PUT    /api/users/{id}`

### Movie API
- `GET    /api/movies`
- `GET    /api/movies/{id}`
- `POST   /api/movies`
- `PUT    /api/movies/{id}`
- `DELETE /api/movies/{id}`

### Mood API
- `GET    /api/moods`
- `POST   /api/moods`

### Recommendation API
- `GET    /api/recommendations/{userId}`
- `POST   /api/recommendations`

### Rating API
- `POST   /api/ratings`
- `GET    /api/ratings/movie/{movieId}`

### Watch History API
- `POST   /api/history`
- `GET    /api/history/user/{userId}`

### Swagger / OpenAPI
สามารถดูและทดสอบ REST API ผ่าน Swagger UI ได้ที่:
--> **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

---

## 🧪 How to Run Tests

โปรเจกต์ใช้ **JUnit** และ **Mockito** สำหรับทดสอบการทำงานของระบบ

```bash
mvn test
```

สำหรับ Windows:
```cmd
mvnw.cmd test
```

**Unit Tests หลักที่ครอบคลุม:**
* `UserServiceTest`
* `MovieServiceTest`
* `RecommendationServiceTest`
* `RatingServiceTest`

---

## 🌐 Deployment URL

* **Production URL:** `<deployment-url>` *(จะถูกเพิ่มหลังจากนำระบบขึ้น Deployment Server เรียบร้อยแล้ว)*