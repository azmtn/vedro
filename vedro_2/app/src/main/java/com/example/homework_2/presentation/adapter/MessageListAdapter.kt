package com.example.homework_2.presentation.adapter

import android.util.LayoutDirection
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.Utils
import com.example.homework_2.data.getUserById

import com.example.homework_2.model.MessageItem
import com.example.homework_2.presentation.custom.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MessageListAdapter(private val dialog: ReactionBottomSheetDialog) :
    RecyclerView.Adapter<MessageListAdapter.BaseViewHolder>() {

    open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view)

    internal var messages: List<Any>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    private val differ = AsyncListDiffer(this, MessageListDiffCallback())
    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message is LocalDate -> VIEW_TYPE_DATE
            message is MessageItem && message.userId == 1 -> VIEW_TYPE_OWN_MESSAGE
            else -> VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_OTHER_MESSAGE -> {
                val messageView = MessageViewGroup(parent.context)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(Utils.dpToPx(DEFAULT_MARGIN_DP, parent.context))
                messageView.layoutParams = layoutParams
                messageView.setOnLongClickListener {
                    return@setOnLongClickListener messageOnClickFunc(dialog, messageView)
                }
                MessageViewHolder(messageView)
            }
            VIEW_TYPE_OWN_MESSAGE -> {
                val selfMessageView = OwnMessageViewGroup(parent.context)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(Utils.dpToPx(DEFAULT_MARGIN_DP, parent.context))
                layoutParams.gravity = Gravity.END
                parent.layoutParams.resolveLayoutDirection(LayoutDirection.RTL)
                selfMessageView.layoutParams = layoutParams
                selfMessageView.setOnLongClickListener {
                    return@setOnLongClickListener messageOnClickFunc(dialog, selfMessageView)
                }
                SelfMessageViewHolder(selfMessageView)
            }
            VIEW_TYPE_DATE -> {
                val sendDateView = LayoutInflater.from(parent.context).inflate(
                    R.layout.view_send_date,
                    parent,
                    false
                ) as FrameLayout
                SendDateViewHolder(sendDateView)
            }
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        return when (viewHolder) {
            is MessageViewHolder -> viewHolder.bind(messages[position] as MessageItem)
            is SelfMessageViewHolder -> viewHolder.bind(messages[position] as MessageItem)
            is SendDateViewHolder -> viewHolder.bind(messages[position] as LocalDate)
            else -> throw RuntimeException("Unknown view type: $viewHolder")
        }
    }

    internal fun update(newMessages: List<Any>, position: Int) {
        messages = newMessages
        notifyItemInserted(position)
    }

    inner class SelfMessageViewHolder(selfMessageView: OwnMessageViewGroup) :
        BaseViewHolder(selfMessageView) {
        private val messageView = selfMessageView.binding.message
        private val flexBox = selfMessageView.binding.flexBox

        fun bind(message: MessageItem?) {
            message?.let {
                messageView.text = it.text
                fillEmojiBox(it, flexBox)
            }
        }
    }

    inner class MessageViewHolder(messageView: MessageViewGroup) :
        BaseViewHolder(messageView) {
        private val username = messageView.binding.userName
        private val messageView = messageView.binding.message
        private val flexBox = messageView.binding.flexBox

        internal fun bind(message: MessageItem) {
            message?.let {
                val user = getUserById(it.userId)
                username.text = user?.name
                messageView.text = it.text
                fillEmojiBox(it, flexBox)
            }
        }
    }

    class SendDateViewHolder(private val sendDateView: FrameLayout) : BaseViewHolder(sendDateView) {
        internal fun bind(sendDate: LocalDate?) {
            var sendDateStr =
                sendDate?.format(DateTimeFormatter.ofPattern("dd MMM"))?.replace(".", "")
            sendDateStr?.let {
                sendDateStr = it.replaceRange(
                    it.length - 3,
                    it.length - 2,
                    it[it.length - 3].uppercaseChar().toString()
                )
            }
            (sendDateView.getChildAt(0) as TextView).text = sendDateStr
        }
    }

    private fun messageOnClickFunc(dialog: ReactionBottomSheetDialog, view: View): Boolean {
        dialog.show(view)
        return true
    }

    fun fillEmojiBox(message: MessageItem, emojiBox: FlexBoxLayout) {
        val emojis = Utils.getEmojisForMessage(message.id)

        var addEmojiView: ImageView? = null
        if (emojiBox.childCount == 0) {
            addEmojiView = LayoutInflater.from(emojiBox.context).inflate(
                R.layout.view_image_add_emoji,
                emojiBox,
                false
            ) as ImageView
            addEmojiView.setOnClickListener {
                this@MessageListAdapter.dialog.show(addEmojiView)
            }
            emojiBox.addView(addEmojiView)
        }

        if (emojis.isNotEmpty()) {
            emojis.forEach { emoji ->
                val emojiView = ReactionView.createEmojiWithCountView(emojiBox, emoji)
                if (emoji.selectedByCurrentUser) emojiView.isSelected = true
                emojiBox.addView(emojiView, emojiBox.childCount - 1)
            }
            addEmojiView?.visibility = View.VISIBLE
        }
    }

    companion object {
        const val VIEW_TYPE_OTHER_MESSAGE = 1
        const val VIEW_TYPE_OWN_MESSAGE = 0
        const val VIEW_TYPE_DATE = 2
        const val DEFAULT_MARGIN_DP = 16
    }
}