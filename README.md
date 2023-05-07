# jastonitas.github.io
Software &amp; data technology blog

<!DOCTYPE html>
<html>
<head>
	<title>Tutorial de consumo de datos de un sensor en Arduino Leonardo hacia Atlas MongoDB</title>
</head>
<body>
	<h1>Tutorial de consumo de datos de un sensor en Arduino Leonardo hacia Atlas MongoDB</h1>
	
	<p>En este tutorial, aprenderás cómo consumir los datos de un sensor en Arduino Leonardo y almacenarlos en una base de datos en línea, como Atlas MongoDB. A continuación, se presenta el código básico necesario para realizar la conexión entre el sensor, Arduino y la base de datos:</p>

	<pre>
		<code>
			// Código de conexión a Atlas MongoDB
			const char* host = "cluster0-shard-00-00.mongodb.net";
			const char* user = "user";
			const char* password = "password";
			const char* database = "database";
			const char* collection = "collection";
			mongocxx::uri uri("mongodb+srv://" + user + ":" + password + "@" + host + "/" + database + "?retryWrites=true&w=majority");
			mongocxx::client conn(uri);
			auto db = conn[database];
			auto coll = db[collection];

			// Código de lectura de sensor en Arduino Leonardo
			int sensorValue = analogRead(A0);
			float voltage = sensorValue * (5.0 / 1023.0);
			float temperature = (voltage - 0.5) * 100;
			Serial.println("Temperature: " + String(temperature) + "°C");

			// Código de inserción de datos en la base de datos
			bsoncxx::builder::stream::document document{};
			document << "temperature" << temperature;
			auto result = coll.insert_one(document.view());
			if (result) {
				Serial.println("Data inserted successfully");
			} else {
				Serial.println("Error inserting data");
			}
		</code>
	</pre>

	<p>Este es solo un ejemplo básico de cómo se puede realizar la conexión y el almacenamiento de datos en línea con Arduino Leonardo y Atlas MongoDB. Para obtener más información detallada sobre cómo consumir datos de un sensor en Arduino y almacenarlos en una base de datos en línea, puedes seguir leyendo el tutorial completo en mi sitio web o en mi repositorio de GitHub.</p>
	
	<p>¡Gracias por leer este tutorial! Espero que haya sido útil para ti.</p>
</body>
</html>
