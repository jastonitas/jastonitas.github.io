package com.datafirst;

import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class SerialDataProducer {
    PulsarTemplate pulsarTemplate;

    public SerialDataProducer(final PulsarTemplate pulsarTemplate) {
        this.pulsarTemplate = pulsarTemplate;
    }

    @Async
    public void readAndProduce() {
        SerialPort comPort = SerialPort.getCommPorts()[1];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        InputStream in = comPort.getInputStream();

        try {
            StringBuilder sb = new StringBuilder();
            //for (int j = 0; j < 1000; ++j) {
            while (true) {
                char ch = (char) in.read();
                if (ch == '\n') {
                    sendPulsarMessage(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append(ch);
                }
            }
            //in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        comPort.closePort();
    }

    public void sendPulsarMessage(final String message) throws PulsarClientException {
        System.out.println("Sent: " + message);
        MessageId send = pulsarTemplate.send("my-topic", message);
    }
}
