package com.github.jw3.xbtgps

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {
    private val providerName = "extbtgps"

    companion object {
        fun locFromEvent(e: String, p: String): Location {
            val c = e.split(':').drop(1)
            val loc = Location(p)
            loc.latitude = c.first().toDouble()
            loc.longitude = c.last().toDouble()
            loc.accuracy = 1.0f
            loc.speed = 1.0f
            loc.altitude = 2000.0
            loc.bearing = 0.0f
            loc.time = System.currentTimeMillis()
            loc.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

            return loc
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pt0 = Point(39.724182, 79.336137, SpatialReferences.getWgs84())
        val map = ArcGISMap(Basemap.Type.IMAGERY, pt0.x, pt0.y, 16)
        mapView.map = map

        val bt = BluetoothAdapter.getDefaultAdapter()
        if (bt?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 999)
        }

        bt.bondedDevices.forEach { d -> println(" ==================> ${d.name}") }


        val dev = bt.bondedDevices.find { d -> d.name == "HC-05" }
        val sock = dev!!.createInsecureRfcommSocketToServiceRecord(dev.uuids.first().uuid)
        sock.connect()

        MyBluetoothService(sock).execute()

        val lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!lm.allProviders.contains(providerName))
            lm.addTestProvider(providerName,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    true,
                    Criteria.NO_REQUIREMENT,
                    Criteria.NO_REQUIREMENT)

        lm.setTestProviderEnabled(providerName, true)
        lm.setTestProviderStatus(providerName,
                LocationProvider.AVAILABLE, null, System.currentTimeMillis())

        val locationsLayer = GraphicsOverlay()
        val locationMarker = SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, -0x10000, 10f)
        val g = Graphic(pt0, locationMarker)
        locationsLayer.graphics.add(g)

        mapView.graphicsOverlays.add(locationsLayer)

        //lm.requestLocationUpdates(providerName, 100, 1.0f, LocationDisplayListener(g, { pt -> mapView.setViewpointCenterAsync(pt) }))


    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }


    class LocationDisplayListener(val g: Graphic, val fn: (pt: Point) -> Unit) : LocationListener {
        val sr: SpatialReference = SpatialReferences.getWgs84()

        override fun onLocationChanged(loc: Location?) {
            loc?.let { l ->
                val pt = Point(l.latitude, l.longitude, sr)
                fn(pt)
                g.geometry = pt
            }
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }
    }

    class MyBluetoothService(private val mmSocket: BluetoothSocket) : AsyncTask<Unit, Void, Unit>() {
        private val mmInStream: BufferedReader?
        private var mmBuffer: ByteArray? = null

        init {
            var tmpIn: InputStream? = null
            try {
                tmpIn = mmSocket.inputStream
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when creating input stream", e)
            }
            mmInStream = BufferedReader(InputStreamReader(tmpIn))
        }

        override fun doInBackground(vararg p0: Unit?): Unit {
            while (true) {
                try {
                    val l = mmInStream!!.readLine()
                    println(l)

                        //////////////////////
                        // on serial event
                        // val loc = locFromEvent(e, providerName);
                        // lm.setTestProviderLocation(providerName, loc)
                        //////////////////////
                } catch (e: Exception) {
                    Log.d(TAG, "Input stream was disconnected", e)
                }
            }
        }

        override fun onCancelled() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }

        companion object {
            private val TAG = "EXT_BT_GPS_TAG"
        }
    }
}