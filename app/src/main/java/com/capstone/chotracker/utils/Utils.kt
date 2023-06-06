package com.capstone.chotracker.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import com.capstone.chotracker.R
import com.capstone.chotracker.databinding.ActivityChotrackCamBinding
import java.text.SimpleDateFormat
import java.util.*

private const val PASSWORD_MIN_LENGTH = 8

fun validateEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validatePassword(password: String): Boolean {
    return !TextUtils.isEmpty(password) && password.length >= PASSWORD_MIN_LENGTH
}


fun manipulateBottomSheetVisibility(activity: Activity, slideOffSet: Float, mBinding: ActivityChotrackCamBinding, count: Int){

    mBinding.recyclerViewInstantMedia.alpha = 1 - slideOffSet
    mBinding.constraintCheck.alpha = 1 - slideOffSet
    mBinding.constraintBottomSheetTop.alpha =     slideOffSet
    mBinding.recyclerViewBottomSheetMedia.alpha = slideOffSet

    if ((1 - slideOffSet) == 0f && mBinding.recyclerViewInstantMedia.visibility == View.VISIBLE){
        mBinding.recyclerViewInstantMedia.visibility = View.GONE
        mBinding.constraintCheck.visibility = View.GONE
    }else if(mBinding.recyclerViewInstantMedia.visibility == View.GONE && (1 - slideOffSet) > 0f){
        mBinding.recyclerViewInstantMedia.visibility = View.VISIBLE
        if (count > 0) mBinding.constraintCheck.visibility = View.VISIBLE
    }

    if (slideOffSet > 0f && mBinding.recyclerViewBottomSheetMedia.visibility == View.INVISIBLE){
        mBinding.recyclerViewBottomSheetMedia.visibility = View.VISIBLE
        mBinding.constraintBottomSheetTop.visibility = View.VISIBLE
        showStatusBar(activity)
    }else if (slideOffSet == 0f && mBinding.recyclerViewBottomSheetMedia.visibility == View.VISIBLE){
        mBinding.recyclerViewBottomSheetMedia.visibility = View.INVISIBLE
        mBinding.constraintBottomSheetTop.visibility = View.GONE
        hideStatusBar(activity)
    }
}

fun hideStatusBar(appCompatActivity: Activity) {
    synchronized(appCompatActivity) {
        appCompatActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun showStatusBar(appCompatActivity: Activity) {
    synchronized(appCompatActivity) {
        appCompatActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun getStringDate(context: Context, time: Long): String{
    //
    val date = Date(time * 1000)
    val calendar = Calendar.getInstance()
    calendar.time = date

    val lastMonth = Calendar.getInstance()
    val lastWeek = Calendar.getInstance()
    val recent = Calendar.getInstance()
    lastMonth.add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_MONTH)
    lastWeek.add(Calendar.DAY_OF_MONTH, -7)
    recent.add(Calendar.DAY_OF_MONTH, -2)
    return if (calendar.before(lastMonth)) {
        SimpleDateFormat("MMMM", Locale.ENGLISH).format(date)
    } else if (calendar.after(lastMonth) && calendar.before(lastWeek)) {
        context.resources.getString(R.string.last_month)
    } else if (calendar.after(lastWeek) && calendar.before(recent)) {
        context.resources.getString(R.string.last_week)
    } else {
        context.resources.getString(R.string.recent)
    }
}

fun getScreenWidth(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun convertDpToPixel(dp: Float, context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun convertPixelsToDp(px: Float, context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}
