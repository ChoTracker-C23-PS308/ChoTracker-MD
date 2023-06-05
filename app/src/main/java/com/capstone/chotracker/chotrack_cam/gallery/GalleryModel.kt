package com.capstone.chotracker.chotrack_cam.gallery

import android.net.Uri

data class GalleryModel (
    var mMediaUri: Uri?,
    var mMediaType: Int,
    var mMediaDate: String
){
    var isSelected = false
}