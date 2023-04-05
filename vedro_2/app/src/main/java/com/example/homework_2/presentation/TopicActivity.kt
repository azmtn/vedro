package com.example.homework_2.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.Utils.Companion.showSnackBarWithRetryAction
import com.example.homework_2.data.Reaction
import com.example.homework_2.databinding.ActivityTopicBinding
import com.example.homework_2.data.model.ReactionCounterItem
import com.example.homework_2.data.networking.NarrowRequest
import com.example.homework_2.data.networking.Networking
import com.example.homework_2.presentation.adapter.MessageListAdapter
import com.example.homework_2.presentation.adapter.OnBottomSheetListener
import com.example.homework_2.presentation.adapter.OnReactionClickListener
import com.example.homework_2.presentation.custom.*
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

internal class TopicActivity : AppCompatActivity(), OnReactionClickListener, OnBottomSheetListener {
    private lateinit var dialog: ReactionBottomSheetDialog
    private lateinit var binding: ActivityTopicBinding
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: MessageListAdapter
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        compositeDisposable = CompositeDisposable()

        setupBottomSheet()
        enterValueInputField()
        setupMessagesRecycler()
        setupToolbar()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onReactionClick(reactionView: ReactionView) {
        val reactionName = Reaction.emoji[reactionView.reaction]
        if (reactionName != null) {
            if (!reactionView.isSelected) {
                addReaction(reactionView, reactionName, false, null)
            } else {
                removeReaction(reactionView, reactionName)
            }
        }
    }

    private fun addReaction(
        reactionView: ReactionView,
        reactionName: String,
        isNewEmojiView: Boolean,
        emojiBox: FlexBoxLayout?
    ) {
        Networking.getZulipApi().addReaction(
            messageId = reactionView.messageId,
            emojiName = reactionName
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    reactionView.isSelected = true
                    reactionView.reactionCount++
                    if (isNewEmojiView) {
                        addNewEmojiToEmojiBox(emojiBox, reactionView)
                    }
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.add_error),
                        Snackbar.LENGTH_LONG
                    ) { addReaction(reactionView, reactionName, isNewEmojiView, emojiBox) }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun addNewEmojiToEmojiBox(
        emojiBox: FlexBoxLayout?,
        reactionView: ReactionView
    ) {
        if (emojiBox != null) {
            emojiBox.addView(reactionView, emojiBox.childCount - 1)
            if (emojiBox.childCount > 1) {
                emojiBox.getChildAt(emojiBox.childCount - 1).visibility = View.VISIBLE
            }
        }
    }

    private fun removeReaction(
        reactionView: ReactionView,
        reactionName: String
    ) {
        Networking.getZulipApi().removeReaction(
            messageId = reactionView.messageId,
            emojiName = reactionName
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    reactionView.isSelected = false
                    reactionView.reactionCount--
                    if (reactionView.reactionCount == 0) {
                        val emojiBox = (reactionView.parent as FlexBoxLayout)
                        emojiBox.removeView(reactionView)
                        if (emojiBox.childCount == 1) {
                            emojiBox.getChildAt(0).visibility = View.GONE
                        }
                    }
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.remove_error),
                        Snackbar.LENGTH_LONG
                    ) { removeReaction(reactionView, reactionName) }
                }
            )
            .addTo(compositeDisposable)
    }


    override fun onBottomSheetChoose(selectedView: View?, chooseReaction: String) {
        val flexBox = when (selectedView) {
            is MessageViewGroup -> selectedView.binding.flexBox
            is OwnMessageViewGroup -> selectedView.binding.flexBox
            is ImageView -> selectedView.parent as FlexBoxLayout
            else -> null
        }
        val reaction = flexBox?.children?.firstOrNull {
            it is ReactionView && it.reaction == chooseReaction
        }
        if (reaction is ReactionView) {
            if (!reaction.isSelected) {
                reaction.isSelected = true
                reaction.reactionCount++
            }
        } else {
            addNewEmojiToMessage(selectedView, flexBox, chooseReaction)
        }
    }

    private fun addNewEmojiToMessage(
        selectedView: View?,
        flexBox: FlexBoxLayout?,
        chosenEmojiCode: String
    ) {
        val messageId = when (selectedView) {
            is MessageViewGroup -> selectedView.messageId
            is OwnMessageViewGroup -> selectedView.messageId
            is ImageView -> {
                when (val parentViewGroup = selectedView.parent.parent) {
                    is MessageViewGroup -> parentViewGroup.messageId
                    is OwnMessageViewGroup -> parentViewGroup.messageId
                    else -> 0L
                }
            }
            else -> 0L
        }
        if (flexBox != null) {
            val reactionView = ReactionView.createEmojiWithCountView(
                emojiBox = flexBox,
                emoji = ReactionCounterItem(chosenEmojiCode, 0),
                messageId = messageId,
                reactionClickListener = this
            )
            val reactionName = Reaction.emoji[reactionView.reaction]
            if (reactionName != null) {
                addReaction(reactionView, reactionName, true, flexBox)
            }
        }
    }

    private fun setupToolbar() {
        binding.backIcon.setOnClickListener {
            onBackPressed()
        }
        binding.topicName.text = adapter.topicName
        binding.channelName.text = adapter.channelName
    }

    private fun enterValueInputField() {
        val enterMessage = binding.enterMessage
        val sendButton = binding.sendButton

        enterMessage.doAfterTextChanged {
            if (enterMessage.text.isEmpty()) {
                sendButton.setImageResource(R.drawable.plus)
                sendButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey_dark))
            } else {
                sendButton.setImageResource(R.drawable.airplane)
                sendButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_700))
            }
        }
        sendButton.setOnClickListener {
            if (enterMessage.text.isNotEmpty()) {
                sendMessage(
                    content = enterMessage.text.toString(),
                    stream = intent.getStringExtra(CHANNEL_NAME) ?: "",
                    topic = intent.getStringExtra(TOPIC_NAME) ?: ""
                )
                enterMessage.text.clear()
                val inputMethodManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(enterMessage.windowToken, 0)
            }
        }
    }

    private fun setupMessagesRecycler() {
        chatRecycler = binding.chat
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        chatRecycler.layoutManager = layoutManager
        adapter = MessageListAdapter(dialog, chatRecycler, this)

        adapter.topicName = resources.getString(
            R.string.topic_name,
            intent.getStringExtra(TOPIC_NAME)?.lowercase()
        )

        adapter.channelName = resources.getString(
            R.string.stream_name,
            intent.getStringExtra(CHANNEL_NAME)
        )

        getMessagesForChat()

        chatRecycler.adapter = adapter
    }

    private fun getMessagesForChat() {
        Networking.getZulipApi().getMessages(
            narrow = arrayOf(
                NarrowRequest(
                    operator = TOPIC_KEY,
                    operand = intent.getStringExtra(TOPIC_NAME) ?: ""
                )
            ).contentToString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    val oldMessages = adapter.messagesWithDateSeparators
                    adapter.messages = it.messages
                    val isLastChanged = !oldMessages.isNullOrEmpty()
                            && adapter.messagesWithDateSeparators.last() != oldMessages.last()
                    if (isLastChanged) adapter.notifyItemChanged(
                        adapter.messagesWithDateSeparators.size - 1
                    )
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.messages_error),
                        Snackbar.LENGTH_LONG
                    ) { setupMessagesRecycler() }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun sendMessage(
        content: String,
        stream: String,
        topic: String,
    ) {
        Networking.getZulipApi().sendMessage(
            to = stream,
            content = content,
            topic = topic
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onSuccess = {
                    getMessagesForChat()
                },
                onError = {
                    binding.root.showSnackBarWithRetryAction(
                        resources.getString(R.string.add_error),
                        Snackbar.LENGTH_LONG
                    ) { sendMessage(content, stream, topic) }
                }
            )
            .addTo(compositeDisposable)
    }

    private fun setupBottomSheet() {
        val bottomSheetLayout =
            layoutInflater.inflate(R.layout.layout_bottom_sheet, null) as LinearLayout
        dialog = ReactionBottomSheetDialog(
            this,
            R.style.BottomSheetDialogTheme,
            bottomSheetLayout,
            this
        )
        dialog.setContentView(bottomSheetLayout)
    }

    companion object {
        const val CHANNEL_NAME = "channelName"
        const val TOPIC_NAME = "topicName"
        const val TOPIC_KEY = "topic"

    }
}