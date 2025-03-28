package com.example.whatsautomessage.controller

import android.content.Context
import android.widget.Toast
import com.example.whatsautomessage.model.Message

class MessageController(private val context: Context) {

    fun sendMessage(message: Message) {
        // Şu an için simülasyon: WhatsApp mesajı gönderilmiş gibi gösteriyoruz.
        val info = "Mesaj Gönderildi:\nKime: ${message.receiverName}\nMesaj: ${message.messageContent}\nGrup: ${message.group}"
        Toast.makeText(context, info, Toast.LENGTH_LONG).show()
    }
}
