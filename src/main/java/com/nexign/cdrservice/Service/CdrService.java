package com.nexign.cdrservice.Service;

import com.nexign.cdrservice.Entity.CdrEntity;
import com.nexign.cdrservice.Repository.CdrRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Сервис для генерации CDR-записей и их сохранения в базу данных.
 */
@Service
public class CdrService {

    private final CdrRepository cdrRepository;
    private final List<String> phoneNumbers = List.of(
            "79123456789", "79876543210", "79998887766",
            "79556667788", "79223344556", "79667788990",
            "79334455667", "79778899001", "79445566778", "79997776655"
    );

    public CdrService(CdrRepository cdrRepository) {
        this.cdrRepository = cdrRepository;
    }

    /**
     * Генерирует случайные CDR-записи и сохраняет их в базу данных.
     *
     * @param numberOfRecords количество записей, которые необходимо создать.
     */
    @Transactional
    public void generateCdrRecords(int numberOfRecords) {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now().minusYears(1);

        for (int i = 0; i < numberOfRecords; i++) {
            CdrEntity record = new CdrEntity();
            record.setCallType(random.nextBoolean() ? "01" : "02");
            record.setCaller(phoneNumbers.get(random.nextInt(phoneNumbers.size())));
            record.setReceiver(phoneNumbers.get(random.nextInt(phoneNumbers.size())));
            LocalDateTime startTime = now.plusSeconds(random.nextInt(3600 * 24 * 365)); // Генерация в пределах года
            record.setStartTime(startTime);
            record.setEndTime(startTime.plusSeconds(random.nextInt(300))); // Длительность до 5 мин

            cdrRepository.save(record);
        }
    }
}
