package com.jordandroid.aac;

import android.annotation.SuppressLint;
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrajetsRecyclerViewAdapter(private val trajetsList : ArrayList<Trajet>)  : RecyclerView.Adapter<TrajetsRecyclerViewAdapter.TrajetHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrajetsRecyclerViewAdapter.TrajetHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.custom_list, parent, false)
        return TrajetHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: TrajetsRecyclerViewAdapter.TrajetHolder, position: Int) {
        val itemTrajets = trajetsList[position]
        holder.bind(itemTrajets)

    }
    fun getItem(i : Int): Trajet {
        return trajetsList[i]
    }
    override fun getItemCount(): Int {
        return trajetsList.size
    }

    class TrajetHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        var title : TextView? = null
        var date : TextView? = null
        var distance : TextView? = null

        init {
            this.title = view.findViewById<TextView>(R.id.tv_title)
            this.date = view.findViewById<TextView>(R.id.tv_date)
            this.distance = view.findViewById<TextView>(R.id.tv_distance)
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
           // DO NOTHING
        }

       fun bind(trajet: Trajet){
           title!!.text = trajet.trajet
           date!!.text = trajet.date
           distance!!.text = trajet.distance.toString() + " " + settings.unit
       }
    }

}


