package com.nexign.cdrservice.Controller;

import com.nexign.cdrservice.Entity.CdrEntity;
import com.nexign.cdrservice.Repository.CdrRepository;
import com.nexign.cdrservice.Service.CdrService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для обработки UDR-запросов.
 */
@RestController
@RequestMapping("/api/udr")
public class UdrController {
    private final CdrRepository repository;
    private final CdrService cdrService;

    public UdrController(CdrRepository repository, CdrService cdrService) {
        this.repository = repository;
        this.cdrService = cdrService;
    }

    /**
     * Генерирует CDR-записи и сохраняет их в базу данных.
     *
     * @param count количество записей для генерации (по умолчанию 1000).
     * @return строка с подтверждением генерации.
     */
    @PostMapping("/generate")
    public String generateCdr(@RequestParam(defaultValue = "1000") int count) {
        cdrService.generateCdrRecords(count);

        return "Сгенерировано " + count + " CDR-записей.";
    }

    /**
     * Получает UDR-запись для заданного абонента.
     *
     * @param msisdn номер абонента.
     * @param month  месяц в формате YYYY-MM (необязательный параметр).
     * @return UDR-объект в формате JSON.
     */
    @GetMapping("/{msisdn}")
    public Map<String, Object> getUdrByMsisdn(@PathVariable String msisdn, @RequestParam(required = false) String month) {
        LocalDateTime startTime = month != null ? YearMonth.parse(month).atDay(1).atStartOfDay() : LocalDateTime.now().minusYears(1);
        LocalDateTime endTime = month != null ? YearMonth.parse(month).atEndOfMonth().atTime(23, 59, 59) : LocalDateTime.now();

        List<CdrEntity> records = repository.findByCallerOrReceiverAndStartTimeBetween(msisdn, msisdn, startTime, endTime);

        return buildUdrResponse(msisdn, records);
    }

    /**
     * Получает UDR-записи для всех абонентов за заданный месяц.
     *
     * @param month месяц в формате YYYY-MM.
     * @return список UDR-записей для всех абонентов.
     */
    @GetMapping("/all")
    public List<Map<String, Object>> getAllUdrs(@RequestParam String month) {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(CdrEntity::getCaller))
                .entrySet().stream()
                .map(entry -> buildUdrResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Формирует UDR-ответ на основе CDR-записей.
     *
     * @param msisdn  номер абонента.
     * @param records список CDR-записей.
     * @return объект UDR в формате JSON.
     */
    private Map<String, Object> buildUdrResponse(String msisdn, List<CdrEntity> records) {
        Duration incomingDuration = records.stream()
                .filter(r -> r.getCallType().equals("02"))
                .map(r -> Duration.between(r.getStartTime(), r.getEndTime()))
                .reduce(Duration.ZERO, Duration::plus);

        Duration outgoingDuration = records.stream()
                .filter(r -> r.getCallType().equals("01"))
                .map(r -> Duration.between(r.getStartTime(), r.getEndTime()))
                .reduce(Duration.ZERO, Duration::plus);

        Map<String, Object> response = new HashMap<>();
        response.put("msisdn", msisdn);
        response.put("incomingCall", Map.of("totalTime", formatDuration(incomingDuration)));
        response.put("outcomingCall", Map.of("totalTime", formatDuration(outgoingDuration)));

        return response;
    }

    /**
     * Форматирует длительность вызова в формат HH:mm:ss.
     *
     * @param duration длительность вызова.
     * @return строка в формате HH:mm:ss.
     */
    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }
}