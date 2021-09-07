package views

import android.text.SpannableString
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.teachable.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.github.ponnamkarthik.richlinkpreview.MetaData
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import models.User
import java.lang.Exception
import java.util.regex.Pattern

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>() { //GroupieViewHolder is type parameter
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(viewHolder.itemView).load(user.profileImageUrl).apply(requestOptions).into(viewHolder.itemView.imageview_chat_from_row)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

        // load user image into the star
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(viewHolder.itemView).load(user.profileImageUrl).apply(requestOptions).into(viewHolder.itemView.imageview_chat_to_row)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}