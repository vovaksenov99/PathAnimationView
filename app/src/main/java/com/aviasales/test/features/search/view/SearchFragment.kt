package com.aviasales.test.features.search.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aviasales.test.R
import com.aviasales.test.di.DaggerAppComponent
import com.aviasales.test.features.loading_map.view.LoadingMapFragment
import com.aviasales.test.features.search.logic.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    companion object {

        fun newInstance() = SearchFragment()

    }

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerAppComponent
            .builder()
            .build()
            .inject(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_search_button.setOnClickListener {
            viewModel.search(
                fragment_search_from.text.toString(),
                fragment_search_to.text.toString()
            )
        }
        initObservers()
    }

    private fun initObservers() {
        viewModel.globalError.observe(viewLifecycleOwner, { errorId ->
            if (errorId != null) {
                fragment_search_error.text = getString(errorId)
            }
            fragment_search_error.isVisible = errorId != null
        })
        viewModel.fromFieldError.observe(viewLifecycleOwner, { errorId ->
            fragment_search_from.error = getString(errorId)
        })
        viewModel.toFieldError.observe(viewLifecycleOwner, { errorId ->
            fragment_search_to.error = getString(errorId)
        })
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            fragment_search_button.text =
                getString(if (isLoading) R.string.loading else R.string.search)
            fragment_search_button.isEnabled = !isLoading
            fragment_search_to.isEnabled = !isLoading
            fragment_search_from.isEnabled = !isLoading
        })
        viewModel.searchResult.observe(viewLifecycleOwner, { state ->
            requireActivity()
                .supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.container, LoadingMapFragment.newInstance(state))
                .commit()
        })
    }

}
