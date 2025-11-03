package com.example.fopsmart.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fopsmart.R
import com.example.fopsmart.data.model.ChatMessage
import com.google.android.material.button.MaterialButton
import io.noties.markwon.Markwon
import io.noties.markwon.linkify.LinkifyPlugin

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private var markwon: Markwon? = null

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isFromUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_chat_user
        } else {
            R.layout.item_chat_bot
        }

        if (markwon == null) {
            val context = parent.context
            markwon = Markwon.builder(context)
                .usePlugin(LinkifyPlugin.create())
                .build()
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        markwon?.setMarkdown(holder.messageText, message.text)

        if (!message.isFromUser) {
            holder.copyButton.visibility = View.VISIBLE
            holder.copyButton.setOnClickListener {
                copyToClipboard(holder.itemView.context, message.text)
                showCopyToast(holder.itemView.context)
            }
        } else {
            holder.copyButton.visibility = View.GONE
        }
    }

    private fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Відповідь бота", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun showCopyToast(context: Context) {
        Toast.makeText(context, "Відповідь скопійовано", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val copyButton: MaterialButton = itemView.findViewById(R.id.copy_button)
    }
}