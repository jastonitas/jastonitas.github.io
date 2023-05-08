package com.datafirst.dto;

import lombok.Data;

@Data
public class ClimateMeasureCanonical {
    private TemperatureCanonical temperatura;
    private HumidityCanonical humedad;
}
