package com.capstone.chotracker.chotrack_cam

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.webkit.MimeTypeMap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.chotracker.chotrack_cam.interfaces.PermissionCallback
import com.capstone.chotracker.databinding.ActivityChotrackCamBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.collections.ArrayList
import com.capstone.chotracker.R
import com.capstone.chotracker.chotrack_cam.gallery.BottomSheetGalleryAdapter
import com.capstone.chotracker.chotrack_cam.gallery.BottomSheetGalleryAdapter.Companion.HEADER
import com.capstone.chotracker.chotrack_cam.gallery.BottomSheetGalleryAdapter.Companion.SPAN_COUNT
import com.capstone.chotracker.chotrack_cam.gallery.GalleryModel
import com.capstone.chotracker.chotrack_cam.gallery.PreviewGalleryAdapter
import com.capstone.chotracker.chotrack_cam.interfaces.ImageGalleryClickInterface
import com.capstone.chotracker.utils.*
import com.capstone.chotracker.utils.MediaConstants.IMAGE_URI
import com.capstone.chotracker.utils.MediaConstants.getImageVideoCursor
import java.util.concurrent.Executors

@Suppress("DEPRECATION")
class ChotrackCamActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChotrackCamBinding

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF
    private var statusBarSize = 0

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var mChotrackCamOptions: ChotrackCamOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chotrack_cam)

        mChotrackCamOptions = intent?.getSerializableExtra(PICKER_OPTIONS) as ChotrackCamOptions

        binding.viewFinder.post {
            if (allPermissionsGranted()) {
                //to avoid NullPointerException for Display.getRealMetrics which comes for some cases
                Handler().postDelayed({ startCamera() }, 100)
            }
        }

        // Setup the listener for take photo button
        binding.imageViewClick.setOnClickListener {
            takePhoto()
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        window.decorView.setOnApplyWindowInsetsListener { _, insets ->
            statusBarSize = insets.systemWindowInsetTop
            insets
        }
        showStatusBar(this)
        Handler().postDelayed({ getMedia() }, 500)
        initViews()

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            bindCameraUseCases()
            setUpPinchToZoom()
            setupFlash()

        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {

        binding.imageViewChangeCamera.let {

            it.setOnClickListener {
                lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
                    CameraSelector.LENS_FACING_BACK
                } else {
                    CameraSelector.LENS_FACING_FRONT
                }
                // Re-bind use cases to update selected camera
                bindCameraUseCases()
                setUpPinchToZoom()
                setupFlash()
            }
        }

        if (!mChotrackCamOptions.allowFrontCamera) binding.imageViewChangeCamera.visibility =
            View.GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpPinchToZoom() {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1F
                val delta = detector.scaleFactor
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }
        val scaleGestureDetector = ScaleGestureDetector(this, listener)
        binding.viewFinder.setOnTouchListener { _, event ->
            binding.viewFinder.post {
                scaleGestureDetector.onTouchEvent(event)
            }
            return@setOnTouchListener true
        }
    }

    private fun setupFlash() {

        flashMode = ImageCapture.FLASH_MODE_OFF
        binding.imageViewFlash.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources, R.drawable.ic_baseline_flash_off_36, null
            )
        )

        val hasFlash = camera?.cameraInfo?.hasFlashUnit()

        if (hasFlash != null && hasFlash) binding.imageViewFlash.visibility = View.VISIBLE
        else binding.imageViewFlash.visibility = View.GONE

        binding.imageViewFlash.setOnClickListener {
            when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> {
                    binding.imageViewFlash.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources, R.drawable.ic_baseline_flash_on_36, null
                        )
                    )
                    camera?.cameraControl?.enableTorch(true)
                    flashMode = ImageCapture.FLASH_MODE_ON
                }
                ImageCapture.FLASH_MODE_ON -> {
                    binding.imageViewFlash.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources, R.drawable.ic_baseline_flash_off_36, null
                        )
                    )
                    camera?.cameraControl?.enableTorch(false)
                    flashMode = ImageCapture.FLASH_MODE_OFF
                }

            }
        }

    }

    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio()
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = binding.viewFinder.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        imageCapture?.let { imageCapture ->
            // Create output file to hold the image
            val photoFile = File(
                outputDirectory,
                SimpleDateFormat(
                    FILENAME_FORMAT, Locale.ENGLISH
                ).format(System.currentTimeMillis()) + PHOTO_EXTENSION
            )

            val metadata = ImageCapture.Metadata().apply {
                // Mirror image when using the front camera
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
            }

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

            imageCapture.takePicture(
                outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                        Log.d(TAG, "Photo capture succeeded: $savedUri")

                        val mimeType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(savedUri.toFile().extension)
                        MediaScannerConnection.scanFile(
                            this@ChotrackCamActivity,
                            arrayOf(savedUri.toFile().absolutePath),
                            arrayOf(mimeType)
                        ) { _, uri ->
                            Log.d(TAG, "Image capture scanned into media store: $uri")
                        }

                        val mPathList = ArrayList<String>()
                        mPathList.add(savedUri.toString())

                        val intent = Intent()
                        intent.putExtra(PICKED_MEDIA_LIST, mPathList)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                })

            // We can only change the foreground Drawable using API level 23+ API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Display flash animation to indicate that photo was captured
                binding.container.postDelayed({
                    binding.container.foreground = ColorDrawable(Color.WHITE)
                    binding.container.postDelayed(
                        { binding.container.foreground = null }, ANIMATION_FAST_MILLIS
                    )
                }, ANIMATION_SLOW_MILLIS)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()

    }

    private val galleryImageList = ArrayList<GalleryModel>()
    private var mInstantMediaAdapter: PreviewGalleryAdapter? = null
    private var mBottomMediaAdapter: BottomSheetGalleryAdapter? = null

    private fun getMedia() {

        CoroutineScope(Dispatchers.Main).launch {
            val cursor: Cursor? = withContext(Dispatchers.IO) {
                getImageVideoCursor(this@ChotrackCamActivity)
            }

            if (cursor != null) {

                Log.e(TAG, "getMedia: ${cursor.count}")

                val index = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                val dateIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                val typeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)

                var headerDate = ""

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(index)
                    val path = ContentUris.withAppendedId(IMAGE_URI, id)
                    val mediaType = cursor.getInt(typeIndex)
                    val longDate = cursor.getLong(dateIndex)
                    val mediaDate = getStringDate(this@ChotrackCamActivity, longDate)

                    if (!headerDate.equals(mediaDate, true)) {
                        headerDate = mediaDate
                        galleryImageList.add(GalleryModel(null, mediaType, headerDate))
                    }

                    galleryImageList.add(GalleryModel(path, mediaType, ""))
                }


                val selectedList = ArrayList<GalleryModel>()
                for (i in 0 until mChotrackCamOptions.preSelectedMediaList.size) {
                    for (j in 0 until galleryImageList.size) {
                        if (Uri.parse(mChotrackCamOptions.preSelectedMediaList[i]) == galleryImageList[j].mMediaUri) {
                            galleryImageList[j].isSelected = true
                            selectedList.add(galleryImageList[j])
                            break
                        }
                    }
                }

                mInstantMediaAdapter =
                    PreviewGalleryAdapter(galleryImageList, mMediaClickListener, this@ChotrackCamActivity)
                mInstantMediaAdapter?.maxCount = mChotrackCamOptions.maxCount
                binding.recyclerViewInstantMedia.adapter = mInstantMediaAdapter

                for (i in 0 until selectedList.size) {
                    mInstantMediaAdapter?.imageCount = mInstantMediaAdapter?.imageCount!! + 1
                    mMediaClickListener.onImageGalleryLongClick(
                        selectedList[i],
                        PreviewGalleryAdapter::class.java.simpleName
                    )
                }

                handleBottomSheet()
            }

        }
    }

    private val mMediaClickListener = object : ImageGalleryClickInterface {
        override fun onImageGalleryClick(media: GalleryModel) {
            pickImages()
        }

        override fun onImageGalleryLongClick(media: GalleryModel, intentFrom: String) {

            if (intentFrom == PreviewGalleryAdapter::class.java.simpleName) {
                if (mInstantMediaAdapter?.imageCount!! > 0) {
                    binding.textViewImageCount.text = mInstantMediaAdapter?.imageCount?.toString()
                    binding.textViewTopSelect.text = String.format(
                        getString(R.string.images_selected),
                        mInstantMediaAdapter?.imageCount?.toString()
                    )
                    showTopViews()
                } else hideTopViews()
            }


            if (intentFrom == BottomSheetGalleryAdapter::class.java.simpleName) {
                if (mBottomMediaAdapter?.imageCount!! > 0) {
                    binding.textViewImageCount.text = mBottomMediaAdapter?.imageCount?.toString()
                    binding.textViewTopSelect.text = String.format(
                        getString(R.string.images_selected),
                        mBottomMediaAdapter?.imageCount?.toString()
                    )
                    showTopViews()
                } else hideTopViews()

            }
        }

    }

    private fun pickImages() {
        val mPathList = ArrayList<String>()

        galleryImageList.map { mediaModel ->
            if (mediaModel.isSelected) {
                mPathList.add(mediaModel.mMediaUri.toString())
            }
        }

        val intent = Intent()
        intent.putExtra(PICKED_MEDIA_LIST, mPathList)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showTopViews() {
        binding.constraintCheck.visibility = View.VISIBLE
        binding.textViewOk.visibility = View.VISIBLE

        binding.imageViewCheck.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.constraintBottomSheetTop.setBackgroundColor(
                resources.getColor(
                    R.color.main_green,
                    null
                )
            )
            DrawableCompat.setTint(
                binding.imageViewBack.drawable,
                resources.getColor(R.color.white, null)
            )
        } else {
            binding.constraintBottomSheetTop.setBackgroundColor(resources.getColor(R.color.main_green))
            DrawableCompat.setTint(
                binding.imageViewBack.drawable,
                resources.getColor(R.color.white)
            )
        }
    }

    private fun hideTopViews() {
        binding.constraintCheck.visibility = View.GONE
        binding.textViewOk.visibility = View.GONE
        binding.imageViewCheck.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.constraintBottomSheetTop.setBackgroundColor(
                resources.getColor(
                    R.color.white,
                    null
                )
            )
            DrawableCompat.setTint(
                binding.imageViewBack.drawable,
                resources.getColor(R.color.black, null)
            )
        } else {
            binding.constraintBottomSheetTop.setBackgroundColor(resources.getColor(R.color.white))
            DrawableCompat.setTint(
                binding.imageViewBack.drawable,
                resources.getColor(R.color.black)
            )
        }
    }

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    private fun handleBottomSheet() {

        mBottomMediaAdapter =
            BottomSheetGalleryAdapter(galleryImageList, mMediaClickListener, this@ChotrackCamActivity)
        mBottomMediaAdapter?.maxCount = mChotrackCamOptions.maxCount

        val layoutManager = GridLayoutManager(this, SPAN_COUNT)
        binding.recyclerViewBottomSheetMedia.layoutManager = layoutManager

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (mBottomMediaAdapter?.getItemViewType(position) == HEADER) {
                    SPAN_COUNT
                } else 1
            }
        }

        binding.recyclerViewBottomSheetMedia.adapter = mBottomMediaAdapter
        binding.recyclerViewBottomSheetMedia.addItemDecoration(
            HeaderItemDecoration(
                mBottomMediaAdapter!!,
                this
            )
        )

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        var notifiedUp = false
        var notifiedDown = false

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            var oldOffSet = 0f
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

                val inRangeExpanding = oldOffSet < slideOffset
                val inRangeCollapsing = oldOffSet > slideOffset
                oldOffSet = slideOffset

                if (slideOffset == 1f) {
                    notifiedUp = false
                    notifiedDown = false
                    binding.imageViewArrowUp.visibility = View.INVISIBLE
                } else if (slideOffset == 0f) {
                    notifiedUp = false
                    notifiedDown = false
                    binding.imageViewArrowUp.visibility = View.VISIBLE
                }

                if (slideOffset > 0.6f && slideOffset < 0.8f) {
                    if (!notifiedUp && inRangeExpanding) {
                        Log.e(TAG, "onSlide 1: $slideOffset")
                        mBottomMediaAdapter?.notifyDataSetChanged()
                        notifiedUp = true

                        var count = 0
                        galleryImageList.map { mediaModel ->
                            if (mediaModel.isSelected) count++
                        }
                        mBottomMediaAdapter?.imageCount = count
                    }


                } else if (slideOffset > 0.1f && slideOffset < 0.3f) {
                    if (!notifiedDown && inRangeCollapsing) {
                        Log.e(TAG, "onSlide 2: $slideOffset")
                        mInstantMediaAdapter?.notifyDataSetChanged()
                        notifiedDown = true

                        var count = 0
                        galleryImageList.map { mediaModel ->
                            if (mediaModel.isSelected) count++
                        }
                        mInstantMediaAdapter?.imageCount = count
                        binding.textViewImageCount.text = count.toString()
                    }

                }

                val imageCount =
                    if (mInstantMediaAdapter?.imageCount!! > 0) mInstantMediaAdapter?.imageCount!! else mBottomMediaAdapter?.imageCount!!

                manipulateBottomSheetVisibility(this@ChotrackCamActivity, slideOffset, binding, imageCount)

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {/*Not Required*/
            }

        })

        binding.imageViewBack.setOnClickListener {
            bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }

        binding.constraintCheck.setOnClickListener { pickImages() }
        binding.textViewOk.setOnClickListener { pickImages() }
        binding.imageViewCheck.setOnClickListener {
            binding.constraintCheck.visibility = View.VISIBLE
            binding.textViewOk.visibility = View.VISIBLE

            binding.textViewTopSelect.text = resources.getString(R.string.tap_to_select)

            mBottomMediaAdapter?.mTapToSelect = true

            binding.imageViewCheck.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.constraintBottomSheetTop.setBackgroundColor(
                    resources.getColor(
                        R.color.main_green,
                        null
                    )
                )
                DrawableCompat.setTint(
                    binding.imageViewBack.drawable,
                    resources.getColor(R.color.white, null)
                )
            } else {
                binding.constraintBottomSheetTop.setBackgroundColor(resources.getColor(R.color.main_green))
                DrawableCompat.setTint(
                    binding.imageViewBack.drawable,
                    resources.getColor(R.color.white)
                )
            }
        }

        if (statusBarSize > 0) {
            val params =
                binding.constraintBottomSheetTop.layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(0, statusBarSize, 0, 0)
        }
        hideStatusBar(this)
    }

    override fun onBackPressed() {
        when {
            bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior?.state =
                BottomSheetBehavior.STATE_COLLAPSED
            mInstantMediaAdapter?.imageCount != null && mInstantMediaAdapter?.imageCount!! > 0 -> removeSelection()
            else -> super.onBackPressed()
        }
    }

    private fun removeSelection() {
        mInstantMediaAdapter?.imageCount = 0
        mBottomMediaAdapter?.imageCount = 0
        for (i in 0 until galleryImageList.size) galleryImageList[i].isSelected = false
        mInstantMediaAdapter?.notifyDataSetChanged()
        mBottomMediaAdapter?.notifyDataSetChanged()
        binding.constraintCheck.visibility = View.GONE
        binding.textViewOk.visibility = View.GONE
        binding.imageViewCheck.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.constraintBottomSheetTop.setBackgroundColor(
                resources.getColor(
                    R.color.white,
                    null
                )
            )
            DrawableCompat.setTint(
                binding.imageViewBack.drawable,
                resources.getColor(R.color.black, null)
            )
        } else {
            binding.constraintBottomSheetTop.setBackgroundColor(resources.getColor(R.color.white))
            DrawableCompat.setTint(
                binding.imageViewBack.drawable,
                resources.getColor(R.color.black)
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun aspectRatio(): Int {
        return AspectRatio.RATIO_4_3
    }

    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    companion object {
        private const val TAG = "Picker"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PICKER = 10
        const val PICKER_OPTIONS = "PICKER_OPTIONS"
        const val PICKED_MEDIA_LIST = "PICKED_MEDIA_LIST"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val PHOTO_EXTENSION = ".jpg"

        const val ANIMATION_FAST_MILLIS = 50L
        const val ANIMATION_SLOW_MILLIS = 100L

        @JvmStatic
        fun startPicker(activity: FragmentActivity, mChotrackCamOptions: ChotrackCamOptions) {
            ChotrackCamPermission.checkForCameraWritePermissions(
                activity,
                object : PermissionCallback {
                    override fun onPermission(approved: Boolean) {
                        val mPickerIntent = Intent(activity, ChotrackCamActivity::class.java)
                        mPickerIntent.putExtra(PICKER_OPTIONS, mChotrackCamOptions)
                        mPickerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        activity.startActivityForResult(mPickerIntent, REQUEST_CODE_PICKER)
                    }
                })
        }
    }
}
