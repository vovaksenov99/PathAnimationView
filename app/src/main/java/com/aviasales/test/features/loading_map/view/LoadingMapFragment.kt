package com.aviasales.test.features.loading_map.view

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aviasales.test.R
import com.aviasales.test.core.utils.px
import com.aviasales.test.core.views.LoadingOverlayView
import com.aviasales.test.features.loading_map.logic.LoadingMapFragmentState
import com.aviasales.test.features.loading_map.logic.LoadingMapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds


class LoadingMapFragment : Fragment() {

    companion object {

        private val ROUTE_PADDING_DP = 80.px
        private val defaultViewCurveStyle = Paint().apply {
            color = Color.BLACK
            alpha = 150
            strokeWidth = 10f
            isAntiAlias = true
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f)
        }

        private const val FRAGMENT_ARGS = "FRAGMENT_ARGS"

        fun newInstance(state: LoadingMapFragmentState) = LoadingMapFragment().apply {
            arguments = Bundle().apply {
                putParcelable(FRAGMENT_ARGS, state)
            }
        }

    }

    private val viewModel: LoadingMapViewModel by viewModels()

    private val state by lazy {
        arguments?.getParcelable<LoadingMapFragmentState>(FRAGMENT_ARGS)
            ?: throw Exception("Oh, my. Null state! Fire this developer immediately!")
    }

    private var loadingOverlayView: LoadingOverlayView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (childFragmentManager.findFragmentById(R.id.fragment_map_view) as? SupportMapFragment)?.apply {
            getMapAsync(::onMapReady)
        }
        loadingOverlayView =
            view.findViewById<LoadingOverlayView>(R.id.fragment_map_overlay).apply {
                setCurveStyle(defaultViewCurveStyle)
            }
    }

    private fun onMapReady(map: GoogleMap) {
        map.uiSettings.apply {
            isScrollGesturesEnabled = false
            isScrollGesturesEnabledDuringRotateOrZoom = false
            isZoomControlsEnabled = false
            isZoomGesturesEnabled = false
            isTiltGesturesEnabled = false
            isCompassEnabled = false
            isRotateGesturesEnabled = false
        }
        val bound = LatLngBounds.Builder().apply {
            include(state.fromPoint)
            include(state.toPoint)
        }.build()
        
        map.setOnMapLoadedCallback {
            map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bound,
                    ROUTE_PADDING_DP
                ),
                object : GoogleMap.CancelableCallback {

                    override fun onFinish() {
                        loadingOverlayView?.setStartPoints(
                            map.projection.toScreenLocation(state.fromPoint),
                            map.projection.toScreenLocation(state.toPoint)
                        )
                        loadingOverlayView?.setPointsNames(state.fromName, state.toName)
                    }

                    override fun onCancel() {}

                })
        }
    }

}
