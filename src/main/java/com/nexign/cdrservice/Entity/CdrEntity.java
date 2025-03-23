package com.nexign.cdrservice.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Сущность CDR-записи, представляющая данные о звонках.
 */
@Setter
@Getter
@Entity
@Table(name = "CDR_RECORDS")
public class CdrEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * Тип вызова:
     * - "01" - Исходящий
     * - "02" - Входящий
     */
    @Column(name = "CALLTYPE")
    private String callType;

    /**
     * Номер абонента, совершающего звонок.
     */
    @Column(name = "CALLER")
    private String caller;

    /**
     * Номер абонента, принимающего звонок.
     */
    @Column(name = "RECEIVER")
    private String receiver;

    /**
     * Время начала звонка.
     */
    @Column(name = "STARTTIME")
    private LocalDateTime startTime;

    /**
     * Время окончания звонка.
     */
    @Column(name = "ENDTIME")
    private LocalDateTime endTime;

    public CdrEntity() {
    }

    public CdrEntity(String callType, String caller, String receiver, LocalDateTime startTime, LocalDateTime endTime) {
        this.callType = callType;
        this.caller = caller;
        this.receiver = receiver;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
