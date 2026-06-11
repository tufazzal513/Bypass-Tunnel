package com.neko.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.graphics.shapes.toPath
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.handler.MmkvManager

class ProfileBannerImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val TAG_PROFILE_DEFAULT = "DEFAULT_BANNER_PROFILE"
    private var currentShapeKey: String? = null

    private val shapePath = Path()
    private val scaleMatrix = Matrix()

    // --- PROPERTI UNTUK BORDER ---
    private var borderWidth: Float = 0f
    private var borderColor: Int = Color.TRANSPARENT
    private var borderPaint: Paint? = null

    private val shapeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action == AppConfig.BROADCAST_ACTION_PROFILE_BANNER_CHANGED) {
                post {
                    checkAndUpdateShape()
                    loadImage()
                }
            }
        }
    }

    init {
        // 1. Baca Custom Attributes dari XML
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs, 
                R.styleable.M3ShapeImageView, // Sesuaikan dengan nama di attrs.xml
                defStyleAttr, 
                0
            )
            
            borderColor = typedArray.getColor(R.styleable.M3ShapeImageView_shapeBorderColor, Color.TRANSPARENT)
            borderWidth = typedArray.getDimension(R.styleable.M3ShapeImageView_shapeBorderWidth, 0f)
            
            typedArray.recycle()
        }

        // 2. Siapkan Paint untuk menggambar garis (Stroke)
        if (borderWidth > 0f && borderColor != Color.TRANSPARENT) {
            borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = borderColor
                strokeWidth = borderWidth
                strokeJoin = Paint.Join.ROUND // Bikin sudut garisnya mulus
                strokeCap = Paint.Cap.ROUND
            }
        }

        scaleType = ScaleType.CENTER_CROP
        setLayerType(View.LAYER_TYPE_NONE, null)
        elevation = 0f
        outlineProvider = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            val filter = IntentFilter(AppConfig.BROADCAST_ACTION_PROFILE_BANNER_CHANGED)
            ContextCompat.registerReceiver(
                context, shapeChangeReceiver, filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            checkAndUpdateShape()
            loadImage()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!isInEditMode) {
            try { context.unregisterReceiver(shapeChangeReceiver) } catch (_: Exception) {}
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus && !isInEditMode) {
            checkAndUpdateShape()
            loadImage()
        }
    }

    private fun resolveShapeKey(): String =
        MmkvManager.decodeSettingsString(AppConfig.PREF_PROFILE_BANNER_SHAPE)
            ?: AppConfig.PREF_PROFILE_BANNER_SHAPE_DEFAULT

    private fun checkAndUpdateShape() {
        val newKey = resolveShapeKey()
        if (currentShapeKey != newKey) {
            currentShapeKey = newKey
            val polygon = M3ShapeFactory.createM3Shape(newKey)
            shapePath.rewind()
            shapePath.set(polygon.toPath())
            
            updatePathScale()
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updatePathScale()
    }

    private fun updatePathScale() {
        if (width == 0 || height == 0 || shapePath.isEmpty) return

        val bounds = RectF()
        shapePath.computeBounds(bounds, true)

        scaleMatrix.reset()
        
        // --- TRIK BORDER ---
        // Kurangi area yang bisa dipakai sebesar tebal border agar tidak terpotong layar
        val targetWidth = width - borderWidth
        val targetHeight = height - borderWidth
        val halfStroke = borderWidth / 2f

        val scaleX = targetWidth / bounds.width()
        val scaleY = targetHeight / bounds.height()
        
        scaleMatrix.postScale(scaleX, scaleY)
        
        // Geser (Translate) bentuknya ke tengah memperhitungkan setengah ketebalan border
        scaleMatrix.postTranslate(
            (-bounds.left * scaleX) + halfStroke, 
            (-bounds.top * scaleY) + halfStroke
        )
        
        shapePath.transform(scaleMatrix)
    }

    override fun onDraw(canvas: Canvas) {
        // 1. Potong kanvas untuk gambar utamanya
        canvas.save()
        if (!shapePath.isEmpty) {
            canvas.clipPath(shapePath)
        }
        // 2. Gambar bitmap/glide-nya
        super.onDraw(canvas)
        canvas.restore()

        // 3. Gambar garis tepi (Border) di atas gambar yang sudah dipotong
        borderPaint?.let { paint ->
            if (!shapePath.isEmpty) {
                canvas.drawPath(shapePath, paint)
            }
        }
    }

    private fun loadImage() {
        try {
            val uriString = MmkvManager.decodeSettingsString(AppConfig.PREF_PROFILE_BANNER_URI)
            val targetTag = if (uriString.isNullOrEmpty()) TAG_PROFILE_DEFAULT else uriString

            if (this.tag != targetTag) {
                if (!uriString.isNullOrEmpty()) {
                    val savedUri = Uri.parse(uriString)
                    Glide.with(this)
                        .asBitmap()
                        .load(savedUri)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .dontAnimate()
                        .error(R.drawable.uwu_banner_profile)
                        .into(this)
                } else {
                    loadDefault()
                }
                this.tag = targetTag
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (this.tag != TAG_PROFILE_DEFAULT) {
                loadDefault()
                this.tag = TAG_PROFILE_DEFAULT
            }
        }
    }

    private fun loadDefault() {
        Glide.with(this).clear(this)
        setImageResource(R.drawable.uwu_banner_profile)
    }
}
