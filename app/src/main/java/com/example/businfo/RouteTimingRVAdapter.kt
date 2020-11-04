package com.example.businfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.businfo.model.RouteTimings
import kotlinx.android.synthetic.main.view_route_timing_item.view.*

/**
 * Created by Deepak Mandhani on 2020-01-16.
 */
class RouteTimingRVAdapter(
    private val context: Context,
    private var routeTimingList: List<RouteTimings>
) : RecyclerView.Adapter<RouteTimingRVAdapter.RouteTimingVH>() {

    override fun getItemCount(): Int = routeTimingList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteTimingVH =
        RouteTimingVH(
            LayoutInflater.from(context).inflate(
                R.layout.view_route_timing_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RouteTimingVH, position: Int) {
        val routeTiming = routeTimingList[position]
        holder.itemView.start_time_value.text = routeTiming.tripStartTime
        holder.itemView.seat_value.text =
            routeTiming.avaiable.toString() + "/" + routeTiming.totalSeats
    }

    fun updateTimings(list: List<RouteTimings>) {
        routeTimingList = list
        notifyDataSetChanged()
    }

    class RouteTimingVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}