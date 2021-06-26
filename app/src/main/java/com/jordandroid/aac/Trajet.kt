package com.jordandroid.aac

import java.lang.Exception
import java.util.*

public class Trajet : Comparable<Trajet>{
    lateinit var date : String
    lateinit var trajet : String
    lateinit var distance :  Integer
    var ID : Long = -1

    fun toPrint(): String {
        return "$date : $trajet :  $distance ${settings.unit}"
    }

    override fun toString(): String {
        return "$trajet; $date ; $distance "
    }

    override fun compareTo(other: Trajet): Int {
        try {
            var list = this.date.split("/")
            var day = list[0].trim().toInt()
            var month = list[1].trim().toInt()
            var year = list[2].trim().toInt()

            var listOther = other.date.split("/")
            var dayOther = listOther[0].trim().toInt()
            var monthOther = listOther[1].trim().toInt()
            var yearOther = listOther[2].trim().toInt()

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