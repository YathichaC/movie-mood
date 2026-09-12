# MovieMood

ระบบจัดการและแนะนำภาพยนตร์ตามอารมณ์และความชอบของผู้ใช้

## รายละเอียดโปรเจกต์

MovieMood เป็น Web Application สำหรับจัดการข้อมูลภาพยนตร์และแนะนำภาพยนตร์ให้เหมาะกับอารมณ์และความชอบของผู้ใช้ โดยผู้ใช้สามารถเลือกอารมณ์ ค้นหาและดูรายละเอียดภาพยนตร์ ให้คะแนนภาพยนตร์ และดูประวัติการรับชมได้

ระบบจะนำข้อมูลเกี่ยวกับ Mood, Genre และความชอบของผู้ใช้มาใช้ในการแนะนำภาพยนตร์ที่เหมาะสม

## สมาชิกกลุ่ม

| ลำดับ | ชื่อ - นามสกุล | รหัสนักศึกษา | Sec | Email | Branch | หน้าที่รับผิดชอบ |
|---:|---|---|---|---|---|---|
| 1 | กนกพร บุญครอง | 673380024-0  | SEC 1 | kanokporn.bo@kkumail.com | `feature/user-management` | **User Management & Authentication** — พัฒนา User Entity, User Profile, Registration/Login และจัดการข้อมูล Preference ของผู้ใช้ |
| 2 | ชิดชนก ชนะพา | 673380033-9| SEC 1 | chidchanok.cha@kkumail.com | `feature/movie-management` | **Movie & Mood Management** — พัฒนา Movie, Genre, Mood Entity รวมถึง CRUD, Search, Filter และจัดการความสัมพันธ์ระหว่าง Movie กับ Mood |
| 3 | ญาทิชา จันทรศรีสุริยวงศ์ | 673380034-7 | SEC 2 | yathicha.c@kkumail.com | `feature/recommendation` | **Recommendation Engine & Design Patterns** — พัฒนา Recommendation Logic, Match Score และประยุกต์ใช้ Strategy Pattern สำหรับการคำนวณ Recommendation |
| 4 | อรปรีญา แซ่โซ้ง | 673380070-3 | SEC 1 | onpriya122549@gmail.com | `feature/rating-history` | **Rating, Watch History & Frontend Integration** — พัฒนา Rating API, Watch History, Dashboard และเชื่อมต่อ Frontend กับ REST API |

---

## 🛠️ Tech Stack

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
- PostgreSQL

### Development Tools
- IntelliJ IDEA / Visual Studio Code
- Git
- GitHub

### Testing
- JUnit
- Mockito

---

## 🏗️ System Architecture

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
│        PostgreSQL           │
│          Database           │
└─────────────────────────────┘

---

## 🗄️ Database Design (ER Diagram)

Database ของ MovieMood ประกอบด้วยข้อมูลหลักสำหรับผู้ใช้ ภาพยนตร์ Mood และประวัติการใช้งาน

```text
┌──────────────┐
│     User     │
├──────────────┤
│ id           │
│ username     │
│ email        │
│ password     │
└──────┬───────┘
       │
       │ 1 : 1
       ▼
┌──────────────┐
│ UserProfile  │
├──────────────┤
│ id           │
│ user_id      │
│ name         │
└──────────────┘

User
  │
  │ 1 : N
  ▼
┌─────────────────┐
│ UserPreference  │
├─────────────────┤
│ id              │
│ user_id         │
│ genre_id        │
│ mood_id         │
└─────────────────┘

┌──────────────┐
│    Genre     │
├──────────────┤
│ id           │
│ name         │
└──────┬───────┘
       │
       │ 1 : N
       ▼
┌──────────────┐
│    Movie     │
├──────────────┤
│ id           │
│ title        │
│ description  │
│ genre_id     │
│ duration     │
│ release_year │
└──────┬───────┘
       │
       │ N : M
       ▼
┌──────────────┐
│  MovieMood   │
├──────────────┤
│ movie_id     │
│ mood_id      │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│     Mood     │
├──────────────┤
│ id           │
│ name         │
└──────────────┘

User ───── 1 : N ───── MovieRating
User ───── 1 : N ───── WatchHistory
Movie ──── 1 : N ───── MovieRating
Movie ──── 1 : N ───── WatchHistory
```

> **Note:** ER Diagram ฉบับสมบูรณ์จะจัดทำเพิ่มเติมตามโครงสร้าง Database ที่พัฒนาจริง

---

## 📂 โครงสร้างโปรเจกต์ (Project Structure)

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

### Prerequisites
ก่อนเริ่มใช้งานโปรเจกต์ ต้องติดตั้ง Software ดังต่อไปนี้:
* **Java 17** หรือสูงกว่า
* **Maven**
* **PostgreSQL**
* **Git**

### 1. Clone Repository
```bash
git clone <repository-url>
cd MovieMood
```

### 2. ตั้งค่า PostgreSQL
สร้าง Database ชื่อ `moviemood` ใน PostgreSQL:
```sql
CREATE DATABASE moviemood;
```

### 3. ตั้งค่า Database Connection
เปิดไฟล์ `src/main/resources/application.properties` และกำหนดค่าการเชื่อมต่อ Database:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/moviemood
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
> **หมายเหตุ:** เปลี่ยน `username` และ `password` ให้ตรงกับ PostgreSQL ในเครื่องของผู้ใช้งาน

### 4. Install Dependencies
ใช้ Maven เพื่อดาวน์โหลด Dependencies ที่กำหนดไว้ใน `pom.xml`:

```bash
mvn install
```

สำหรับ Windows สามารถใช้:
```cmd
mvnw.cmd install
```

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
👉 **[http://localhost:8080](http://localhost:8080)**

### Stop Application
กด `Ctrl + C` ใน Terminal เพื่อหยุดการทำงานของ Application

---

## 📚 API Documentation

MovieMood ใช้ REST API สำหรับการสื่อสารระหว่าง Frontend และ Backend ตัวอย่าง API หลักของระบบ:

### User API
* `POST /api/users/register` - ลงทะเบียนผู้ใช้ใหม่
* `POST /api/users/login` - เข้าสู่ระบบ
* `GET  /api/users/{id}` - ดึงข้อมูลผู้ใช้ตาม ID
* `PUT  /api/users/{id}` - อัปเดตข้อมูลผู้ใช้

### Movie API
* `GET    /api/movies` - ดึงรายชื่อภาพยนตร์ทั้งหมด
* `GET    /api/movies/{id}` - ดึงข้อมูลภาพยนตร์ตาม ID
* `POST   /api/movies` - เพิ่มภาพยนตร์ใหม่
* `PUT    /api/movies/{id}` - แก้ไขข้อมูลภาพยนตร์
* `DELETE /api/movies/{id}` - ลบภาพยนตร์

### Mood API
* `GET  /api/moods` - ดึงหมวดหมู่อารมณ์ทั้งหมด
* `POST /api/moods` - เพิ่มหมวดหมู่อารมณ์ใหม่

### Recommendation API
* `GET  /api/recommendations/{userId}` - ดึงรายการแนะนำภาพยนตร์ตามผู้ใช้
* `POST /api/recommendations` - ประมวลผลคำแนะนำภาพยนตร์

### Rating API
* `POST /api/ratings` - บันทึกคะแนนรีวิวภาพยนตร์
* `GET  /api/ratings/movie/{movieId}` - ดึงรายการรีวิวตามภาพยนตร์

### Watch History API
* `POST /api/history` - บันทึกประวัติการรับชม
* `GET  /api/history/user/{userId}` - ดึงประวัติการรับชมของผู้ใช้

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