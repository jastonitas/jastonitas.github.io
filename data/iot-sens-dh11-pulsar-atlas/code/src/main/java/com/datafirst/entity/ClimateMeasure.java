package com.datafirst.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Document(collection = "climate_track")
@AllArgsConstructor
public class ClimateMeasure {

    @Id
    @Getter
    private String id;

    @Getter
    private String measureCode;

    @Getter
    private Double measureValue;

    @Getter
    private LocalDateTime measureTime;

    @Getter
    private LocalDateTime measureUpdated;

    /*
    public ClimateMeasure(final String measureCode,
                          final String measureValue,
                          final String measureTime,
                          final String measureUpdated) {
        super();
        this.measureCode = measureCode;
        this.measureValue = measureValue;
        this.measureTime = measureTime;
        this.measureUpdated = measureUpdated;
    }
    */

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, measureCode='%s', measureValue='%s', measureTime='%s', measureUpdated='%s']",
                id, measureCode, measureValue, measureTime, measureUpdated);
    }

}
