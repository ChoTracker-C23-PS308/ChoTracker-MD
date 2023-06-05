package com.capstone.chotracker.chotrack_cam

import java.io.Serializable

class ChotrackCamOptions : Serializable {

    var maxCount = 1
    var allowFrontCamera = true
    var preSelectedMediaList = ArrayList<String>()

    companion object{
        @JvmStatic
        fun init(): ChotrackCamOptions{
            return ChotrackCamOptions()
        }
    }
}