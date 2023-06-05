package com.capstone.chotracker.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

object MediaConstants {

    private var IMAGE_PROJECTION = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.PARENT,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.DATE_MODIFIED,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.TITLE
    )

    var IMAGE_URI: Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Files.getContentUri(
            MediaStore.VOLUME_EXTERNAL)
        else MediaStore.Files.getContentUri("external")

    private var IMAGE_SELECTION = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)

    private var IMAGE_ORDER_BY = MediaStore.Images.Media.DATE_MODIFIED + " DESC"

    fun getImageVideoCursor(
        context: Context,
    ): Cursor? {
        return context.contentResolver
            .query(
                IMAGE_URI,
                IMAGE_PROJECTION,
                IMAGE_SELECTION,
                null,
                IMAGE_ORDER_BY
            )
    }
}