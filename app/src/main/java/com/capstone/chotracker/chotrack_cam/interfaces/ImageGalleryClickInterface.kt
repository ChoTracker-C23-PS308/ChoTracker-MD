package com.capstone.chotracker.chotrack_cam.interfaces

import com.capstone.chotracker.chotrack_cam.gallery.GalleryModel

interface ImageGalleryClickInterface {
    fun onImageGalleryClick(media: GalleryModel)
    fun onImageGalleryLongClick(media: GalleryModel, intentFrom: String)
}