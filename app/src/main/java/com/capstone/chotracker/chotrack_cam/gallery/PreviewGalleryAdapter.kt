package com.capstone.chotracker.chotrack_cam.gallery

import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.capstone.chotracker.chotrack_cam.interfaces.ImageGalleryClickInterface
import com.capstone.chotracker.databinding.RecyclerItemMediaBinding
import com.capstone.chotracker.utils.convertDpToPixel

class PreviewGalleryAdapter(val mMediaList: ArrayList<GalleryModel>, val mInterface: ImageGalleryClickInterface, private val mContext: Context): RecyclerView.Adapter<PreviewGalleryAdapter.MediaViewHolder>() {

    var maxCount = 0
    private var glide: RequestManager? = null
    private val options: RequestOptions
    private var size = 0f
    private val margin = 3
    private var padding = 0

    init {
        size = convertDpToPixel(72f, mContext) - 2
        padding = (size / 3.5).toInt()
        options = RequestOptions().override(300).transform(CenterCrop())
            .transform(FitCenter())
        try {
            if(!(mContext as AppCompatActivity).isFinishing) glide = Glide.with(mContext)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val mBinding = RecyclerItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return mMediaList.size
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(holder.adapterPosition)
    }

    var imageCount = 0

    inner class MediaViewHolder(private val mBinding: RecyclerItemMediaBinding): RecyclerView.ViewHolder(mBinding.root) {

        fun bind(position: Int){
            val media = mMediaList[position]
            if (media.mMediaUri == null){
                itemView.visibility = View.GONE
                itemView.layoutParams = FrameLayout.LayoutParams(0,0)
            }else {
                itemView.visibility = View.VISIBLE
                val layoutParams = FrameLayout.LayoutParams(size.toInt(), size.toInt())

                if (position == 0) {
                    layoutParams.setMargins(-(margin / 2), margin, margin, margin)
                } else {
                    layoutParams.setMargins(margin, margin, margin, margin)
                }

                itemView.layoutParams = layoutParams

                glide?.load(media.mMediaUri)
                    ?.apply(options)
                    ?.into(mBinding.imageView)


                if (media.isSelected) mBinding.imageViewSelection.visibility = View.VISIBLE
                else mBinding.imageViewSelection.visibility = View.GONE

                itemView.setOnClickListener {
                    if (imageCount == 0) {
                        media.isSelected = !media.isSelected
                        mInterface.onImageGalleryClick(media)
                    }
                    else if (imageCount < maxCount || media.isSelected){
                        media.isSelected = !media.isSelected
                        notifyItemChanged(position)
                        if (media.isSelected) imageCount++ else imageCount--
                        mInterface.onImageGalleryLongClick(media, this@PreviewGalleryAdapter::class.java.simpleName)
                    }else{
                        Toast.makeText(mContext, "Cannot add more than $maxCount items",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                itemView.setOnLongClickListener {
                    if (imageCount == 0) {
                        media.isSelected = true
                        notifyItemChanged(position)
                        imageCount++
                        mInterface.onImageGalleryLongClick(media, this@PreviewGalleryAdapter::class.java.simpleName)
                    }
                    true
                }
            }

        }
    }
}