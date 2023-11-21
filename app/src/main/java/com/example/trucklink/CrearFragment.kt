package com.example.trucklink


import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CrearFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrearFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_crear, container, false)

        val cargaEditText = view.findViewById<EditText>(R.id.carga)
        val pesoEditText = view.findViewById<EditText>(R.id.peso)
        val recolectaEditText = view.findViewById<EditText>(R.id.recolecta)
        val entregaEditText = view.findViewById<EditText>(R.id.entrega)
        val crearButton = view.findViewById<Button>(R.id.button)

        crearButton.setOnClickListener {
            val cargaText = cargaEditText.text.toString()
            val pesoText = pesoEditText.text.toString().toInt()
            val recolectaText = recolectaEditText.text.toString()
            val entregaText = entregaEditText.text.toString()

            if (cargaText.isNotBlank() && pesoText != null && recolectaText.isNotBlank() && entregaText.isNotBlank()) {
                val reciboFragment = ReciboFragment.newInstance(cargaText, pesoText, recolectaText, entregaText)
                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container, reciboFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            } else {
                // Alguno de los campos está vacío, muestra un mensaje de error o realiza una acción apropiada
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
         * @return A new instance of fragment CrearFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CrearFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}