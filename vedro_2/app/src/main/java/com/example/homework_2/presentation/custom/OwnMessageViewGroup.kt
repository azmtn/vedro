package com.example.homework_2.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.*
import com.example.homework_2.Utils
import com.example.homework_2.databinding.OwnMessageViewGroupBinding

class OwnMessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    var messageId = 0L

    var binding: OwnMessageViewGroupBinding =
        OwnMessageViewGroupBinding.inflate(LayoutInflater.from(context), this)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val message = binding.message
        val emojiBox = binding.flexBox

        measureChildWithMargins(
            message,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        var contentWidth = Utils.measuredWidthWithMargins(message)
        var contentHeight = Utils.measuredHeightWithMargins(message)

        measureChildWithMargins(
            emojiBox,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        if (Utils.measuredWidthWithMargins(emojiBox) > Utils.measuredWidthWithMargins(message)) {
            contentWidth += Utils.measuredWidthWithMargins(emojiBox) - Utils.measuredWidthWithMargins(
                message
            )
        }
        contentHeight += Utils.measuredHeightWithMargins(emojiBox)

        setMeasuredDimension(
            resolveSize(contentWidth, widthMeasureSpec),
            resolveSize(contentHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val message = binding.message
        val emojiBox = binding.flexBox

        message.layout(
            r - Utils.measuredWidthWithMargins(message) - marginEnd,
            0,
            r - marginEnd,
            Utils.measuredHeightWithMargins(message)
        )

        val emojiBoxTop = Utils.measuredHeightWithMargins(message)
        val firstChildRight = emojiBox.children.filter {
            right == emojiBox.children.maxOfOrNull { right }
        }.first()
        val lastChildEnd = firstChildRight.marginEnd
        emojiBox.layout(
            r - Utils.measuredWidthWithMargins(emojiBox) + lastChildEnd - marginEnd,
            emojiBoxTop,
            r + lastChildEnd - marginEnd,
            emojiBoxTop + emojiBox.marginBottom + emojiBox.measuredHeight
        )
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
