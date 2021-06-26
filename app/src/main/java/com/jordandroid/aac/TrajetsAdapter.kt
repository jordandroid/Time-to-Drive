package com.jordandroid.aac;

import android.annotation.SuppressLint;
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class TrajetsAdapter (private val activity: Activity, trajetsList: List<Trajet>) : BaseAdapter() {

    private var trajetsList = ArrayList<Trajet>()

    init {
        this.trajetsList =  trajetsList as ArrayList
    }

    override fun getCount(): Int {
        return trajetsList.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }


    private class ViewHolder(row: View?) {
        var title : TextView? = null
        var date : TextView? = null
        var distance : TextView? = null

        init {
            this.title = row?.findViewById<TextView>(R.id.tv_title)
            this.date = row?.findViewById<TextView>(R.id.tv_date)
            this.distance = row?.findViewById<TextView>(R.id.tv_distance)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.custom_list, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.title?.text = trajetsList[position].trajet
        viewHolder.date?.text = trajetsList[position].date
        viewHolder.distance?.text = trajetsList[position].distance.toString() + " " + settings.unit

        return view as View
    }

}


