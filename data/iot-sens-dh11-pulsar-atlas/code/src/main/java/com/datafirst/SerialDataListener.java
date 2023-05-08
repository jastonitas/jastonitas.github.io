package com.datafirst;

import com.datafirst.dto.ClimateMeasureCanonical;
import com.datafirst.entity.ClimateMeasure;
import com.datafirst.entity.ClimateMeasureRepository;
import com.datafirst.util.AppBeanUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SerialDataListener {

    private ClimateMeasureRepository climateMeasureRepository;

    @Autowired
    public SerialDataListener(final ClimateMeasureRepository climateMeasureRepository) {
        this.climateMeasureRepository = climateMeasureRepository;
    }

    @PulsarListener(subscriptionName = "my-sub", topics = "my-topic")
    void listen(final String message) {
        try {
            ClimateMeasureCanonical climateMeasureCanonical = AppBeanUtils.deserializeToClimateMeasureCanonical(message);
            List<ClimateMeasure> measures = AppBeanUtils.dtoToClimateMeasure(climateMeasureCanonical);
            measures.stream().forEach(measure -> climateMeasureRepository.save(measure));
            System.out.println("listened and saved!");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
