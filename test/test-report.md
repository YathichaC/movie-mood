# Movie Mood — Test Report

## 1. ข้อมูลการทดสอบ

- **Project:** Movie Mood
- **Technology:** Java, Spring Boot, Maven, JUnit 5, Mockito
- **Test execution:** PowerShell / Maven Wrapper (`mvnw.cmd`) และ HTTP requests (`curl.exe`)
- **Evidence directory:** `test/images/`


## 2. สรุปผลการทดสอบ

| ประเภทการทดสอบ | ผลที่ตรวจสอบได้ | สถานะ |
|---|---|---|
| Automated Tests (Maven) | 118 tests, 0 failures, 0 errors, 0 skipped | **PASS** |
| RecommendationServiceImplTest | 6 tests ผ่านในการรันชุดรวม | **PASS (full suite)** |
| PlaylistServiceTest | 9 tests ผ่านในการรันชุดรวม | **PASS (full suite)** |
| WatchHistoryServiceTest | 7 tests ผ่านในการรันชุดรวม | **PASS (full suite)** |
| UserPreferenceServiceTest | 8 tests ผ่านในการรันชุดรวม | **PASS (full suite)** |
| RecommendationStrategyTest | 1 test ผ่านในการรันชุดรวม | **PASS (full suite)** |
| MovieMoodApplicationTests (`@SpringBootTest`) | ผู้ทดสอบแจ้งว่ารันแยกผ่าน | **PASS (reported)** |
| REST API — Movies | 4 HTTP status checks ผ่าน | **PASS** |
| REST API — Moods (ไม่ส่ง JWT) | ได้ HTTP 403; ยังต้องตรวจสอบสิทธิ์ที่คาดหวัง | **NEEDS REVIEW** |
| Database CRUD Integration Test | ยังไม่มีหลักฐานยืนยัน | **NOT VERIFIED** |

> **ข้อจำกัด:** จำนวน Automated Tests 118 รายการเป็นผลจากการรัน Maven ทั้งชุด ไม่ควรนำจำนวน API Tests 4 รายการไปรวมเป็น 122 Automated Tests เพราะเป็นการทดสอบคนละรูปแบบ

## 3. Automated Testing (JUnit 5 / Mockito)

### 3.1 Full Test Suite

**คำสั่งที่ใช้:**

```powershell
cd C:\Users\User\movie-mood\code\movie_mood
.\mvnw.cmd clean test
```

**ผลการทดสอบที่บันทึกไว้:**

```text
Tests run: 118, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**ผล:** PASS — Automated Tests ทั้งหมดผ่าน ไม่มี Failure, Error หรือ Skipped

![Full Maven Test Results](./images/01-mvn-test.png)

### 3.2 Recommendation Service

`RecommendationServiceImplTest` มีผลผ่าน 6 Tests จากการรันชุดรวม ใช้เป็นหลักฐานการทดสอบส่วน Service ที่เกี่ยวข้องกับการแนะนำภาพยนตร์

![Recommendation Service Test](./images/02-unit-test.png)

### 3.3 Playlist Service

`PlaylistServiceTest` มีผลผ่าน 9 Tests จากการรันชุดรวม

![Playlist Service Test](./images/03-playlist-unit-test.png)

### 3.4 Authentication Service

การทดสอบ Authentication เป็นส่วนหนึ่งของแผนการเก็บหลักฐาน แต่ยังไม่มีรายละเอียดจำนวน Test Cases และผลการรันแยกที่ยืนยันได้จากข้อมูลที่ส่งมา จึงไม่ระบุจำนวนหรือสถานะ PASS เพิ่มเติมในหัวข้อนี้

![Authentication Test Evidence (if available)](./images/04-auth-unit-test.png)

### 3.5 TMDB Adapter / Cache Proxy

มีการกำหนดแผนทดสอบส่วน Adapter และ Proxy แต่ยังไม่มีผลการรันแยกที่ยืนยันได้ จึงไม่ระบุว่า PASS ในรายงานนี้

![TMDB Adapter Test Evidence (if available)](./images/05-tmdb-adapter-test.png)

![TMDB Proxy Test Evidence (if available)](./images/06-tmdb-proxy-test.png)

### 3.6 Watch History Service

`WatchHistoryServiceTest` มีผลผ่าน 7 Tests จากการรันชุดรวม

![Watch History Test](./images/07-watch-history-test.png)

### 3.7 User Preference Service

`UserPreferenceServiceTest` มีผลผ่าน 8 Tests จากการรันชุดรวม

![User Preference Test](./images/08-user-preferences-test.png)

## 4. Spring Boot Application Context Test

ตรวจพบ `@SpringBootTest` ใน `MovieMoodApplicationTests.java` และผู้ทดสอบยืนยันว่ารันคลาสนี้แยกแล้วผ่าน

**คำสั่ง:**

```powershell
.\mvnw.cmd "-Dtest=MovieMoodApplicationTests" test
```

**ผล:** PASS (ตามผลที่ผู้ทดสอบแจ้ง)

**ขอบเขต:** การใช้ `@SpringBootTest` แสดงว่ามีการทดสอบด้วย Spring Application Context แต่ยังไม่มีหลักฐานว่ามีการทดสอบ CRUD กับฐานข้อมูลจริงโดยตรง

![Spring Boot Context Test](./images/09-spring-boot-context-test.png)

## 5. REST API Testing

ทดสอบด้วย `curl.exe` ผ่าน `http://localhost:8080` และตรวจสอบ **HTTP Status Code** ที่ตอบกลับจริง

| Test ID | Test Case | Method / Endpoint | Expected | Actual | Result |
|---|---|---|---|---|---|
| API-01 | Browse Movies | `GET /api/v1/movies` | HTTP 200 | HTTP 200 | **PASS** |
| API-02 | Search Movies | `GET /api/v1/movies/search?keyword=batman` | HTTP 200 | HTTP 200 | **PASS** |
| API-03 | Search with Pagination | `GET /api/v1/movies/search?keyword=batman&page=1` | HTTP 200 | HTTP 200 | **PASS** |
| API-04 | Invalid Search (blank keyword) | `GET /api/v1/movies/search?keyword=` | HTTP 400 | HTTP 400 | **PASS** |

**Summary:** 4/4 HTTP Status Checks PASS, 0 FAIL

**คำสั่งที่ใช้:**

```powershell
curl.exe -s -o NUL -w "HTTP Status: %{http_code}`n" "http://localhost:8080/api/v1/movies"
curl.exe -s -o NUL -w "HTTP Status: %{http_code}`n" "http://localhost:8080/api/v1/movies/search?keyword=batman"
curl.exe -s -o NUL -w "HTTP Status: %{http_code}`n" "http://localhost:8080/api/v1/movies/search?keyword=batman&page=1"
curl.exe -s -o NUL -w "HTTP Status: %{http_code}`n" "http://localhost:8080/api/v1/movies/search?keyword="
```

![Movie API Status Check](./images/10-api-movies-test.png)

![Four REST API Test Results](./images/11-api-test-results.png)

**ข้อจำกัด:** คำสั่งด้านบนตรวจสอบเฉพาะ HTTP Status Code ไม่ได้ตรวจสอบเนื้อหา JSON, จำนวนภาพยนตร์ หรือความถูกต้องของข้อมูลที่ส่งกลับ

### 5.1 Mood API — Access Control Observation

ทดสอบ `GET /api/v1/moods` โดยไม่ส่ง JWT แล้วได้รับ `HTTP 403 Forbidden` สองครั้ง สอดคล้องกับความเป็นไปได้ว่า Endpoint ต้องผ่านการยืนยันตัวตน แต่ยังไม่สามารถสรุปว่าเป็น PASS หรือ Bug ได้จนกว่าจะตรวจสอบ Security Configuration และพฤติกรรมที่ออกแบบไว้

| Endpoint | Request | Actual | Status |
|---|---|---|---|
| `GET /api/v1/moods` | ไม่มี JWT | HTTP 403 | **NEEDS REVIEW** |

## 6. ข้อสรุปและข้อจำกัด

จากหลักฐานที่ตรวจสอบได้ Automated Tests ของ Movie Mood ผ่าน **118/118 Tests** และ REST API สำหรับ Browse/Search Movies ผ่าน **4/4 HTTP Status Checks** รวมทั้งมีการยืนยันว่า Spring Boot Context Test รันผ่าน

สิ่งที่ยังไม่ได้ยืนยันในรายงานนี้ ได้แก่ การทดสอบ CRUD กับฐานข้อมูลจริง, การตรวจสอบเนื้อหา JSON ของ API, และผลทดสอบ Frontend แบบ End-to-End ดังนั้นจึงไม่ระบุว่าองค์ประกอบเหล่านี้ผ่านแล้ว

---


