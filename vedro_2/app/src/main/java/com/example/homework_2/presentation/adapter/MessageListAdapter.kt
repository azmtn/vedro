package com.example.homework_2.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homework_2.R
import com.example.homework_2.Utils
import com.example.homework_2.Utils.Companion.getDateTimeFromTimestamp
import com.example.homework_2.data.model.MessageItem
import com.example.homework_2.data.model.SELF_USER_ID
import com.example.homework_2.presentation.custom.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MessageListAdapter(
    private val dialog: ReactionBottomSheetDialog,
    private val chatRecycler: RecyclerView,
    private val reactionClickListener: OnReactionClickListener,
) : RecyclerView.Adapter<BaseViewHolder>() {

    var channelName = ""
    var topicName = ""

    var messagesWithDateSeparators: List<Any>
        set(value) {
            if (messagesWithDateSeparators.isNotEmpty() && messagesWithDateSeparators[0] == value[0]) {
                differ.submitList(value) {
                    chatRecycler.scrollToPosition(value.size - 1)
                }
            } else {
                differ.submitList(value)
            }
        }
        get() = differ.currentList

    internal var messages: List<MessageItem> = mutableListOf()
        set(value) {
            field = value
            messagesWithDateSeparators = initDateSeparators(value)
        }

    private val differ = AsyncListDiffer(this, MessageListDiffCallback())

    override fun getItemCount(): Int = messagesWithDateSeparators.size

    override fun getItemViewType(position: Int): Int {
        val message = messagesWithDateSeparators[position]
        return when {
            message is LocalDate -> VIEW_TYPE_DATE
            message is MessageItem && message.userId == SELF_USER_ID -> VIEW_TYPE_OWN_MESSAGE
            else -> VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_OTHER_MESSAGE -> {
                val messageView = MessageViewGroup(parent.context)
                messageView.setOnLongClickListener {
                    return@setOnLongClickListener messageOnClickFunc(dialog, messageView)
                }
                MessageViewHolder(messageView)
            }
            VIEW_TYPE_OWN_MESSAGE -> {
                val selfMessageView = OwnMessageViewGroup(parent.context)
                changeParams(selfMessageView)
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

    private fun changeParams(selfMessageView: OwnMessageViewGroup) {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        selfMessageView.layoutParams = layoutParams
    }


    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        return when (viewHolder) {
            is MessageViewHolder -> viewHolder.bind(messagesWithDateSeparators[position] as MessageItem)
            is SelfMessageViewHolder -> viewHolder.bind(messagesWithDateSeparators[position] as MessageItem)
            is SendDateViewHolder -> viewHolder.bind(messagesWithDateSeparators[position] as LocalDate)
            else -> throw RuntimeException("Unknown view type: $viewHolder")
        }
    }

    internal fun update(newMessages: List<Any>, position: Int) {
        messagesWithDateSeparators = newMessages
        notifyItemInserted(position)
    }

    inner class SelfMessageViewHolder(private val selfMessageView: OwnMessageViewGroup) :
        BaseViewHolder(selfMessageView) {
        private val messageView = selfMessageView.binding.message
        private val flexBox = selfMessageView.binding.flexBox

        fun bind(message: MessageItem) {
            message?.let {
                selfMessageView.messageId = message.id
                messageView.text = HtmlCompat.fromHtml(it.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
                fillEmojiBox(it, flexBox)
            }
        }
    }

    inner class MessageViewHolder(private val messageView: MessageViewGroup) :
        BaseViewHolder(messageView) {
        private val avatar = messageView.binding.avatar
        private val username = messageView.binding.userName
        private val messageText = messageView.binding.message
        private val flexBox = messageView.binding.flexBox

        internal fun bind(message: MessageItem) {
            message?.let {
                messageView.messageId = message.id
                username.text = it.userFullName
                messageText.text = HtmlCompat.fromHtml(
                    it.content,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ) /////TODO 1111111111
                if (it.avatarUrl != null) {
                    Glide.with(messageView)
                        .asBitmap()
                        .load(it.avatarUrl)
                        .error(R.drawable.avatar)
                        .into(avatar as ImageView)
                }
                fillEmojiBox(it, flexBox)
            }
        }
    }

    class SendDateViewHolder(private val sendDateView: FrameLayout) : BaseViewHolder(sendDateView) {
        internal fun bind(sendDate: LocalDate?) {
            var sendDateStr =
                sendDate?.format(DateTimeFormatter.ofPattern("dd MMM"))?.replace(".", "")
            sendDateStr = sendDateStr?.replaceRange(
                sendDateStr.length - 3,
                sendDateStr.length - 2,
                sendDateStr[sendDateStr.length - 3].uppercaseChar().toString()
            )
            (sendDateView.getChildAt(0) as TextView).text = sendDateStr
        }
    }

    private fun initDateSeparators(messages: List<MessageItem>): List<Any> {
        val messagesWithDateSeparators = mutableListOf<Any>()
        for (curIndex in messages.indices) {
            val curDate = getDateTimeFromTimestamp(messages[curIndex].timestamp).toLocalDate()
            if (curIndex == 0) {
                messagesWithDateSeparators.add(curDate)
            } else {
                val prevDate = getDateTimeFromTimestamp(messages[curIndex - 1].timestamp).toLocalDate()
                if (prevDate != curDate) {
                    messagesWithDateSeparators.add(curDate)
                }
            }
            messagesWithDateSeparators.add(messages[curIndex])
        }
        return messagesWithDateSeparators
    }

    private fun messageOnClickFunc(dialog: ReactionBottomSheetDialog, view: View): Boolean {
        dialog.show(view)
        return true
    }

    fun fillEmojiBox(message: MessageItem, emojiBox: FlexBoxLayout) {
        val emojis = Utils.getEmojisForMessage(message.reactions)

        emojiBox.removeAllViews()
        var addEmojiView = LayoutInflater.from(emojiBox.context).inflate(
                R.layout.view_image_add_emoji,
                emojiBox,
                false
            ) as ImageView
            addEmojiView.setOnClickListener {
                this@MessageListAdapter.dialog.show(addEmojiView)
            }
            emojiBox.addView(addEmojiView)

        if (emojis.isNotEmpty()) {
            emojis.forEach { emoji ->
                val emojiView = ReactionView.createEmojiWithCountView(
                    emojiBox = emojiBox,
                    emoji = emoji,
                    messageId = message.id,
                    reactionClickListener = reactionClickListener
                )
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
    }
}