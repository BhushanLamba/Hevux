package com.softbrain.hevix.utils

object Constants {
    private val weekDaysList = ArrayList<String>()

    private val statusList = ArrayList<String>()


    fun getWeekDaysList(): ArrayList<String> {
        if (weekDaysList.isEmpty()) {
            weekDaysList.add("Monday")
            weekDaysList.add("Tuesday")
            weekDaysList.add("Wednesday")
            weekDaysList.add("Thursday")
            weekDaysList.add("Friday")
            weekDaysList.add("Saturday")
            weekDaysList.add("Sunday")
        }

        return weekDaysList
    }


    fun getStatusList(): ArrayList<String> {
        if (statusList.isEmpty()) {
            statusList.add("ALL")
            statusList.add("FINAL")
            statusList.add("DUE")
        }

        return statusList
    }
}