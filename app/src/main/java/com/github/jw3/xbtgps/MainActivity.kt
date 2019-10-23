package com.github.jw3.xbtgps

import android.content.Context
import android.location.*
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val providerName = "extbtgps"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pt0 = Point(39.724182, 79.336137, SpatialReferences.getWgs84())
        val map = ArcGISMap(Basemap.Type.IMAGERY, pt0.x, pt0.y, 16)
        mapView.map = map

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

        lm.requestLocationUpdates(providerName, 100, 1.0f, LocationDisplayListener(g, { pt -> mapView.setViewpointCenterAsync(pt)}))

        thread(name = "location-reader") {
            for (c in coords) {
                val loc = Location(providerName)
                loc.latitude = c.first
                loc.longitude = c.second
                loc.accuracy = 1.0f
                loc.speed = 1.0f
                loc.altitude = 2000.0
                loc.bearing = 0.0f
                loc.time = System.currentTimeMillis()
                loc.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

                lm.setTestProviderLocation(providerName, loc)

                Thread.sleep(1000)
            }
        }
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
        private val mmInStream: InputStream?
        private var mmBuffer: ByteArray? = null

        init {
            var tmpIn: InputStream? = null
            try {
                tmpIn = mmSocket.inputStream
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when creating input stream", e)
            }
            mmInStream = tmpIn
        }

        override fun doInBackground(vararg p0: Unit?): Unit {
            mmBuffer = ByteArray(1024)
            while (true) {
                try {
                    mmInStream!!.read(mmBuffer)
                    mmBuffer?.let { bytes ->
                        println("bt: ${String(bytes)}")
                    }
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
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
            val uuid = UUID.randomUUID()
        }
    }