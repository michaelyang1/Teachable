package views

import android.os.Looper
import android.util.Log
import com.example.teachable.R
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.github.ponnamkarthik.richlinkpreview.MetaData
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import kotlinx.android.synthetic.main.preview_chat_from_row.view.*
import kotlinx.android.synthetic.main.preview_chat_to_row.view.*
import messages.ChatLogActivity
import models.ChatMessage
import models.User
import java.util.logging.Handler
import kotlin.reflect.typeOf

//class ChatFromItemsPreview(val chatMessage: ChatMessage, val fromId: String, val toId: String, val key: String): Item<GroupieViewHolder>() { //GroupieViewHolder is type parameter
//    var meta: MetaData? = null
//    val text = chatMessage.text
//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        if (chatMessage.messagePreview.isEmpty() and (meta == null)) {
//            Log.e("HashMap", "is empty")
//            setPreviewWithoutMetaData(viewHolder)
//        } else {
//            Log.e("HashMap", "is not empty")
//            setPreviewWithMetaData(viewHolder)
//        }
//
//        if (meta == null) {
//            Log.e("ChatFromItemsPreview", "meta is null")
//        } else {
//            Log.e("ChatFromItemsPreview", "meta is not null")
//        }
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.preview_chat_from_row
//    }
//
//    private fun setPreviewWithMetaData(viewHolder: GroupieViewHolder) {
//        if (meta != null) {
//            viewHolder.itemView.richLinkViewFrom.setLinkFromMeta(meta)
//            return
//        }
//
//        val mapMeta = chatMessage.messagePreview
//        val metaData = MetaData()
//        metaData.title = mapMeta["Title"]
//        metaData.description = mapMeta["Description"]
//        metaData.favicon = mapMeta["Favicon"]
//        metaData.imageurl = mapMeta["ImageUrl"]
//        metaData.sitename = mapMeta["Sitename"]
//        metaData.url = mapMeta["Url"]
//        metaData.mediatype = mapMeta["MediaType"]
//
//        viewHolder.itemView.richLinkViewFrom.setLinkFromMeta(metaData)
//    }
//
//    private fun setPreviewWithoutMetaData(viewHolder: GroupieViewHolder) {
//        val textList = text.split("\\s+".toRegex())
//        textList.forEach {
//            if (it.contains("https://")) {
//                viewHolder.itemView.richLinkViewFrom.setLink(it, object: ViewListener {
//                    override fun onSuccess(status: Boolean) {
//                        meta = viewHolder.itemView.richLinkViewFrom.metaData
//                        meta!!.url = it // found bug and fixed
//                        storeMetaInFirebase()
//                    }
//
//                    override fun onError(e: Exception?) {
//                        Log.e("ChatFromItemsPreview", "An error has occured unfortunately$text")
//                        viewHolder.itemView.richLinkViewFrom.removeAllViews()
//                        if (e.toString().contains("Check your Internet")) {
//                            Log.e("ChatFromItemsPreview", "Internet connection : ${e.toString()}")
//                        } else {
//                            meta = MetaData()
//                        }
//                    }
//                })
//                return@forEach
//            }
//        }
//    }
//
//    private fun storeMetaInFirebase() {
//        val metaAttributes = HashMap<String, String>()
//        metaAttributes["Title"] = meta!!.title
//        metaAttributes["Description"] = meta!!.description
//        metaAttributes["Favicon"] = meta!!.favicon
//        metaAttributes["ImageUrl"] = meta!!.imageurl
//        metaAttributes["Sitename"] = meta!!.sitename
//        metaAttributes["Url"] = text // www instead of "http" bug resolver
//        metaAttributes["MediaType"] = meta!!.mediatype
//
//        val refFrom = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
//        val refTo = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")
//
//        refFrom.child("$key/messagePreview").setValue(metaAttributes)
//        refTo.child("$key/messagePreview").setValue(metaAttributes)
//
//    }
//}
//
//class ChatToItemsPreview(val chatMessage: ChatMessage, val fromId: String, val toId: String, val key: String): Item<GroupieViewHolder>() { //GroupieViewHolder is type parameter
//    var meta: MetaData? = null
//    val text = chatMessage.text
//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        if (chatMessage.messagePreview.isEmpty() and (meta == null)) {
//            setPreviewWithoutMetaData(viewHolder)
//        } else {
//            setPreviewWithMetaData(viewHolder)
//        }
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.preview_chat_to_row
//    }
//
//    private fun setPreviewWithMetaData(viewHolder: GroupieViewHolder) {
//        if (meta != null) {
//            viewHolder.itemView.richLinkViewTo.setLinkFromMeta(meta)
//            return
//        }
//
//        val mapMeta = chatMessage.messagePreview
//        val metaData = MetaData()
//        metaData.title = mapMeta["Title"]
//        metaData.description = mapMeta["Description"]
//        metaData.favicon = mapMeta["Favicon"]
//        metaData.imageurl = mapMeta["ImageUrl"]
//        metaData.sitename = mapMeta["Sitename"]
//        metaData.url = mapMeta["Url"]
//        metaData.mediatype = mapMeta["MediaType"]
//
//        viewHolder.itemView.richLinkViewTo.setLinkFromMeta(metaData)
//    }
//
//    private fun setPreviewWithoutMetaData(viewHolder: GroupieViewHolder) {
//        val textList = text.split("\\s+".toRegex())
//        textList.forEach {
//            if (it.contains("https://")) {
//                viewHolder.itemView.richLinkViewTo.setLink(it, object: ViewListener {
//                    override fun onSuccess(status: Boolean) {
//                        meta = viewHolder.itemView.richLinkViewTo.metaData
//                        meta!!.url = it // found bug and fixed
////                        storeMetaInFirebase()
//                    }
//
//                    override fun onError(e: Exception?) {
////                        val handler = android.os.Handler(Looper.getMainLooper())
////                        handler.post {
////                            viewHolder.itemView.richLinkViewTo.removeAllViews()
////                        }
//                        if (e.toString().contains("Check your Internet")) {
//                            Log.e("ChatFromItemsPreview", "Internet connection : ${e.toString()}")
//                        } else {
//                            meta = MetaData()
//                        }
//                    }
//                })
//                return@forEach
//            }
//        }
//    }
//
////    private fun storeMetaInFirebase() {
////        val metaAttributes = HashMap<String, String>()
////        metaAttributes["Title"] = meta!!.title
////        metaAttributes["Description"] = meta!!.description
////        metaAttributes["Favicon"] = meta!!.favicon
////        metaAttributes["ImageUrl"] = meta!!.imageurl
////        metaAttributes["Sitename"] = meta!!.sitename
////        metaAttributes["Url"] = text // www instead of "http" bug resolver
////        metaAttributes["MediaType"] = meta!!.mediatype
////
////        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
////        ref.child("$key/messagePreview").setValue(metaAttributes)
////    }
//}
class ChatFromItemsPreview(val chatMessage: ChatMessage, val suppliedMetaData: MetaData? = null): Item<GroupieViewHolder>() { //GroupieViewHolder is type parameter
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if (suppliedMetaData != null) {
            viewHolder.itemView.richLinkViewFrom.setLinkFromMeta(suppliedMetaData)
            return
        }

        val mapMeta = chatMessage.messagePreview
        val metaData = MetaData()
        metaData.title = mapMeta["Title"]
        metaData.description = mapMeta["Description"]
        metaData.favicon = mapMeta["Favicon"]
        metaData.imageurl = mapMeta["ImageUrl"]
        metaData.sitename = mapMeta["Sitename"]
        metaData.url = mapMeta["Url"]
        metaData.mediatype = mapMeta["MediaType"]

        viewHolder.itemView.richLinkViewFrom.setLinkFromMeta(metaData)
    }

    override fun getLayout(): Int {
        return R.layout.preview_chat_from_row
    }

}

class ChatToItemsPreview(val chatMessage: ChatMessage,  val suppliedMetaData: MetaData? = null): Item<GroupieViewHolder>() { //GroupieViewHolder is type parameter
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        if (suppliedMetaData != null) {
            viewHolder.itemView.richLinkViewTo.setLinkFromMeta(suppliedMetaData)
            return
        }

        val mapMeta = chatMessage.messagePreview
        Log.e("Error", mapMeta.toString())
        Log.e("Error", viewHolder.toString())
        val metaData = MetaData()
        metaData.title = mapMeta["Title"]
        metaData.description = mapMeta["Description"]
        metaData.favicon = mapMeta["Favicon"]
        metaData.imageurl = mapMeta["ImageUrl"]
        metaData.sitename = mapMeta["Sitename"]
        metaData.url = mapMeta["Url"]
        metaData.mediatype = mapMeta["MediaType"]

        viewHolder.itemView.richLinkViewTo.setLinkFromMeta(metaData)
    }

    override fun getLayout(): Int {
        return R.layout.preview_chat_to_row
    }

}