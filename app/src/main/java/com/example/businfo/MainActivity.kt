package com.example.businfo

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.businfo.model.RouteInfo
import com.example.businfo.model.RouteResponse
import com.example.businfo.model.RouteTimings
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var response: RouteResponse? = null
    private var selectedRoute: RouteInfo? = null
    private var routeInfoRVAdapter: RouteInfoRVAdapter? = null
    private var routeTimingRVAdapter: RouteTimingRVAdapter? = null
    private val snapHelper = LinearSnapHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getJsonData()
        initUi()
    }

    private fun initUi() {
        val list = response?.routeInfo
        routeInfoRVAdapter = RouteInfoRVAdapter(this, list ?: emptyList()) {
            refreshRouteTimings(it)
        }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_route_info.layoutManager = layoutManager
        rv_route_info.adapter = routeInfoRVAdapter
        snapHelper.attachToRecyclerView(rv_route_info)
        initListener()

        routeTimingRVAdapter =
            RouteTimingRVAdapter(this, getRouteTimingList(selectedRoute?.id ?: ""))
        val layoutManager2 = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_route_timings.layoutManager = layoutManager2
        rv_route_timings.adapter = routeTimingRVAdapter

        startTimer()
    }

    private fun initListener() {
        rv_route_info.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    onSnapPositionChange(recyclerView)
            }
        })
    }

    private fun onSnapPositionChange(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        val snapPosition =
            snapHelper.findSnapView(layoutManager)?.let { layoutManager?.getPosition(it) }
                ?: RecyclerView.NO_POSITION
        val snapPositionChanged = response?.routeInfo?.indexOf(selectedRoute) != snapPosition
        if (snapPositionChanged && snapPosition != RecyclerView.NO_POSITION) {
            val routeInfo = getRouteInfo(snapPosition)
            routeInfo?.id?.let { refreshRouteTimings(it) }
            selectedRoute = routeInfo
        }
    }

    private fun getJsonData() {
        val responseString =
            "{     \"routeInfo\": [         {             \"id\": \"r002\",             \"name\": \"k-12\",             \"source\": \"Yashwantpur\",             \"tripDuration\":\"2hrs\",             \"destination\": \"Marathahalli\",             \"icon\": \"http://\"         }, {             \"id\": \"r003\",             \"name\": \"k-11\",             \"tripDuration\":\"45 min\",             \"source\": \"Koramangala\",             \"destination\": \"Bomanhalli\",             \"icon\": \"http://\"         }, {             \"id\": \"r004\",             \"name\": \"k-14\",             \"source\": \"E City\",             \"tripDuration\":\"1hrs\",             \"destination\": \"Silk Board\",             \"icon\": \"http://\"         }, {             \"id\": \"r001\",             \"name\": \"R-1\",             \"source\": \"Marathahalli\",             \"tripDuration\":\"2hrs\",             \"destination\": \"E City\",             \"icon\": \"http://\"         }, {             \"id\": \"r005\",             \"name\": \"G-12\",             \"tripDuration\":\"2hrs\",             \"source\": \"Koramangala\",             \"destination\": \"E City\",             \"icon\": \"http://\"         }     ],     \"routeTimings\": {         \"r002\": [{                 \"totalSeats\": 13,                 \"avaiable\": 5,                 \"tripStartTime\": \"14:55\"             }, {                 \"totalSeats\": 13,                 \"avaiable\": 0,                 \"tripStartTime\": \"15:00\"             },             {                 \"totalSeats\": 13,                 \"avaiable\": 1,                 \"tripStartTime\": \"15:05\"             }         ],         \"r005\": [{                 \"totalSeats\": 13,                 \"avaiable\": 5,                 \"tripStartTime\": \"14:55\"             }, {                 \"totalSeats\": 13,                 \"avaiable\": 0,                 \"tripStartTime\": \"15:00\"             },             {                 \"totalSeats\": 13,                 \"avaiable\": 1,                 \"tripStartTime\": \"15:05\"             }         ],         \"r001\": [],         \"r004\": [{                 \"totalSeats\": 13,                 \"avaiable\": 5,                 \"tripStartTime\": \"14:55\"             }, {                 \"totalSeats\": 13,                 \"avaiable\": 0,                 \"tripStartTime\": \"15:00\"             },             {                 \"totalSeats\": 13,                 \"avaiable\": 1,                 \"tripStartTime\": \"15:05\"             }         ],         \"r003\": [{                 \"totalSeats\": 12,                 \"avaiable\": 10,                 \"tripStartTime\": \"15:55\"             }, {                 \"totalSeats\": 12,                 \"avaiable\": 9,                 \"tripStartTime\": \"16:00\"             },             {                 \"totalSeats\": 12,                 \"avaiable\": 1,                 \"tripStartTime\": \"16:05\"             }         ]     } } ";
        response = Gson().fromJson(responseString, RouteResponse::class.java)
        selectedRoute = response?.routeInfo?.get(0)
    }

    private fun getRouteTimingList(id: String) =
        response?.routeTimings?.get(id) ?: emptyList()

    private fun getRouteInfo(index: Int) =
        response?.routeInfo?.get(index)

    private fun refreshRouteTimings(id: String) {
        stopTimer()
        startTimer()
        val list = getFutureRouteTimings(id)
        if (list?.isNullOrEmpty())
            iv_no_bus_available.visibility = View.VISIBLE
        else
            iv_no_bus_available.visibility = View.GONE
        routeTimingRVAdapter?.updateTimings(list)
    }

    private fun getFutureRouteTimings(id: String): List<RouteTimings> {
        val routeTimings = getRouteTimingList(id)
        var today = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm")
        today = dateFormat.parse(dateFormat.format(today))

        val futureRouteTimings = mutableListOf<RouteTimings>()
        routeTimings.forEach {
            val tripStartTime = dateFormat.parse(it.tripStartTime)
            if (tripStartTime.after(today))
                futureRouteTimings.add(it)
        }
        return futureRouteTimings
    }

    private val timer = object: CountDownTimer(60000, 60000) {
        override fun onFinish() {
            selectedRoute?.id?.let { refreshRouteTimings(it) }
        }

        override fun onTick(p0: Long) = Unit
    }

    private fun startTimer() = timer.start()

    private fun stopTimer() = timer.cancel()

}