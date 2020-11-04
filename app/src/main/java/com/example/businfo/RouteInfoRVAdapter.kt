package com.example.businfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.businfo.model.RouteInfo
import kotlinx.android.synthetic.main.view_route_info_item.view.*

/**
 * Created by Deepak Mandhani on 2020-01-16.
 */
class RouteInfoRVAdapter(
    private val context: Context, private var routeInfoList: List<RouteInfo>,
    routeInfoCallback: (id: String) -> Unit
) : RecyclerView.Adapter<RouteInfoRVAdapter.RouteInfoVH>() {

    override fun getItemCount(): Int = routeInfoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteInfoVH =
        RouteInfoVH(
            LayoutInflater.from(context).inflate(
                R.layout.view_route_info_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RouteInfoVH, position: Int) {
        val routeInfo = routeInfoList[position]
        holder.itemView.name.text = routeInfo.name
        holder.itemView.route.text = routeInfo.source + " - " + routeInfo.destination
        holder.itemView.duration.text = routeInfo.tripDuration
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(itemClickListener)
    }

    private val itemClickListener = View.OnClickListener {
        val position = it.tag as Int
        routeInfoCallback(routeInfoList[position].id)
    }

    class RouteInfoVH(itemView: View) : RecyclerView.ViewHolder(itemView)
}