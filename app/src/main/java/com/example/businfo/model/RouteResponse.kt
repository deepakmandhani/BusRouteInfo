package com.example.businfo.model

/**
 * Created by Deepak Mandhani on 2020-01-16.
 */
data class RouteResponse(
    val routeInfo: List<RouteInfo>,
    val routeTimings: Map<String, List<RouteTimings>>
)