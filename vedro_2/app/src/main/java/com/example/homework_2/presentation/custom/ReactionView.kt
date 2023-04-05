package com.example.homework_2.presentation.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.example.homework_2.R
import com.example.homework_2.data.model.ReactionCounterItem
import com.example.homework_2.presentation.adapter.OnReactionClickListener

class ReactionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val textPaint = TextPaint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textBounds = Rect()
    private val textPoint = PointF()
    var reaction = ""
    private var minWidth = 40
    private lateinit var reactionClickListener: OnReactionClickListener
    var messageId = 0L

    var reactionCount = 0
        set(value) {
            val oldValue = field
            field = value
            if (oldValue.toString().length != value.toString().length) {
                requestLayout()
            }
        }

    private val textToDraw: String
        get() = "$reaction $reactionCount"

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ReactionView)
        reactionCount = typedArray.getInt(R.styleable.ReactionView_reactionCount, 1)
        reaction = typedArray.getString(R.styleable.ReactionView_reaction) ?: DEFAULT_REACTION
        minWidth = typedArray.getDimensionPixelSize(R.styleable.ReactionView_minWidth, 40)
        textPaint.color = typedArray.getColor(R.styleable.ReactionView_textColor, Color.WHITE)
        textPaint.textSize = typedArray.getDimension(
            R.styleable.ReactionView_textSize,
            DEFAULT_DIMENS_SP * resources.displayMetrics.density
        )
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (visibility == VISIBLE) {
            textPaint.getTextBounds(textToDraw, 0, textToDraw.length, textBounds)

            val textWidth = textBounds.width()
            val textHeight = textBounds.height()

            var sumWidth = textWidth + paddingLeft + paddingRight
            val sumHeight = textHeight + paddingTop + paddingBottom

            if (sumWidth < minWidth) sumWidth = minWidth

            setMeasuredDimension(
                resolveSize(sumWidth, widthMeasureSpec),
                resolveSize(sumHeight, heightMeasureSpec)
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textPoint.x = w / 2f - textBounds.width() / 2f
        textPoint.y = h / 2f + textBounds.height() / 2f - textPaint.descent()
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawText(textToDraw, textPoint.x, textPoint.y, textPaint)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) {
            mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        }
        return drawableState
    }


    companion object {
        private const val DEFAULT_DIMENS_SP = 15
        private const val DEFAULT_REACTION = "\uD83D\uDE02"
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
//
//        private val onEmojiClick: (v: View) -> Unit = { view ->
//            view.isSelected = !view.isSelected
//            (view as ReactionView).apply {
//                if (isSelected) reactionCount++ else reactionCount--
//                if (reactionCount == 0) {
//                    val emojiBox = (parent as FlexBoxLayout)
//                    emojiBox.removeView(this)
//                    if (emojiBox.childCount == 1) {
//                        emojiBox.getChildAt(0).visibility = GONE
//                    }
//                }
//            }
//        }

        internal fun createEmojiWithCountView(
            emojiBox: FlexBoxLayout,
            emoji: ReactionCounterItem,
            messageId: Long,
            reactionClickListener: OnReactionClickListener
        ): ReactionView {
            val emojiView = LayoutInflater.from(emojiBox.context).inflate(
                R.layout.default_reaction_view,
                emojiBox,
                false
            ) as ReactionView
            emojiView.messageId = messageId
            emojiView.reaction = if (emoji.code.any { it in 'a'..'f' }) {
                String(Character.toChars(emoji.code.toInt(16)))
            } else {
                emoji.code
            }
            emojiView.setOnClickListener {
                reactionClickListener.onReactionClick(emojiView)
            }
            emojiView.reactionCount = emoji.count
            return emojiView
        }
    }
}
