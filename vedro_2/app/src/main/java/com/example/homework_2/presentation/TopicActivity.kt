package com.example.homework_2.presentation

import android.content.Context
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homework_2.R
import com.example.homework_2.data.messagesItem
import com.example.homework_2.data.topicsItems

import com.example.homework_2.databinding.ActivityTopicBinding
import com.example.homework_2.model.MessageItem
import com.example.homework_2.model.TopicItem
import com.example.homework_2.presentation.ProfileFragment.Companion.OWN_USER_ID
import com.example.homework_2.presentation.adapter.MessageListAdapter
import com.example.homework_2.presentation.custom.*
import java.time.LocalDateTime


internal class TopicActivity : AppCompatActivity() {
    private lateinit var dialog: ReactionBottomSheetDialog
    private lateinit var binding: ActivityTopicBinding
    private lateinit var chatRecycler: RecyclerView
    private lateinit var adapter: MessageListAdapter
    private var topics: TopicItem = topicsItems[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomSheet()
        enterValueInputField()
        setupMessagesRecycler()
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.backIcon.setOnClickListener {
            onBackPressed()
        }

        binding.topicName.text = resources.getString(
            R.string.topic_name,
            intent.getStringExtra(TOPIC_NAME)?.lowercase()
        )

        binding.channelName.text = resources.getString(
            R.string.stream_name,
            intent.getStringExtra(CHANNEL_NAME)
        )
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
                messagesItem.add(
                    MessageItem(
                        id = (messagesItem.size + 1),
                        userId = OWN_USER_ID,
                        topicName = topics.topicName,
                        text = enterMessage.text.toString(),
                        reactions = listOf(),
                        sendDateTime = LocalDateTime.now()
                    )
                )
                adapter.update(messagesItem, messagesItem.size - 1)
                chatRecycler.layoutManager?.scrollToPosition(adapter.messages.size - 1)
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
        adapter = MessageListAdapter(dialog)
        adapter.messages = messagesItem
        chatRecycler.adapter = adapter
    }

    private fun setupBottomSheet() {
        val bottomSheetLayout =
            layoutInflater.inflate(R.layout.layout_bottom_sheet, null) as LinearLayout
        dialog = ReactionBottomSheetDialog(
            this,
            R.style.BottomSheetDialogTheme,
            bottomSheetLayout
        )
        dialog.setContentView(bottomSheetLayout)
    }

    companion object {
        const val CHANNEL_NAME = "channelName"
        const val TOPIC_NAME = "topicName"
    }
}