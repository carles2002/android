package com.example.beaconandroid

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import org.altbeacon.beacon.RangeNotifier

class MainActivity : AppCompatActivity(), BeaconConsumer {

    private lateinit var beaconManager: BeaconManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var beaconInfoTextView = findViewById(R.id.beaconInfoTextView) as TextView

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

        //Arrancar servicio que crea beacons
        val intent = Intent(this, BeaconService::class.java)
        //startService(intent)

        // Establecer el rango de beacons
        beaconManager.addRangeNotifier(object : RangeNotifier {
            override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
                if (beacons != null && beacons.isNotEmpty()) {

                    // Imprimir los beacons encontrados por consola
                    for (beacon in beacons) {
                        Log.d("Beacon1234", "Beacon encontrado: $beacon")
                        val beaconInfo = "UUID: ${beacon.id1}, Major: ${beacon.id2}, Minor: ${beacon.id3}"

                        beaconInfoTextView.text = beaconInfo // Actualizar el TextView con la información del beacon

                    }
                }
            }
        })

        // Iniciar la detección de beacons en la región especificada
        beaconManager.bind(this)
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
