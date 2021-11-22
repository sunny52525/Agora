package com.sunny.agoravideochat.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunny.agoravideochat.R
import com.sunny.agoravideochat.adapter.ChatAdapter
import com.sunny.agoravideochat.models.ChatMessage
import com.sunny.agoravideochat.models.MessageType
import com.sunny.agoravideochat.viewmodel.VideoChatViewModel
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback


class ChatFragment : Fragment(R.layout.fragment_chat) {


    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var send: CardView
    private lateinit var messageText: EditText
    private lateinit var viewModel: VideoChatViewModel
    private lateinit var adapter: ChatAdapter
    private fun initViews(view: View) {
        with(view) {
            chatRecyclerView = findViewById(R.id.chat_recycler_view)
            send = findViewById(R.id.send)
            messageText = findViewById(R.id.messageText)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(VideoChatViewModel::class.java)

        initViews(view)
        viewModel.isChatOpen.postValue(true)
        viewModel.newMessage.postValue(false)
        adapter = ChatAdapter(
            viewModel.messages.value as ArrayList<ChatMessage>,
            requireContext(),
            "Shaun"
        )
        chatRecyclerView.adapter = adapter
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
//            stackFromEnd=true
        }

        viewModel.messages.observe(viewLifecycleOwner) {
            viewModel.newMessage.postValue(false)

            if (adapter.itemCount == 0)
                adapter.updateData(it)
            else
                adapter.updateMessage(it)


            chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
        }

        send.setOnClickListener {
            val message = messageText.text.toString()

            if (message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Message Cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val chatMessage = ChatMessage(messageType = MessageType.SENT, message = message)
            viewModel.addMessage(chatMessage)
            val rtmMessage = viewModel.rtmClient?.createMessage()
            rtmMessage?.text = message
            viewModel?.channel?.sendMessage(rtmMessage, object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                }

                override fun onFailure(p0: ErrorInfo?) {
                }

            })
            messageText.setText("")
        }

    }


    override fun onDestroy() {
        super.onDestroy()
    }

}