<html>
<head>
	<title>Tutorial de consumo de datos de un sensor en Arduino Leonardo hacia Atlas MongoDB</title>
</head>
<body>
	<h1>IoT: Sensorización y consumo de datos con Arduino, Apache Pulsar MongoDB y Reporting near Real Time</h1>
	
	<p>
		En este articulo, revisaremos el consumo los datos de un sensor en Arduino Leonardo y almacenarlos en una base de datos en línea, como Atlas MongoDB. Vamos con ello.
	</p>	
	<p>
		<b>Paso 1:</b> Antes que nada, necesitamos realizar las conexiones en nuestra placa arduino, en este caso estamos usando un Arduino Leonardo, para la prueba usaremos un sensor de temperatura y humedad de tipo DHT11, el esquema se muestra a continuación:
	</p>
	<p>
		<img src="/multimedia/conexion%20arduino%20y%20dht11.jpg" alt="Sensor DH11" class="center">
	</p>
	<p>
		<b>Paso 2:</b> Acto seguido debemos instalar Arduino IDE (en caso utilicemos Linux/Ubuntu puede que sea necesario autorizar a tu usuario para leer la cola del Puerto Serial usado por la conexión de Arduino). Una vez establecida la conexión de la placa arduino y el IDE, compilaremos el siguiente código fuente:
	</p>

{% highlight c++  linenos %}
#include "DHT.h"
#include <ArduinoJson.h>

#define DHTPIN 2     // Pin donde está conectado el sensor

#define DHTTYPE DHT11   // Descomentar si se usa el DHT 11
//#define DHTTYPE DHT22   // Sensor DHT22

DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(9600);
  Serial.println("Iniciando...");
  dht.begin();
}
void loop() {
  delay(2000);
  float h = dht.readHumidity(); //Leemos la Humedad
  float t = dht.readTemperature(); //Leemos la temperatura en grados Celsius

  DynamicJsonDocument json(512);
  JsonObject temperatura = json.createNestedObject("temperatura");
  temperatura["valor"] = t;
  temperatura["medida"] = "%";
  JsonObject humedad = json.createNestedObject("humedad");
  humedad["valor"] = h;
  humedad["medida"] = "grados";
  humedad["tipo"] = "C";

  //--------Enviamos las lecturas por el puerto serial-------------
  serializeJson(json, Serial);
  Serial.println();
}
{% endhighlight %}

	<p>
		Paso 3: Ahora compilamos y descargamos el código a arduino, debemos empezar a notar datos en el Monitor Serial de Arduino IDE:
		<pre>
	03:02:52.381 -> {"temperatura":{"valor":26.2,"medida":"%"},"humedad":{"valor":71,"medida":"grados","tipo":"C"}}
	03:02:54.408 -> {"temperatura":{"valor":26.2,"medida":"%"},"humedad":{"valor":71,"medida":"grados","tipo":"C"}}
	03:02:56.430 -> {"temperatura":{"valor":26.3,"medida":"%"},"humedad":{"valor":71,"medida":"grados","tipo":"C"}}
	03:02:58.481 -> {"temperatura":{"valor":26.3,"medida":"%"},"humedad":{"valor":71,"medida":"grados","tipo":"C"}}
	03:03:00.505 -> {"temperatura":{"valor":26.3,"medida":"%"},"humedad":{"valor":71,"medida":"grados","tipo":"C"}}
	03:03:02.523 -> {"temperatura":{"valor":26.3,"medida":"%"},"humedad":{"valor":71,"medida":"grados","tipo":"C"}}
	03:03:04.543 -> {"temperatura":{"valor":26.4,"medida":"%"},"humedad":{"valor":71,"medida":"grados","tipo":"C"}}
	03:03:06.611 -> {"temperatura":{"valor":26.3,"medida":"%"},"humedad":{"valor":71,"medida":"grados","tipo":"C"}}
		</pre>
	</p>

	<p>
		<b>Paso 3:</b> Ahora instalaremos Apache Pulsar, instrucciones de instalación las podemos encontrar en la página oficial de la herramienta: https://pulsar.apache.org/docs/3.0.x/getting-started-standalone/ , recomiendo realiar una instalación simple standalone para esta prueba, mientras escribía este artículo realicé una instalación en Docker, sin embargo puede presentar problemas por ejemplo con contenedores de PostgreSQL aún un poco complicados de resolver.
	</p>
	
	<p>
		<b>Paso 4:</b> Usaremos un proyecto Java, nos apoyaremos en SpringBoot y el módulo para pulsar y mongodb, pongo a disposición el código de este proyecto aquí: <a href="https://github.com/jastonitas/jastonitas.github.io/tree/main/data/iot-sens-dh11-pulsar-atlas/code">SpringBoot Pulsar Project</a> veamos a continuación la clase en la que recuperamos los datos que van llegando al puerto serial gracias al código ejecutandose en nuestra placa Arduino:
	</p>

{% highlight java linenos %}
package com.datafirst;

import com.fazecast.jSerialComm.SerialPort;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
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
{% endhighlight %}

	<p>
		<b>Paso 5:</b> Para este caso levantaremos un listener en el mismo proyecto, en situaciones reales, deberían ser dos instancias separadas con manejos de carga independiente, el siguinete listener se activa cada vez que pulsar recibe un mensaje (gracias al producer descrito en el paso anterior) y lo persiste en una instancia de Atlas de mongo DB:
	</p>
	
{% highlight java linenos %}
package com.datafirst;

import com.datafirst.dto.ClimateMeasureCanonical;
import com.datafirst.entity.ClimateMeasure;
import com.datafirst.entity.ClimateMeasureRepository;
import com.datafirst.util.AppBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
{% endhighlight %}

	<p>
		Si los datos son registrados correctamente, en Atlas MongoDB podremos apreciar los datos aproximadamente como sigue:
	</p>
	<img src="/multimedia/mongodb-image.png" alt="MongoDB Data" class="center">
	
	<p>
		<b>Paso 6:</b> Una vez que los datos están siendo registrados en MongoDB podemos usarlos para consumirlos desde cualquier cliente compatible con MongoDB, el enfoque más simple puede consistir en una actualización en un intervalo de minutos, para lo cual para este caso usaremos Atlas Charts, en una próxima actualización detallaremos la generación de los Atlas Charts, adjunto imagen de cómo lucen los reportes de Temperatura y Humedad:
	</p>
	<img src="/multimedia/mongo-atlas-charts.png" alt="Atlas MongoDB Charts" class="center">
	
</body>
</html>
