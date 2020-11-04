package com.example.businfo.model

/**
 * Created by Deepak Mandhani on 2020-01-16.
 */
data class RouteInfo(
    val id: String,
    val name: String,
    val source: String,
    val tripDuration: String,
    val destination: String,
    val icon: String
)