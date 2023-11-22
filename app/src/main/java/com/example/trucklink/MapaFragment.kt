package com.example.trucklink
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapaFragment : Fragment() {
    private var pedido: Pedido? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private var mMap: GoogleMap? = null
    private lateinit var locationOrigen: LatLng
    private lateinit var locationDestino: LatLng

    val noroesteMapa = LatLng(4.4845, -74.1479)
    val suresteMapa =  LatLng(4.7634, -74.0039)
    val bounds = LatLngBounds(suresteMapa, noroesteMapa)

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        if (mMap != null) {
            mMap!!.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = true
            mMap!!.uiSettings?.isZoomControlsEnabled = true
            mMap!!.uiSettings?.isZoomGesturesEnabled = true
            val pedido = arguments?.getParcelable<Pedido>("pedido")
            val geocoder = Geocoder(requireContext())
            centrarMapa(noroesteMapa, suresteMapa)
            if (pedido != null) {

                val origen = pedido.direccion_recoger
                val destino = pedido.direccion_entregar

                if (origen?.isNotEmpty() == true) {
                    searchAddress1(origen)
                } else {
                    Toast.makeText(requireContext(), "Dirección de recogida no encontrada", Toast.LENGTH_SHORT).show()
                }

                if (destino?.isNotEmpty() == true) {
                    searchAddress(destino)
                } else {
                    Toast.makeText(requireContext(), "Dirección de entrega no encontrada", Toast.LENGTH_SHORT).show()
                }

                if (locationOrigen!=null && locationDestino!=null){
                    centrarMapa(locationOrigen, locationDestino)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mapa, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun searchAddress1(address: String) {
        val geocoder = Geocoder(requireActivity())
        val addresses = geocoder.getFromLocationName(address, 1)

        if (addresses?.isNotEmpty() == true) {
            val location = LatLng(addresses[0].latitude, addresses[0].longitude)

            if (bounds.contains(location)) {
                mMap?.addMarker(MarkerOptions().position(locationOrigen!!).title("Dirección donde recoger carga").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.start)))
            } else {
                Toast.makeText(requireActivity(), "Dirección fuera de Bogotá", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireActivity(), "Dirección no encontrada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchAddress(address: String) {
        val geocoder = Geocoder(requireActivity())
        val addresses = geocoder.getFromLocationName(address, 1)

        if (addresses?.isNotEmpty() == true) {
            val location = LatLng(addresses[0].latitude, addresses[0].longitude)

            if (bounds.contains(location)) {
                mMap?.addMarker(MarkerOptions().position(locationDestino!!).title("Dirección de entrega").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.end)))
            } else {
                Toast.makeText(requireActivity(), "Dirección fuera de Bogotá", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireActivity(), "Dirección no encontrada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun centrarMapa(noroesteMapa: LatLng, suresteMapa: LatLng) {
        val centroMapa= LatLng(
            (noroesteMapa.latitude + suresteMapa.latitude) / 2,
            (noroesteMapa.longitude + suresteMapa.longitude) / 2
        )
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(centroMapa, 10f))
    }

    companion object {
        fun newInstance(pedido: Pedido): MapaFragment {
            val fragment = MapaFragment()
            val args = Bundle()
            args.putParcelable("pedido", pedido)
            fragment.arguments = args
            return fragment
        }
    }
}
