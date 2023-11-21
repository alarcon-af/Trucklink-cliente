package com.example.trucklink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"

/**
 * A simple [Fragment] subclass.
 * Use the [ReciboFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReciboFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: Int? = null
    private var param3: String? = null
    private var param4: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var ref: DatabaseReference

    private val mydb = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getInt(ARG_PARAM2)
            param3 = it.getString(ARG_PARAM3)
            param4 = it.getString(ARG_PARAM4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("usuarios/").child(auth.getUid().toString())

        val userId =auth.currentUser!!.uid
        val view = inflater.inflate(R.layout.fragment_recibo, container, false)
        val cargaTextView = view.findViewById<TextView>(R.id.carga)
        val pesoTextView = view.findViewById<TextView>(R.id.peso)
        val recogerTextView = view.findViewById<TextView>(R.id.recoger)
        val entregarTextView = view.findViewById<TextView>(R.id.entregar)
        val clienteTextView = view.findViewById<TextView>(R.id.cliente)
        val cedulaTextView = view.findViewById<TextView>(R.id.cedula)
        val volver = view.findViewById<Button>(R.id.cancelar)
        val confirmar = view.findViewById<Button>(R.id.confirmar)

        cargaTextView.text = "${param1 ?: ""}"
        pesoTextView.text = "${param2.toString() ?: ""}"
        recogerTextView.text = "${param3 ?: ""}"
        entregarTextView.text = "${param4 ?: ""}"

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val user = dataSnapshot.getValue(User::class.java)
                if(user!=null){
                    val nombreCompleto = "${user.nombre} ${user.apellido}"
                    clienteTextView.text = nombreCompleto
                    cedulaTextView.text = user.cedula.toString()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error en la base de datos", Toast.LENGTH_LONG).show()
            }
        })

        volver.setOnClickListener {
            val crearFragment = CrearFragment()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, crearFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        confirmar.setOnClickListener {
            ref = Firebase.database.reference
            val pedidoId = UUID.randomUUID().toString()
            val pedido = Pedido(
                pedidoId = pedidoId,
                carga = param1!!,
                peso = param2!!,
                cliente = userId,
                driver = null,
                direccion_recoger = param3!!,
                direccion_entregar = param4!!,
                latitud = 0.0,
                longitud = 0.0,
                estado = "activo"
            )
            ref = mydb.getReference("pedidos/"+pedidoId)
            ref.setValue(pedido).addOnSuccessListener {
                val mapaFragment = MapaFragment.newInstance(pedido)

                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, mapaFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }



        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReciboFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: Int, recolectaText: String, entregaText: String) =
            ReciboFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, recolectaText)
                    putString(ARG_PARAM4, entregaText)
                }
            }
    }
}