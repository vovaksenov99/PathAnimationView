package com.aviasales.test.features.search.logic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aviasales.test.R
import com.aviasales.test.core.utils.SingleLiveEvent
import com.aviasales.test.domain.search.SearchUseCase
import com.aviasales.test.features.loading_map.logic.LoadingMapFragmentState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel : ViewModel() {

    @Inject
    lateinit var searchUseCase: SearchUseCase

    val isLoading = MutableLiveData(false)
    val searchResult = SingleLiveEvent<LoadingMapFragmentState>()
    val fromFieldError = SingleLiveEvent<Int>()
    val toFieldError = SingleLiveEvent<Int>()
    val globalError = MutableLiveData<Int?>()

    fun search(from: String, to: String) {
        isLoading.postValue(true)
        globalError.postValue(null)
        viewModelScope.launch(IO) {
            try {
                val fromPosition = searchUseCase.getPlaceCoordinatesByName(from)
                val toPosition = searchUseCase.getPlaceCoordinatesByName(to)
                if (fromPosition.location == null || toPosition.location == null) {
                    fromPosition.location ?: fromFieldError.postValue(R.string.wrong_city)
                    toPosition.location ?: toFieldError.postValue(R.string.wrong_city)
                    return@launch
                }
                if (toPosition == fromPosition) {
                    toFieldError.postValue(R.string.too_close_to_finish_point)
                    return@launch
                }
                searchResult.postValue(
                    LoadingMapFragmentState(
                        LatLng(fromPosition.location.lat, fromPosition.location.lon),
                        LatLng(toPosition.location.lat, toPosition.location.lon),
                        fromPosition.airportName ?: "",
                        toPosition.airportName ?: ""
                    )
                )
            } catch (exception: Exception) {
                globalError.postValue(R.string.something_went_wrong)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

}