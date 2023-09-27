package com.example.beaconandroid

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Region

class BeaconService : Service(), BeaconConsumer {

    private lateinit var beaconManager: BeaconManager

    override fun onCreate() {
        super.onCreate()

        // Inicializar el administrador de beacons
        beaconManager = BeaconManager.getInstanceForApplication(this)
        Log.d("Beacon1234", "STARTSSSS")
        // Configurar el rango de beacons
        beaconManager.addRangeNotifier(object : RangeNotifier {
            override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
                if (beacons != null && beacons.isNotEmpty()) {
                    // Procesar los beacons encontrados
                    Log.d("Beacon1234", "FOUNDSSSS")
                    // Imprimir los beacons encontrados por consola
                    for (beacon in beacons) {
                        Log.d("Beacon1234", "Beacon encontradosSSSSS: $beacon")
                    }
                }
            }
        })

        // Iniciar la detección de beacons en la región especificada
        beaconManager.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener la detección de beacons cuando el servicio se detiene
        beaconManager.unbind(this)
        Log.d("Beacon1234", "STOPSSSSSSSS")
    }

    override fun onBeaconServiceConnect() {
        try {
            // Iniciar el monitoreo en la región
            beaconManager.startRangingBeaconsInRegion(Region("myRegion", null, null, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        // No es necesario para este servicio
        return null
    }
}
