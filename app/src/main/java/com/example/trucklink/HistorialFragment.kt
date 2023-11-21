package com.example.trucklink

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [HistorialFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistorialFragment : Fragment(), PedidoAdapter.PedidoAdapterCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoAdapter: PedidoAdapter
    private lateinit var pedidos: ArrayList<Pedido>
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    var eventListener: ValueEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_historial, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("pedidos")
        val currentUser = auth.currentUser
        val currentUserId = currentUser?.uid
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_pedidos)
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.layoutManager = gridLayoutManager
        pedidos = ArrayList()
        pedidoAdapter = PedidoAdapter(requireContext(), pedidos, this)
        recyclerView.adapter = pedidoAdapter

        eventListener = myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pedidos.clear()
                for(itemSnapshot in snapshot.children){
                    val pedido = itemSnapshot.getValue(Pedido::class.java)
                    if(pedido!=null && pedido.cliente == currentUserId){
                        pedidos.add(pedido)
                    }
                }
                pedidoAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        } )
        return view
    }

    override fun onPedidoClicked(pedido: Pedido) {
        // Aquí manejas el clic en un pedido, por ejemplo, lanzar un nuevo fragmento
        val confirmarPedidoFragment = VerPedidoFragment.newInstance(pedido)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, confirmarPedidoFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val ARG_PEDIDO = "pedido"

        @JvmStatic
        fun newInstance(pedido: Pedido) =
            VerPedidoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PEDIDO, pedido)
                }
            }
    }
}