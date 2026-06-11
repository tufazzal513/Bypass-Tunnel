package com.neko.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.graphics.shapes.toPath
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.util.getColorAttr

class DynamicShapeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var currentShapeKey: String? = null
    private var customBgColor: Int? = null

    private val shapePath = Path()
    private val scaleMatrix = Matrix()

    // --- PROPERTI UNTUK BORDER ---
    private var borderWidth: Float = 0f
    private var borderColor: Int = Color.TRANSPARENT
    private var borderPaint: Paint? = null

    private val shapeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action == AppConfig.BROADCAST_ACTION_ICON_SHAPE_CHANGED) {
                val newKey = MmkvManager.decodeSettingsString(AppConfig.PREF_ICON_SHAPE)
                    ?: AppConfig.PREF_ICON_SHAPE_DEFAULT
                applyShape(newKey)
            }
        }
    }

    init {
        // 1. Baca Atribut Kustom dari XML
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs, 
                R.styleable.M3ShapeImageView, // Merujuk ke <declare-styleable name="M3ShapeImageView">
                defStyleAttr, 
                0
            )
            
            // Ambil warna background jika ada
            if (typedArray.hasValue(R.styleable.M3ShapeImageView_shapeBackgroundColor)) {
                customBgColor = typedArray.getColor(
                    R.styleable.M3ShapeImageView_shapeBackgroundColor, 
                    0
                )
            }
            
            // Ambil warna dan ketebalan border
            borderColor = typedArray.getColor(R.styleable.M3ShapeImageView_shapeBorderColor, Color.TRANSPARENT)
            borderWidth = typedArray.getDimension(R.styleable.M3ShapeImageView_shapeBorderWidth, 0f)
            
            typedArray.recycle()
        }

        // 2. Siapkan Paint untuk Border
        if (borderWidth > 0f && borderColor != Color.TRANSPARENT) {
            borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = borderColor
                strokeWidth = borderWidth
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
        }

        scaleType = ScaleType.CENTER_CROP
        loadColorBitmap()
    }

    private fun loadColorBitmap() {
        try {
            val color = customBgColor ?: context.getColorAttr(androidx.appcompat.R.attr.colorPrimary)
            val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(color)
            setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            val savedKey = MmkvManager.decodeSettingsString(AppConfig.PREF_ICON_SHAPE)
                ?: AppConfig.PREF_ICON_SHAPE_DEFAULT
            applyShape(savedKey)

            val filter = IntentFilter(AppConfig.BROADCAST_ACTION_ICON_SHAPE_CHANGED)
            ContextCompat.registerReceiver(
                context, shapeChangeReceiver, filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
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
            val savedKey = MmkvManager.decodeSettingsString(AppConfig.PREF_ICON_SHAPE)
                ?: AppConfig.PREF_ICON_SHAPE_DEFAULT
            applyShape(savedKey)
        }
    }

    private fun applyShape(shapeKey: String) {
        if (currentShapeKey != shapeKey) {
            currentShapeKey = shapeKey
            
            val polygon = M3ShapeFactory.createM3Shape(shapeKey)
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
        
        // --- TRIK KOMPENSASI BORDER ---
        val targetWidth = width - borderWidth
        val targetHeight = height - borderWidth
        val halfStroke = borderWidth / 2f

        val scaleX = targetWidth / bounds.width()
        val scaleY = targetHeight / bounds.height()
        
        scaleMatrix.postScale(scaleX, scaleY)
        scaleMatrix.postTranslate(
            (-bounds.left * scaleX) + halfStroke, 
            (-bounds.top * scaleY) + halfStroke
        )
        
        shapePath.transform(scaleMatrix)
    }

    override fun onDraw(canvas: Canvas) {
        // 1. Potong kanvas
        canvas.save()
        if (!shapePath.isEmpty) {
            canvas.clipPath(shapePath)
        }
        // 2. Render bitmap / warna background
        super.onDraw(canvas)
        canvas.restore()

        // 3. Gambar garis tepi (Border) jika aktif
        borderPaint?.let { paint ->
            if (!shapePath.isEmpty) {
                canvas.drawPath(shapePath, paint)
            }
        }
    }
}
