# SOLID Principles Analysis — Movie Mood


## สรุปผล

| หลักการ | ผลประเมิน | หลักฐานสำคัญ |
|---|---|---|
| S — Single Responsibility | สอดคล้องในระดับการแยกชั้นและบทบาท | `RecommendationController.java:24–54`, `RecommendationServiceImpl.java:25–26`, `MovieMapper.java` |
| O — Open/Closed | สอดคล้องบางส่วน | `MatchScoreStrategy.java:6–8`, `DefaultMatchScoreStrategy.java:11–21`, `MoodFocusedStrategy.java:10–20`, `TopRatedStrategy.java:11–21`; แต่ `RecommendationServiceImpl.java:73–79` ต้องแก้ switch เมื่อเพิ่มชื่อกลยุทธ์ |
| L — Liskov Substitution | มีหลักฐานเชิงโครงสร้าง แต่ยังไม่ยืนยันครบทุกพฤติกรรม | `MatchScoreStrategy.java:6–8` และคลาสที่ implement ทั้งสาม |
| I — Interface Segregation | สอดคล้องชัดเจนในส่วน Strategy; ส่วนอื่นยังมีข้อสังเกต | `MatchScoreStrategy.java:6–8`, `RecommendationService.java:8–12`, `MovieProvider.java:9–27` |
| D — Dependency Inversion | สอดคล้องชัดเจน | `RecommendationServiceImpl.java:32–57`, `MovieServiceImpl.java:26–37`, `MovieProvider.java:9–27`, `TmdbMovieAdapter.java:22–23` |

> ทุก path ด้านล่างเริ่มจาก `src/main/java/com/example/movie_mood/` 

## S — Single Responsibility Principle (SRP)

**หลักการ:** แต่ละคลาสควรมีความรับผิดชอบหลักที่ชัดเจนและมีเหตุผลในการเปลี่ยนแปลงที่สอดคล้องกับหน้าที่นั้น

**หลักฐาน:**
- `controller/api/RecommendationController.java` บรรทัด **24–54**: รับ HTTP request, อ่าน `mood`/`strategy` และผู้ใช้ที่ยืนยันตัวตน แล้วส่งต่อไปยัง `RecommendationService` และ `MovieMapper` ไม่ได้คำนวณคะแนนแนะนำใน Controller
- `service/impl/RecommendationServiceImpl.java` บรรทัด **25–26, 66–79, 142–164**: ประสานการเลือกกลยุทธ์ คำนวณคะแนน และเรียงผลการแนะนำ
- `strategy/DefaultMatchScoreStrategy.java` บรรทัด **20–34**: รับผิดชอบเฉพาะสูตรคะแนนแบบ Default
- `strategy/MoodFocusedStrategy.java` บรรทัด **19–36**: รับผิดชอบเฉพาะสูตรคะแนนที่ให้น้ำหนักกับแนวหนังตามอารมณ์
- `strategy/TopRatedStrategy.java` บรรทัด **20–34**: รับผิดชอบเฉพาะสูตรคะแนนที่ให้น้ำหนักกับ Rating

**เหตุผล:** การรับ HTTP request, การประสานระบบแนะนำ และสูตรคำนวณคะแนนถูกแยกไว้คนละคลาส จึงแก้สูตรคะแนนโดยไม่ต้องนำสูตรไปเขียนรวมกับ Controller อย่างไรก็ตาม `RecommendationServiceImpl` ยังรับผิดชอบหลายขั้นตอนภายในงานแนะนำ เช่น ดึงหนัง กรองประวัติ เลือกกลยุทธ์ และเรียงลำดับ จึงไม่ควรอ้างว่าทุกคลาสในระบบมีหน้าที่เดียวอย่างสมบูรณ์

## O — Open/Closed Principle (OCP)

**หลักการ:** ควรเพิ่มพฤติกรรมใหม่ผ่านการขยายโค้ด มากกว่าต้องแก้โค้ดเดิมทุกครั้ง

**หลักฐาน:**
- `strategy/MatchScoreStrategy.java` บรรทัด **6–8**: กำหนดสัญญา `calculateScore(Movie movie, Mood mood)`
- `strategy/DefaultMatchScoreStrategy.java` บรรทัด **11–12, 20–34**
- `strategy/MoodFocusedStrategy.java` บรรทัด **10–11, 19–36**
- `strategy/TopRatedStrategy.java` บรรทัด **11–12, 20–34**
- `service/impl/RecommendationServiceImpl.java` บรรทัด **73–79**: เลือก Strategy ด้วย `switch`

**เหตุผล:** สามารถสร้างคลาสใหม่ที่ `implements MatchScoreStrategy` เพื่อเพิ่มสูตรคะแนนได้โดยไม่แก้ Interface หรือสูตรในคลาสเดิม แต่การเปิดให้ผู้ใช้เลือกชื่อ Strategy ใหม่ยังต้องเพิ่ม Dependency และแก้ `switch` ใน `RecommendationServiceImpl` ดังนั้นโครงสร้างนี้ **รองรับ OCP บางส่วน** ไม่ใช่เปิดต่อการขยายได้ทั้งหมดโดยไม่แก้โค้ดเดิม

## L — Liskov Substitution Principle (LSP)

**หลักการ:** ออบเจ็กต์ของคลาสที่ทำตาม Interface เดียวกันควรใช้แทนกันได้ โดยไม่ทำให้สัญญาการทำงานของผู้เรียกเสียหาย

**หลักฐาน:**
- `strategy/MatchScoreStrategy.java` บรรทัด **6–8**: กำหนดชนิดที่ผู้เรียกใช้งานต้องพึ่งพา
- `strategy/DefaultMatchScoreStrategy.java` บรรทัด **12, 20–34**
- `strategy/MoodFocusedStrategy.java` บรรทัด **11, 19–36**
- `strategy/TopRatedStrategy.java` บรรทัด **12, 20–34**
- `service/impl/RecommendationServiceImpl.java` บรรทัด **37–48, 73–79, 142–145**: ใช้ `MatchScoreStrategy` ตัวใดก็ได้ในการเรียก `calculateScore`

**เหตุผล:** ทั้งสามคลาส implement เมธอดเดียวกัน คืนค่า `double` และไม่ได้โยน `UnsupportedOperationException` ในเมธอดที่แสดง จึงสามารถสลับเป็นตัวคำนวณคะแนนผ่าน Interface ได้ในเชิงโครงสร้าง โดย `RecommendationServiceImpl` ไม่จำเป็นต้องรู้ชนิดคลาสขณะเรียก `calculateScore` อย่างไรก็ตาม การยืนยัน LSP อย่างครบถ้วนต้องมีการทดสอบข้อกำหนดด้านพฤติกรรม เช่น ค่าคะแนนในขอบเขตที่คาดหวัง และกรณีค่า `null` จึงสรุปได้เพียงว่า **มีหลักฐานสนับสนุน** ไม่ใช่พิสูจน์ครบทุกกรณี

## I — Interface Segregation Principle (ISP)

**หลักการ:** ไม่ควรบังคับคลาสให้พึ่งพาเมธอดที่ตนไม่จำเป็นต้องใช้

**หลักฐาน:**
- `strategy/MatchScoreStrategy.java` บรรทัด **6–8**: Interface เล็ก มีเมธอดเดียวคือ `calculateScore`
- `strategy/DefaultMatchScoreStrategy.java` บรรทัด **12, 20–34**, `strategy/MoodFocusedStrategy.java` บรรทัด **11, 19–36** และ `strategy/TopRatedStrategy.java` บรรทัด **12, 20–34**: แต่ละคลาส implement เฉพาะเมธอดที่จำเป็นต่อการคำนวณคะแนน
- `service/RecommendationService.java` บรรทัด **8–12**: Interface สำหรับการขอรายการแนะนำโดยเฉพาะ

**เหตุผล:** การแยก `MatchScoreStrategy` ออกเป็น Interface ขนาดเล็กช่วยให้กลยุทธ์ทั้งสามไม่ต้อง implement ฟังก์ชันที่ไม่เกี่ยวข้อง ขณะเดียวกัน `MovieProvider.java` บรรทัด **9–27** มีหลายเมธอดสำหรับงานต่างกัน เช่น ค้นหา ดูรายละเอียด และดึงวิดีโอ จึงยังมีโอกาสแยก Interface ให้ละเอียดขึ้นได้หากมีผู้ใช้บริการที่ต้องการเพียงบางความสามารถ

## D — Dependency Inversion Principle (DIP)

**หลักการ:** โมดูลระดับสูงควรพึ่งพา Abstraction แทนที่จะผูกกับ Concrete Class โดยตรง และรับ Dependency ผ่าน Constructor Injection

**หลักฐาน:**
- `service/impl/RecommendationServiceImpl.java` บรรทัด **32–48**: ประกาศ Dependency เป็น `MovieProvider`, `UserPreferenceService`, `WatchHistoryService` และ `MatchScoreStrategy` และรับผ่าน Constructor
- `service/impl/RecommendationServiceImpl.java` บรรทัด **50–57**: เก็บ Dependency ที่รับมาไว้ในฟิลด์
- `service/impl/MovieServiceImpl.java` บรรทัด **26–37**: รับ `MovieProvider` ผ่าน Constructor แทนการสร้าง `TmdbMovieAdapter` เอง
- `integration/tmdb/MovieProvider.java` บรรทัด **9–27**: Abstraction สำหรับผู้ให้บริการข้อมูลหนัง
- `integration/tmdb/TmdbMovieAdapter.java` บรรทัด **22–23**: Concrete Adapter ที่ `implements MovieProvider`
- `controller/api/RecommendationController.java` บรรทัด **26–34**: รับ `RecommendationService` ผ่าน Constructor

**เหตุผล:** Service และ Controller พึ่งพา Interface แทน Implementation ที่เฉพาะเจาะจง และใช้ Constructor Injection ซึ่งช่วยสลับ Implementation และทำ Unit Test ด้วย Mock ได้ง่ายขึ้น ตัวอย่างเช่น `MovieServiceImpl` ไม่ต้องสร้าง `TmdbMovieAdapter` โดยตรง แต่ใช้ `MovieProvider` ที่ Spring Inject เข้ามา


