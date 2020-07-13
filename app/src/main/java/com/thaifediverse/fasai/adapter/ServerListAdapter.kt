package com.thaifediverse.fasai.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.thaifediverse.fasai.R

class ServerListAdapter(var context: Context, var instances: List<String>? = null) : RecyclerView.Adapter<ServerHolder>() {

    private var listener: OnClickServer?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerHolder {
        return ServerHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_server_channel, parent, false))
    }

    override fun getItemCount(): Int = instances!!.size

    override fun onBindViewHolder(holder: ServerHolder, position: Int) {
        holder.serverName.text = instances!![position]
        Glide.with(context)
                .load(
                        when(instances!![position]){
                           "mastodon.in.th" -> ContextCompat.getDrawable(context, R.drawable.mastodon_in_th)
                            "pleroma.in.th" -> ContextCompat.getDrawable(context, R.drawable.pleromainth)
                            else -> ContextCompat.getDrawable(context, R.drawable.logo_fasai)
                        })
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.serverLogo)
        holder.root.setOnClickListener { listener?.onSelectServer(instances!![position]) }
    }

    fun setOnClickServer(onClickServer: OnClickServer?){
        this.listener = onClickServer
    }

    interface OnClickServer{
        fun onSelectServer(urlInstance: String?)
    }

}

class ServerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val root:RelativeLayout = itemView.findViewById(R.id.rootItem)
    val serverLogo:ImageView = itemView.findViewById(R.id.serverLogo)
    val serverName: TextView = itemView.findViewById(R.id.tvServerName)
}