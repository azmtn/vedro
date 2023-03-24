package com.example.homework_2.presentation.custom

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.view.children
import com.example.homework_2.R
import com.example.homework_2.data.Reaction
import com.example.homework_2.model.ReactionCounterItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class ReactionBottomSheetDialog(
    context: Context,
    @StyleRes theme: Int,
    private var bottomSheet: LinearLayout
): BottomSheetDialog(context, theme) {

    private var chosenEmojiCode = ""
    private var selectedView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createEmojiViews()
    }

    fun show(view: View) {
        selectedView = view
        this.show()
    }

    override fun dismiss() {
        super.dismiss()
        selectorEmoji(selectedView, chosenEmojiCode)
    }



    private fun createEmojiViews() { ////TODO  надо оптимизировать добавление.
        Reaction.emoji.forEach { emojiCode ->
            val emojiView = LayoutInflater.from(context).inflate(
                R.layout.layout_bottom_sheet_emoji, null
            ) as TextView
            emojiView.text = emojiCode
            emojiView.setOnClickListener {
                chosenEmojiCode = emojiView.text.toString()
                dismiss()
            }
            (bottomSheet.getChildAt(1) as FlexBoxLayout).addView(emojiView)
        }
    }

    private fun selectorEmoji(selectView: View?, selectReaction: String) {
        if (selectReaction.isNotEmpty()) {
            val flexBox = when (selectView) {
                is MessageViewGroup -> selectView.binding.flexBox
                is OwnMessageViewGroup -> selectView.binding.flexBox
                is ImageView -> selectView.parent as FlexBoxLayout
                else -> null
            }
            val reaction = flexBox?.children?.firstOrNull {
                it is ReactionView && it.reaction == selectReaction
            }
            if (reaction is ReactionView) {
                if (!reaction.isSelected) {
                    reaction.isSelected = true
                    reaction.reactionCount++
                }
            } else {
                if (flexBox != null) {
                    val reactionView = ReactionView.createEmojiWithCountView(
                        flexBox,
                        ReactionCounterItem(selectReaction, 1)
                    )
                    reactionView.isSelected = true
                    flexBox.addView(reactionView, flexBox.childCount - 1)
                    if (flexBox.childCount > 1) {
                        flexBox.getChildAt(flexBox.childCount - 1).visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
