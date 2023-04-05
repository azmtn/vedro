package com.example.homework_2

import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import com.example.homework_2.data.model.ReactionCounterItem
import com.example.homework_2.data.model.Reaction
import com.example.homework_2.data.model.SELF_USER_ID
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Single
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class Utils {
    companion object {

        fun getEmojisForMessage(reactions: List<Reaction>): List<ReactionCounterItem> {
            return reactions
                .groupBy { reaction -> reaction.emojiCode }
                .map { emoji -> ReactionCounterItem(emoji.key, emoji.value.size) }
                .map { emojiWithCount ->
                    val selfReaction = reactions.firstOrNull { reaction ->
                        reaction.userId == SELF_USER_ID && reaction.emojiCode == emojiWithCount.code
                    }
                    if (selfReaction != null) emojiWithCount.selectedByCurrentUser = true
                    emojiWithCount
                }
        }

        fun measuredWidthWithMargins(view: View): Int {
            return view.measuredWidth + view.marginRight + view.marginLeft
        }

        fun measuredHeightWithMargins(view: View): Int {
            return view.measuredHeight + view.marginTop + view.marginBottom

        }

        internal fun <T> waitItemTestError(item: T): Single<T> {
            return Single.fromCallable {
                if (Random.nextBoolean()) throw Exception()
                item
            }
                .delay(1000, TimeUnit.MILLISECONDS, true)
        }

        internal fun View.snackBarAction(
            text: CharSequence,
            duration: Int,
            action: () -> Unit
        ) {
            Snackbar.make(this, text, duration).apply {
                setAction(context.getString(R.string.retry_action_snack_bar)) { action() }
            }
                .show()
        }

        internal fun getDateTimeFromTimestamp(timestamp: Long): LocalDateTime {
            return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                TimeZone.getDefault().toZoneId()
            )
        }

        internal fun View.showSnackBarWithRetryAction(
            text: CharSequence,
            duration: Int,
            action: () -> Unit
        ) {
            Snackbar.make(this, text, duration).apply {
                setAction(context.getString(R.string.retry_action_snack_bar)) { action() }
            }
                .show()
        }
    }


}