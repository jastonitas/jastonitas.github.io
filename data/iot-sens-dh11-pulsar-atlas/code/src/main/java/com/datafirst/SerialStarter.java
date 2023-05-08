package com.datafirst;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;

public class SerialStarter {

    public static void main(String[] args) {
        System.out.println("Hello world");

        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port: ports) {
            System.out.println(port.getSystemPortName());
        }

        SerialPort comPort = SerialPort.getCommPorts()[1];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        InputStream in = comPort.getInputStream();

        try {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 2000; ++j) {
                char ch = (char) in.read();
                if (ch == '\n') {
                    System.out.println("Cadena: " + sb);
                    sb = new StringBuilder();
                } else {
                    sb.append(ch);
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        comPort.closePort();
    }

}
