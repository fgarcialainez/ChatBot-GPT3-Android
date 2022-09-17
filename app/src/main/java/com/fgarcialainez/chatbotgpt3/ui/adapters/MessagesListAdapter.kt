package com.fgarcialainez.chatbotgpt3.ui.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fgarcialainez.chatbotgpt3.R
import com.fgarcialainez.chatbotgpt3.models.Message

class MessagesListAdapter(val context: Context, val messagesList: ArrayList<Message>) :
    RecyclerView.Adapter<MessagesListAdapter.ViewHolder>() {

    private val CHAT_MINE = 0
    private val CHAT_PARTNER = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View? = null

        when (viewType) {
            CHAT_MINE -> {
                view = LayoutInflater.from(context).inflate(R.layout.row_chat_user, parent, false)
            }
            CHAT_PARTNER -> {
                view =
                    LayoutInflater.from(context).inflate(R.layout.row_chat_partner, parent, false)
            }
        }

        return ViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun getItemViewType(position: Int): Int {
        val user = messagesList[position].user.isCurrentUser
        return if (user) CHAT_MINE else CHAT_PARTNER
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messagesList[position]
        val avatar = message.user.avatar
        val content = message.content
        val viewType = if (message.user.isCurrentUser) CHAT_MINE else CHAT_PARTNER

        when (viewType) {
            CHAT_MINE -> {
                holder.message.setText(content)
            }
            CHAT_PARTNER -> {
                holder.avatar.setBackgroundResource(avatar)

                if (content.isNotEmpty()) {
                    holder.message.setText(content)
                    holder.message.setTextColor(Color.BLACK)
                    holder.message.setTypeface(null, Typeface.NORMAL)
                }
                else {
                    holder.message.setTextColor(Color.GRAY)
                    holder.message.setText(R.string.message_typing)
                    holder.message.setTypeface(null, Typeface.ITALIC)
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar = itemView.findViewById<ImageView>(R.id.avatar)
        val message = itemView.findViewById<TextView>(R.id.message)
    }
}