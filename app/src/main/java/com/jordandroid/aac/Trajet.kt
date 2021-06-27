package com.jordandroid.aac

public class Trajet : Comparable<Trajet>{
    lateinit var date : String
    lateinit var trajet : String
    lateinit var distance :  Integer
    var ID : Long = -1

    fun toPrint(): String {
        return "$date : $trajet :  $distance ${CustomSettings.unit}"
    }

    override fun toString(): String {
        return "$trajet; $date ; $distance "
    }

    override fun compareTo(other: Trajet): Int {
        try {
            val list = this.date.split("/")
            val day = list[0].trim().toInt()
            val month = list[1].trim().toInt()
            val year = list[2].trim().toInt()

            val listOther = other.date.split("/")
            val dayOther = listOther[0].trim().toInt()
            val monthOther = listOther[1].trim().toInt()
            val yearOther = listOther[2].trim().toInt()

            if (year > yearOther) return 1
            if (year < yearOther) return -1
            if (month > monthOther) return 1
            if (month < monthOther) return -1
            if (day > dayOther) return 1
            if (day < dayOther) return -1
            return 0
        }
        catch (e : Exception){
            println(" Date does not respect the expected format")
            return -1
        }
    }

}