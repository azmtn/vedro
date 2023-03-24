package com.example.homework_2

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.example.homework_2.data.messagesItem
import com.example.homework_2.model.ReactionCounterItem
import com.example.homework_2.model.MessageItem
import kotlin.math.roundToInt

class Utils {
    companion object {
        fun dpToPx(dp: Int, context: Context): Int {
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        }

        fun getEmojisForMessage(messageId: Int): List<ReactionCounterItem> {
            val message = messagesItem.first { it is MessageItem && it.id == messageId }
            return if (message is MessageItem) {
                val emojiList = mutableListOf<ReactionCounterItem>()
                message.reactions
                    .groupBy { reaction -> reaction.code }
                    .map { emoji -> emoji.key to emoji.value.size }
                    .mapTo(emojiList) { emoji -> ReactionCounterItem(emoji.first, emoji.second) }

                emojiList.forEach { emojiWithCount ->
                    val selfReaction = message.reactions.firstOrNull { reaction ->
                        reaction.userId == 1 && reaction.code == emojiWithCount.code
                    }
                    if (selfReaction != null) emojiWithCount.selectedByCurrentUser = true
                }
                return emojiList
            } else {
                listOf()
            }
        }

        fun measuredWidthWithMargins(view: View): Int {
            return view.measuredWidth + view.marginRight + view.marginLeft
        }

        fun measuredHeightWithMargins(view: View): Int {
            return view.measuredHeight + view.marginTop + view.marginBottom

        }
    }
}