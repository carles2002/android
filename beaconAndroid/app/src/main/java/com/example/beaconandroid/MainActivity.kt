package com.example.beaconandroid
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.service.BeaconService
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), BeaconConsumer {

    private lateinit var beaconManager: BeaconManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val beaconInfoTextView = findViewById<TextView>(R.id.beaconInfoTextView)

        // Inicializar el administrador de beacons
        beaconManager = BeaconManager.getInstanceForApplication(this)

        // Habilitar el monitoreo en segundo plano (opcional)
        beaconManager.backgroundBetweenScanPeriod = 0
        beaconManager.backgroundScanPeriod = 1100

        // Configurar el BeaconParser para iBeacon
        val iBeaconParser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        beaconManager.beaconParsers.add(iBeaconParser)

        // Crear una región para detectar todos los beacons (UUID, major y minor en null)
        val region = Region("myRegion", null, null, null)

        Log.d("Beacon1234", "START")

        // Arrancar servicio que crea beacons
        val intent = Intent(this, BeaconService::class.java)
        // startService(intent)

        // Establecer el rango de beacons
        beaconManager.addRangeNotifier(object : RangeNotifier {
            override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
                if (beacons != null && beacons.isNotEmpty()) {
                    for (beacon in beacons) {
                        Log.d("Beacon1234", "Beacon encontrado: $beacon")

                        // Crear un nuevo objeto JSON para cada beacon detectado
                        val jsonObject = JSONObject()
                        jsonObject.put("clave1", beacon.id2) // Major en clave1
                        jsonObject.put("clave2", beacon.id3) // Minor en clave2

                        // Convertir el objeto JSON a una cadena
                        val jsonData = jsonObject.toString()

                        // Actualizar el TextView con la información del beacon
                        val beaconInfo = "UUID: ${beacon.id1}, Major: ${beacon.id2}, Minor: ${beacon.id3}"
                        beaconInfoTextView.text = beaconInfo

                        // Enviar la solicitud HTTP en segundo plano con el JSON actualizado
                        GlobalScope.launch(Dispatchers.IO) {
                            val responseCode = sendHttpRequest(jsonData)
                            // Procesar la respuesta aquí si es necesario
                        }
                    }
                }
            }
        })

        // Iniciar la detección de beacons en la región especificada
        beaconManager.bind(this)


    }

    private fun sendHttpRequest(jsonData: String): Int {
        try {
            val url = URL("http://192.168.0.56/bd/recibir_json.php")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val outputStream = connection.outputStream
            val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
            writer.write(jsonData)
            writer.close()
            outputStream.close()

            // Verificar el código de respuesta HTTP
            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // La solicitud fue exitosa (código 200)
                Log.d("Beacon1234", "Código de respuesta HTTP: $responseCode")
            } else {
                // Ocurrió un error en la solicitud

                Log.e("Beacon1234", "Código de respuesta HTTP: $responseCode")
            }

            return responseCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la detección de beacons cuando la actividad se destruye
        beaconManager.unbind(this)
        val intent = Intent(this, BeaconService::class.java)
        stopService(intent)
        Log.d("Beacon1234", "STOP")
    }

    override fun onBeaconServiceConnect() {
        try {
            // Iniciar el monitoreo en la región
            beaconManager.startRangingBeaconsInRegion(Region("myRegion", null, null, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
