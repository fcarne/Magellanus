package com.unibg.magellanus.app.route.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unibg.magellanus.app.common.observeOnce
import com.unibg.magellanus.app.databinding.FragmentRoutedPoiListBinding
import com.unibg.magellanus.app.route.model.RouteRepositoryImpl
import com.unibg.magellanus.app.route.model.RoutedPOI
import com.unibg.magellanus.app.route.model.network.MatrixAPI
import com.unibg.magellanus.app.route.model.network.RouteAPI
import com.unibg.magellanus.app.route.viewmodel.RoutedPOIListViewModel
import com.unibg.magellanus.app.auth.impl.FirebaseAuthenticationProvider
import kotlinx.coroutines.launch

class RoutedPOIListFragment : Fragment() {

    private var columnCount = 1

    private lateinit var itineraryId: String
    private lateinit var current: MutableList<RoutedPOI>
    private val viewModel by viewModels<RoutedPOIListViewModel> {
        itineraryId = args.itineraryId

        val coordinates = args.coordinates
        val names = args.names

        current = mutableListOf()
        for (i in coordinates.indices)
            current.add(RoutedPOI(name = names[i], coordinates = coordinates[i]))

        val provider = FirebaseAuthenticationProvider()
        val api = RouteAPI.create(provider)
        val matrixAPI = MatrixAPI.create(requireContext().cacheDir)
        val repository = RouteRepositoryImpl(api, matrixAPI)

        RoutedPOIListViewModel.Factory(repository)
    }

    private lateinit var navController: NavController
    private val args: RoutedPOIListFragmentArgs by navArgs()

    // helper per le funzioni di drag e swipe della recycler view
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    recyclerView.adapter as RoutedPOIRecyclerViewAdapter
                    val from = viewHolder.bindingAdapterPosition
                    val to = target.bindingAdapterPosition

                    viewModel.moveRoutedPOI(from, to)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }
            }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRoutedPoiListBinding.inflate(inflater, container, false)

        // Set the adapter
        with(binding.list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }

            itemTouchHelper.attachToRecyclerView(this)

            viewModel.poiRoute.observeOnce(viewLifecycleOwner) {
                // setta i listener degli item
                val deleteClickListener =
                    RoutedPOIRecyclerViewAdapter.OnRoutedPOIItemClickListener { poi ->
                        viewModel.removePOI(poi)
                    }

                // popola la recycler view
                adapter =
                    RoutedPOIRecyclerViewAdapter(deleteClickListener).apply {
                        setRoute(it)
                    }

                viewModel.poiRoute.observe(viewLifecycleOwner) {
                    (adapter as RoutedPOIRecyclerViewAdapter).setRoute(it)
                }
            }

            viewModel.removedIndex.observe(viewLifecycleOwner) {
                (adapter as RoutedPOIRecyclerViewAdapter).removeRoutedPOI(it)
            }

            viewModel.movedToIndex.observe(viewLifecycleOwner) {
                (adapter as RoutedPOIRecyclerViewAdapter).moveRoutedPOI(
                    viewModel.movedFromIndex.value!!, it
                )
            }

            viewModel.updated.observe(viewLifecycleOwner) {
                viewModel.movedFromIndex.value?.let { from ->
                    viewModel.movedToIndex.value?.let { to ->
                        adapter?.notifyItemRangeChanged(
                            from,
                            to,
                        )
                    }
                }
                viewModel.removedIndex.value?.let { removed -> adapter?.notifyItemChanged(removed - 1) }
            }

            viewModel.updateRoute(itineraryId, current)
        }

        binding.autogenerateBtn.setOnClickListener {
            viewModel.autogenerate()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().lifecycleScope.launch {
            viewModel.saveChanges()
        }
    }
}