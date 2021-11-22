package com.sunny.agoravideochat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.sunny.agoravideochat.R

import com.sunny.agoravideochat.models.ChatMessage
import com.sunny.agoravideochat.models.MessageType

class ChatAdapter(
    private var dataList: ArrayList<ChatMessage>,
    private val context: Context,
    private val speaker: String,
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    override fun getItemCount() = dataList.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.bind(dataList[position], speaker)
    }

    fun setData(dataList: ChatMessage) {
        val size = this.dataList.size
        this.dataList.add(dataList)
        notifyItemInserted(size)

    }

    fun updateData(dataList: List<ChatMessage>) {
        val size = this.dataList.size
        this.dataList = dataList as ArrayList<ChatMessage>
        notifyDataSetChanged()

    }

    fun updateMessage(dataList: List<ChatMessage>) {
        val size = this.dataList.size
        this.dataList = dataList as ArrayList<ChatMessage>


        notifyItemInserted(size)

    }

    fun addMessage(dataList: ChatMessage) {
        val size = this.dataList.size
        this.dataList.add(dataList)
        notifyItemInserted(size)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val speakerName = view.findViewById<TextView>(R.id.speaker_name)
        val received = view.findViewById<LinearLayout>(R.id.received)
        val sent = view.findViewById<LinearLayout>(R.id.sent)
        val sentMessage = view.findViewById<TextView>(R.id.sent_message)
        val message = view.findViewById<TextView>(R.id.message)
        fun bind(data: ChatMessage, speaker: String) {


            speakerName.text = speaker
            when (data.messageType) {
                MessageType.SENT -> {
                    sentMessage.text = data.message

                    received.isVisible = false
                    sent.isVisible = true

                }
                MessageType.RECEIVED -> {
                    sent.isVisible = false
                    received.isVisible = true
                    message.text = data.message


                }
            }

        }
    }
}