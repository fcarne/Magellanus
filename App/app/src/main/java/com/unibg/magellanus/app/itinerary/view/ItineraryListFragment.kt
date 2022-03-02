package com.unibg.magellanus.app.itinerary.view

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.unibg.magellanus.app.common.observeOnce
import com.unibg.magellanus.app.databinding.FragmentItineraryListBinding
import com.unibg.magellanus.app.itinerary.model.Itinerary
import com.unibg.magellanus.app.itinerary.model.ItineraryRepositoryImpl
import com.unibg.magellanus.app.itinerary.model.network.GeocodingAPI
import com.unibg.magellanus.app.itinerary.model.network.ItineraryAPI
import com.unibg.magellanus.app.itinerary.viewmodel.ItineraryListViewModel
import com.unibg.magellanus.app.auth.impl.FirebaseAuthenticationProvider
import kotlin.properties.Delegates

class ItineraryListFragment : Fragment() {

    private var columnCount = 1
    private var call by Delegates.notNull<Int>()

    private val viewModel by viewModels<ItineraryListViewModel> {
        val provider = FirebaseAuthenticationProvider()
        val api = ItineraryAPI.create(provider)
        val geoApi = GeocodingAPI.create(requireContext().cacheDir)
        val repository = ItineraryRepositoryImpl(api, geoApi)

        ItineraryListViewModel.Factory(repository)
    }

    private lateinit var navController: NavController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        call = requireArguments().getInt(CALL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentItineraryListBinding.inflate(inflater, container, false)
        with(binding.list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }

            viewModel.itineraryList.observeOnce(viewLifecycleOwner) {

                // setta i listener degli item della recycler view
                val itemClickListener =
                    ItineraryRecyclerViewAdapter.OnItineraryItemClickListener { itinerary ->
                        navController.navigate(
                            ItineraryExplorerFragmentDirections.actionItineraryExplorerFragmentToMapFragment(itinerary.id)
                        )
                    }

                val deleteClickListener =
                    ItineraryRecyclerViewAdapter.OnItineraryItemClickListener { itinerary ->
                        viewModel.deleteItinerary(itinerary)
                    }

                // popola la recycler view
                adapter =
                    ItineraryRecyclerViewAdapter(itemClickListener, deleteClickListener).apply {
                        setItineraries(it)
                    }
            }

            viewModel.newItinerary.observe(viewLifecycleOwner) {
                (adapter as ItineraryRecyclerViewAdapter).addItinerary(it)
            }

            viewModel.removedIndex.observe(viewLifecycleOwner) {
                (adapter as ItineraryRecyclerViewAdapter).removeItinerary(it)
            }

            val completed = call == 1
            viewModel.getItineraries(completed)
        }

        binding.createBtn.setOnClickListener {

            // crea il dialog per la creazione di un nuovo itinerario
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Create new itinerary")

            val input = EditText(requireContext()).apply {
                hint = "Enter new itinerary name"
                inputType = InputType.TYPE_CLASS_TEXT
            }
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                val name = input.text.toString()
                val itinerary = Itinerary(name = name)
                viewModel.createItinerary(itinerary)
            }

            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            builder.show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

    companion object {
        //Gestisco la chiamata per lo storico o la lista dei to do con la variabile call, 0=to do , 1=storico
        private const val CALL = "call"

        @JvmStatic
        fun newInstance(call: Int) = ItineraryListFragment().apply {
            arguments = bundleOf(CALL to call)
        }
    }

}