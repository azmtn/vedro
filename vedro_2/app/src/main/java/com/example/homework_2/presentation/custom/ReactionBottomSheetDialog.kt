package com.example.homework_2.presentation.custom

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.widget.NestedScrollView
import com.example.homework_2.R
import com.example.homework_2.data.Reaction.emoji
import com.example.homework_2.presentation.adapter.OnBottomSheetListener
import com.google.android.material.bottomsheet.BottomSheetDialog

class ReactionBottomSheetDialog(
    context: Context,
    @StyleRes theme: Int,
    bottomSheet: LinearLayout,
    private val bottomSheetListener: OnBottomSheetListener
): BottomSheetDialog(context, theme) {

    private var flexBoxLayout = bottomSheet.getChildAt(1) as NestedScrollView
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
        processSelectedEmoji(selectedView, chosenEmojiCode)
    }

    private fun processSelectedEmoji(selectedView: View?, chosenEmojiCode: String) {
        if (chosenEmojiCode.isEmpty()) return
        bottomSheetListener.onBottomSheetChoose(selectedView, chosenEmojiCode)
    }

    private fun createEmojiViews() {
        emoji.forEach { emojiCode ->
            val emojiView = LayoutInflater.from(context).inflate(
                R.layout.layout_bottom_sheet_emoji, null
            ) as TextView
            emojiView.text = emojiCode.key
            emojiView.setOnClickListener {
                chosenEmojiCode = emojiView.text.toString()
                dismiss()
            }
            flexBoxLayout?.addView(emojiView)
        }
    }
}
