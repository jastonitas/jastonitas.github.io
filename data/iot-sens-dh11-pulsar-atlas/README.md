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
		Paso 1: Antes que nada, necesitamos realizar las conexiones en nuestra placa arduino, en este caso estamos usando un Arduino Leonardo, para la prueba usaremos un sensor de temperatura y humedad de tipo DHT11
	</p>
	<p>
		<img src="/multimedia/conexion%20arduino%20y%20dht11.jpg" alt="Girl in a jacket">
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


</body>
</html>
