package models

import io.github.ponnamkarthik.richlinkpreview.MetaData

class ChatMessage(val id: String, var text: String, val fromId: String, val toId: String, val timeStamp: Long, val messagePreview: HashMap<String, String>) {
    constructor() : this("", "", "", "", -1, HashMap()) // empty constructor
}