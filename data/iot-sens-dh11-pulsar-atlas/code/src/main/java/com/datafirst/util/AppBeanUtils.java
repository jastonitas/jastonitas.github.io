package com.datafirst.util;

import com.datafirst.dto.ClimateMeasureCanonical;
import com.datafirst.dto.HumidityCanonical;
import com.datafirst.dto.TemperatureCanonical;
import com.datafirst.entity.ClimateMeasure;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppBeanUtils {

    static public ClimateMeasureCanonical deserializeToClimateMeasureCanonical(final String jsonString) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, ClimateMeasureCanonical.class);
    }

    //Returning a list since there is not need for other data structure, it can change in forward
    static public List<ClimateMeasure> dtoToClimateMeasure(final ClimateMeasureCanonical dto){
        final List<ClimateMeasure> beans = new ArrayList<>();
        HumidityCanonical humidity = dto.getHumedad();
        TemperatureCanonical temperature = dto.getTemperatura();

        ClimateMeasure TemperatureBean = new ClimateMeasure(
                null,
                "TEMP01",
                temperature.getValor(),
                LocalDateTime.now(),
                null
        );

        ClimateMeasure HumidityBean = new ClimateMeasure(
                null,
                "HUMI01",
                humidity.getValor(),
                LocalDateTime.now(),
                null
        );
        beans.add(TemperatureBean);
        beans.add(HumidityBean);
        return beans;
    }

}
