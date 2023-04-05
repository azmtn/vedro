package com.example.homework_2.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.*
import com.example.homework_2.databinding.MessageViewGroupBinding

class MessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    var binding: MessageViewGroupBinding =
        MessageViewGroupBinding.inflate(LayoutInflater.from(context), this)

    var messageId = 0L
    private var messageBlockStart = 0
    private val avatar = binding.avatar
    private val messageBlock = binding.messageBlock
    private val flexBox = binding.flexBox

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildWithMargins(
            avatar,
            widthMeasureSpec,
            0,
            heightMeasureSpec,
            0
        )

        measureChildWithMargins(
            messageBlock,
            widthMeasureSpec,
            avatar.measuredWidth,
            heightMeasureSpec,
            0
        )

        var totalWidth =
            avatar.measuredWidth + avatar.marginRight + avatar.marginLeft + messageBlock.measuredWidth + messageBlock.marginRight + messageBlock.marginLeft
        var totalHeight =
            avatar.measuredHeight + avatar.marginTop + avatar.marginBottom + messageBlock.measuredHeight + messageBlock.marginTop + messageBlock.marginBottom

        measureChildWithMargins(
            flexBox,
            widthMeasureSpec,
            avatar.measuredWidth,
            heightMeasureSpec,
            0
        )

        if (flexBox.measuredWidth + flexBox.marginRight + flexBox.marginLeft > messageBlock.measuredWidth + messageBlock.marginRight + messageBlock.marginLeft) {
            totalWidth += flexBox.measuredWidth + flexBox.marginRight + flexBox.marginLeft - messageBlock.measuredWidth + messageBlock.marginRight + messageBlock.marginLeft
        }
        totalHeight += flexBox.measuredHeight + flexBox.marginTop + flexBox.marginBottom

        setMeasuredDimension(
            resolveSize(totalWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        avatar.layout(
            avatar.marginStart,
            avatar.marginTop,
            avatar.marginStart + avatar.measuredWidth,
            avatar.marginTop + avatar.measuredHeight
        )

        messageBlockStart = avatar.right + avatar.marginStart + avatar.marginEnd
        messageBlock.layout(
            messageBlockStart + messageBlock.marginStart,
            0,
            messageBlockStart + messageBlock.measuredWidth + messageBlock.marginEnd,
            messageBlock.marginBottom + messageBlock.measuredHeight
        )

        val flexBoxTop =
            messageBlock.measuredHeight + messageBlock.marginTop + messageBlock.marginBottom
        val firstChild = flexBox.getChildAt(0)

        flexBox.layout(
            messageBlockStart - firstChild.marginStart,
            flexBoxTop,
            messageBlockStart + flexBox.measuredWidth + flexBox.marginRight + flexBox.marginLeft - firstChild.marginStart,
            flexBoxTop + flexBox.marginBottom + flexBox.measuredHeight
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
