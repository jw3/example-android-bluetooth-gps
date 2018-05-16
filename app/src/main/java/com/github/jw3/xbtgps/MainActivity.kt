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

        val pt0 = Point(coords.first().second, coords.first().first, SpatialReferences.getWgs84())
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


    val coords = listOf(
            Pair(79.336137405108389, 39.724182867976062),
            Pair(79.336145405139121, 39.724157867981212),
            Pair(79.336155405163282, 39.724135867988466),
            Pair(79.336162405191914, 39.724112867992893),
            Pair(79.33617740524663, 39.72406786800272),
            Pair(79.33618540526966, 39.724047868008313),
            Pair(79.336193405295774, 39.72402586801374),
            Pair(79.336200405321321, 39.724004868018319),
            Pair(79.336208405347421, 39.723982868023754),
            Pair(79.336222405400036, 39.723939868032843),
            Pair(79.336227405429042, 39.723917868035514),
            Pair(79.336230405459986, 39.723895868036351),
            Pair(79.336232405488829, 39.723875868036444),
            Pair(79.336237405542448, 39.723837868037727),
            Pair(79.336235405564395, 39.723824868034754),
            Pair(79.336230405587685, 39.723812868029142),
            Pair(79.336225405610975, 39.723800868023503),
            Pair(79.336223405629823, 39.723789868020724),
            Pair(79.336223405659055, 39.723770868019074),
            Pair(79.336225405658666, 39.723769868020817),
            Pair(79.336222405669261, 39.723764868017625),
            Pair(79.336213405684134, 39.72376086800903),
            Pair(79.336182405714098, 39.723760867980616),
            Pair(79.336162405734981, 39.723759867962194),
            Pair(79.336138405761275, 39.723757867940016),
            Pair(79.336118405776006, 39.723760867921946),
            Pair(79.336098405784554, 39.723767867904215),
            Pair(79.336073405774911, 39.723789867883212),
            Pair(79.33607240576049, 39.723799867883159),
            Pair(79.3360784057393, 39.723809867889528),
            Pair(79.336080405720438, 39.723820867892321),
            Pair(79.33607540569605, 39.723839867889396),
            Pair(79.3360734056903, 39.723844867887991),
            Pair(79.336068405682809, 39.723852867884098),
            Pair(79.336060405670565, 39.723865867877883),
            Pair(79.336050405657161, 39.723880867870029),
            Pair(79.336030405638041, 39.72390586785388),
            Pair(79.336018405626547, 39.723920867844186),
            Pair(79.336005405616064, 39.723935867833568),
            Pair(79.335990405612108, 39.723947867820861),
            Pair(79.335963405618202, 39.723960867797224),
            Pair(79.335952405630394, 39.723959867787073),
            Pair(79.335942405640068, 39.723959867777886),
            Pair(79.336103405084344, 39.724219867948086),
            Pair(79.336105405077816, 39.724222867950196),
            Pair(79.336120405186378, 39.724142867956999),
            Pair(79.336098405092272, 39.724217867943345),
            Pair(79.336137405226864, 39.724105867969357),
            Pair(79.336098405092272, 39.724217867943345),
            Pair(79.3361074051451, 39.724177867948107),
            Pair(79.336097405082469, 39.724224867943036),
            Pair(79.336088405089626, 39.724225867934869),
            Pair(79.33617540534857, 39.724002867995232),
            Pair(79.336143405251818, 39.724085867973123),
            Pair(79.336182405375638, 39.723980867999728),
            Pair(79.336182405375638, 39.723980867999728),
            Pair(79.336078405806987, 39.723765867885703),
            Pair(79.336080405775832, 39.723784867889187),
            Pair(79.336080405745051, 39.723804867890941),
            Pair(79.336068405701269, 39.723840867883077),
            Pair(79.336060405679788, 39.72385986787738),
            Pair(79.336050405664849, 39.723875867869609),
            Pair(79.336038405650314, 39.723892867860073),
            Pair(79.336027405637864, 39.723907867851288),
            Pair(79.335995405622654, 39.723937867824574),
            Pair(79.335980405618699, 39.723949867811861),
            Pair(79.335965405617813, 39.723959867798968),
            Pair(79.335948405598884, 39.723982867785395),
            Pair(79.335973405551613, 39.723997867809601),
            Pair(79.335953405620202, 39.723965867788515),
            Pair(79.33595840557382, 39.723992867795438),
            Pair(79.335992405536317, 39.723995867826865),
            Pair(79.336007405529514, 39.723990867840179),
            Pair(79.33601240554006, 39.723980867843899),
            Pair(79.336000405582439, 39.723960867831153),
            Pair(79.335988405594037, 39.723960867820153),
            Pair(79.335968405579536, 39.723982867803741),
            Pair(79.33597540558506, 39.723974867809446),
            Pair(79.335942405547769, 39.72401986778312),
            Pair(79.335953405558669, 39.724005867791988),
            Pair(79.335928405541281, 39.724032867771413),
            Pair(79.335912405544462, 39.724040867757445),
            Pair(79.335880405560033, 39.72405086772897),
            Pair(79.335860405576298, 39.724052867710817),
            Pair(79.335855405578044, 39.724054867706407),
            Pair(79.33585540557651, 39.72405586770649),
            Pair(79.335853405579982, 39.724054867704567),
            Pair(79.336008405217768, 39.724192867858662),
            Pair(79.336067405133036, 39.724210867914316),
            Pair(79.336083405140627, 39.724195867927683),
            Pair(79.336110405229874, 39.724120867945913),
            Pair(79.33612040525253, 39.724099867953257),
            Pair(79.336128405278657, 39.72407786795867),
            Pair(79.336145405331422, 39.724032867970337),
            Pair(79.336160405386138, 39.723987867980171),
            Pair(79.336153405359084, 39.724009867975688),
            Pair(79.336167405414784, 39.723964867984613),
            Pair(79.336173405442821, 39.723942867988193),
            Pair(79.336182405503337, 39.723897867992534),
            Pair(79.336187405533892, 39.723874867995107),
            Pair(79.336192405565967, 39.723850867997605),
            Pair(79.336193405600383, 39.723827867996526),
            Pair(79.336193405669619, 39.723782867992611),
            Pair(79.336190405703292, 39.723762867988121),
            Pair(79.336183405730054, 39.723749867980573),
            Pair(79.336168405759949, 39.723739867965953),
            Pair(79.336148405785451, 39.723735867947262),
            Pair(79.336107405811248, 39.723744867910455),
            Pair(79.336092405805758, 39.723757867897852),
            Pair(79.336088405783471, 39.723774867895642),
            Pair(79.336085405761764, 39.723790867894309),
            Pair(79.336078405742384, 39.723807867889363),
            Pair(79.336067405694564, 39.723845867882567),
            Pair(79.3360604056721, 39.723864867877808),
            Pair(79.336050405651008, 39.723884867870382),
            Pair(79.336040405632971, 39.723902867862776),
            Pair(79.336018405600399, 39.723937867845649),
            Pair(79.336008405590078, 39.723950867837615),
            Pair(79.335995405571879, 39.72397086782744),
            Pair(79.336000405582439, 39.723960867831153),
            Pair(79.336008405525462, 39.723992867841282))
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