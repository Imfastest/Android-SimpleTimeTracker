package com.example.util.simpletimetracker.feature_views.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Outline
import android.view.LayoutInflater
import android.graphics.Path
import android.os.Build
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import androidx.core.graphics.createBitmap
import androidx.core.view.isVisible

var View.visible: Boolean
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
    get() {
        return isVisible
    }

val View.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(context)

fun View.setOnClick(listener: (() -> Unit)) {
    setOnClickListener { listener.invoke() }
}

inline fun <T> View.setOnClickWith(item: T, crossinline listener: ((T) -> Unit)) {
    setOnClickListener { listener.invoke(item) }
}

fun View.setOnLongClick(listener: (() -> Unit)) {
    setOnLongClickListener {
        listener.invoke()
        true
    }
}

inline fun <T> View.setOnLongClickWith(item: T, crossinline listener: ((T) -> Unit)) {
    setOnLongClickListener {
        listener.invoke(item)
        true
    }
}

fun TabLayout.onTabSelected(func: (TabLayout.Tab) -> Unit) {
    object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
            // Do nothing
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            // Do nothing
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let(func::invoke)
        }
    }.let { addOnTabSelectedListener(it) }
}

fun AppCompatSpinner.onItemSelected(
    onNothingSelected: () -> Unit,
    onPositionSelected: (Int) -> Unit,
) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            onNothingSelected()
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long,
        ) {
            onPositionSelected(position)
        }
    }
}

fun SeekBar.onProgressChanged(func: (Int) -> Unit) {
    object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) func(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            // Do nothing
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            // Do nothing
        }
    }.let { setOnSeekBarChangeListener(it) }
}

fun View.setAllMargins(
    all: Int?,
) {
    setMargins(all, all, all, all)
}

fun View.setMargins(
    top: Int? = null,
    bottom: Int? = null,
    start: Int? = null,
    end: Int? = null,
) {
    layoutParams = layoutParams.also { params ->
        (params as? ViewGroup.MarginLayoutParams)?.let {
            top?.dpToPx()?.let { params.topMargin = it }
            bottom?.dpToPx()?.let { params.bottomMargin = it }
            start?.dpToPx()?.let { params.marginStart = it }
            end?.dpToPx()?.let { params.marginEnd = it }
        }
    }
}

fun View.getBitmapFromView(): Bitmap {
    val defaultSize by lazy { 100.dpToPx() }
    fun Int.checkValue(): Int = this.takeUnless { it <= 0 } ?: defaultSize

    return createBitmap(
        width = measuredWidth.checkValue(),
        height = measuredHeight.checkValue(),
        config = Bitmap.Config.ARGB_8888,
    ).also {
        draw(Canvas(it))
    }
}

fun View.measureExactly(width: Int, height: Int = width) {
    val specWidth = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
    val specHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    measure(specWidth, specHeight)
    layout(0, 0, measuredWidth, measuredHeight)
}

fun View.measureForSharing(): View {
    // Measure with width matched to screen width and height as wrap content or other height.
    val displayMetrics = context.resources.displayMetrics
    val specWidth = MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, MeasureSpec.EXACTLY)
    val specHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
    measure(specWidth, specHeight)
    layout(0, 0, measuredWidth, measuredHeight)
    return this
}

fun GridLayoutManager.setSpanSizeLookup(getSpanSize: (position: Int) -> Int) {
    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return getSpanSize(position)
        }
    }
}

fun RecyclerView.addOnScrollListenerAdapter(
    onScrolled: (recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit = { _, _, _ -> },
    onScrollStateChanged: (recyclerView: RecyclerView, newState: Int) -> Unit = { _, _ -> },
) = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        onScrolled(recyclerView, dx, dy)
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        onScrollStateChanged(recyclerView, newState)
    }
}.let(this::addOnScrollListener)

fun TextView.setCompoundDrawableWithIntrinsicBounds(
    @DrawableRes left: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes right: Int = 0,
    @DrawableRes bottom: Int = 0,
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

inline fun <T : View> T.postDelayed(
    delayMillis: Long,
    crossinline action: T.() -> Unit,
) {
    postDelayed(
        { action() },
        delayMillis,
    )
}

fun View.setRounded(radiusDp: Int) {
    outlineProvider = getViewOutlineProvider(0, 0, width, height, radiusDp)
    clipToOutline = true
}

fun View.setRoundedStart(radiusDp: Int) {
    outlineProvider = getViewOutlineProvider(0, 0, width + radiusDp.dpToPx(), height, radiusDp)
    clipToOutline = true
}

fun View.setRoundedEnd(radiusDp: Int) {
    outlineProvider = getViewOutlineProvider(-radiusDp.dpToPx(), 0, width, height, radiusDp)
    clipToOutline = true
}

inline fun <reified T : ViewGroup.LayoutParams> View.safeUpdateLayoutParams(
    block: T.() -> Unit,
) {
    val params = layoutParams as? T ?: return
    block(params)
    layoutParams = params
}

fun View.showKeyboard() {
    requestFocus()
    context.getSystemService<InputMethodManager>()?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun TextView.setTextOptional(text: CharSequence?) {
    if (text.isNullOrBlank()) {
        this.visible = false
    } else {
        this.text = text
        this.visible = true
    }
}

@Suppress("SameParameterValue")
private fun getViewOutlineProvider(
    left: Int,
    top: Int,
    right: Int,
    bottom: Int,
    radiusDp: Int,
): ViewOutlineProvider {
    return object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(left, top, right, bottom, radiusDp.dpToPx().toFloat())
        }fun View.applySquircleOutline(radiusPx: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            val w = view.width.toFloat()
            val h = view.height.toFloat()
            if (w <= 0f || h <= 0f) return

            val r = radiusPx.coerceAtMost(minOf(w, h) / 2f)
            // Apple 連續曲率參數：平滑延伸係數 1.28f 與控制點 0.45f
            val smooth = r * 1.28f
            val handle = r * 0.45f

            val path = Path().apply {
                moveTo(smooth, 0f)
                lineTo(w - smooth, 0f)
                cubicTo(w - handle, 0f, w, handle, w, smooth)
                lineTo(w, h - smooth)
                cubicTo(w, h - handle, w - handle, h, w - smooth, h)
                lineTo(smooth, h)
                cubicTo(handle, h, 0f, h - handle, 0f, h - smooth)
                lineTo(0f, smooth)
                cubicTo(0f, handle, handle, 0f, smooth, 0f)
                close()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                outline.setPath(path)
            } else {
                @Suppress("DEPRECATION")
                outline.setConvexPath(path)
            }
        }
    }
    clipToOutline = true
}
    }
}
