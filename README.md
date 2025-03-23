## CDR Service  

### 📌 Описание  
Этот микросервис эмулирует процесс обработки данных о звонках мобильных операторов. Он:  
✅ Генерирует CDR-записи (Call Detail Record) и сохраняет их в базу данных **H2**.  
✅ Позволяет получать UDR-отчеты (Usage Detail Record) через REST API.  
✅ Позволяет получать агрегированные отчеты по каждому абоненту.  

Используется для имитации биллинга сотового оператора.  

---

### 🛠️ Технологии  
- **Java 17**  
- **Spring Boot 3** (Spring Web, Spring Data JPA)  
- **H2 Database** (встроенная in-memory БД)  
- **Maven** (сборка проекта)  

---

### 📂 Структура проекта  

```
📦 src/main/java/com/nexign/cdrservice
 ┣ 📂 Controller
 ┃ ┗ 📜 UdrController.java   # REST-контроллер для получения UDR
 ┣ 📂 Entity
 ┃ ┗ 📜 CdrEntity.java       # JPA-сущность CDR-записи
 ┣ 📂 Repository
 ┃ ┗ 📜 CdrRepository.java   # Репозиторий для работы с БД
 ┣ 📂 Service
 ┃ ┗ 📜 CdrService.java      # Логика генерации CDR
 ┗ 📜 CdrServiceApplication.java # Точка входа в приложение
```

---

### 📊 Структура данных  

#### **CDR (Call Detail Record) — запись о звонке**  
| Поле       | Тип данных      | Описание |
|------------|---------------|----------|
| `id`       | Long          | Уникальный ID записи |
| `callType` | String        | "01" — исходящий, "02" — входящий |
| `caller`   | String        | Номер звонящего абонента |
| `receiver` | String        | Номер принимающего абонента |
| `startTime`| LocalDateTime | Дата и время начала звонка |
| `endTime`  | LocalDateTime | Дата и время окончания звонка |

Пример **CDR-отчета (CSV формат)**:  
```
02,79876543221,79123456789,2025-02-10T14:56:12,2025-02-10T14:58:20
01,79996667755,79876543221,2025-02-10T10:12:25,2025-02-10T10:12:57
```

#### **UDR (Usage Detail Record) — агрегированный отчет**  
Пример JSON-отчета:  
```json
{
    "msisdn": "79992221122",
    "incomingCall": {
        "totalTime": "02:12:13"
    },
    "outcomingCall": {
        "totalTime": "00:02:50"
    }
}
```

---

### 📡 API  

#### ✅ **1. Генерация CDR-записей**  
Создаёт N случайных записей о звонках в БД.  
📌 **POST** `/api/udr/generate?count=1000`  
💡 **Пример ответа**:  
```
Сгенерировано 1000 CDR-записей.
```

#### ✅ **2. Получение UDR по абоненту**  
Возвращает UDR-отчет по указанному номеру.  
📌 **GET** `/api/udr/{msisdn}?month=2025-02`  
💡 **Пример ответа**:  
```json
{
    "msisdn": "79991112233",
    "incomingCall": { "totalTime": "01:45:30" },
    "outcomingCall": { "totalTime": "00:23:15" }
}
```

#### ✅ **3. Получение UDR для всех абонентов**  
📌 **GET** `/api/udr/all?month=2025-02`  
💡 **Пример ответа**:  
```json
[
    {
        "msisdn": "79991112233",
        "incomingCall": { "totalTime": "01:45:30" },
        "outcomingCall": { "totalTime": "00:23:15" }
    },
    {
        "msisdn": "79887766554",
        "incomingCall": { "totalTime": "00:50:10" },
        "outcomingCall": { "totalTime": "00:10:05" }
    }
]
```
