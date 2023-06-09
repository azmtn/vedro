package com.example.homework_2.presentation.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.*
import com.example.homework_2.R

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private var maxWidth: Int?

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexBoxLayout)
        maxWidth = a.getDimensionPixelSize(R.styleable.FlexBoxLayout_flexMaxWidth, 0)
        if (maxWidth == 0) maxWidth = null
        a.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentStringWidth = 0
        var currentStringHeight = 0
        var totalHeight = 0
        var maxCurrentStringWidth = 0
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        children.forEach { child ->
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            currentStringHeight = maxOf(
                currentStringHeight,
                child.measuredHeight + child.marginTop + child.marginBottom
            )
            if (currentStringWidth + child.measuredWidth + child.marginRight + child.marginLeft <= maxWidth ?: widthSize) {
                currentStringWidth += child.measuredWidth + child.marginRight + child.marginLeft
            } else {
                maxCurrentStringWidth = maxOf(currentStringWidth, maxCurrentStringWidth)
                currentStringWidth = child.measuredWidth + child.marginRight + child.marginLeft
                totalHeight += currentStringHeight
            }
        }
        if (childCount > 1 && maxCurrentStringWidth == 0) {
            maxCurrentStringWidth = currentStringWidth
            totalHeight = currentStringHeight
        } else {
            maxCurrentStringWidth = maxOf(currentStringWidth, maxCurrentStringWidth)
            totalHeight += currentStringHeight
        }
        setMeasuredDimension(
            resolveSize(maxCurrentStringWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 0) {
            var currentStart = 0
            var currentTop = getChildAt(0).marginTop
            children.forEach { child ->
                currentStart += child.marginStart
                if (currentStart + child.measuredWidth + child.marginEnd > width) {
                    currentStart = child.marginStart
                    currentTop += child.measuredHeight + child.marginTop + child.marginBottom
                }
                child.layout(
                    currentStart,
                    currentTop,
                    currentStart + child.measuredWidth,
                    currentTop + child.measuredHeight
                )
                currentStart += child.measuredWidth + child.marginRight
            }
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }
}