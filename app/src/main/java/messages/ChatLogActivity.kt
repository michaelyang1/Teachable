package messages

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.teachable.R
import com.example.teachable.RichLinkViewSkype
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import io.github.ponnamkarthik.richlinkpreview.MetaData
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import kotlinx.android.synthetic.main.activity_chat_log.*
import models.ChatMessage
import models.User
import navdrawerfragments.ContactsFragment
import views.ChatFromItem
import views.ChatFromItemsPreview
import views.ChatToItem
import views.ChatToItemsPreview


class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
        var currentUser: User? = null
    }

    private val adapter = GroupAdapter<GroupieViewHolder>() // adapter is bridge between UI and data

    private var toUser : User? = null // changes for different person


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchCurrentUser()
        setContentView(R.layout.activity_chat_log)

        toUser = intent.getParcelableExtra<User>(ContactsFragment.USER_KEY)

        setUpClickListeners()

        recyclerview_chat_log.adapter = adapter

        listenForMessages()

        keyboardManagement()
    }


    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid //fromId changes depending on user, therefore we must have a ref and toRef
        val toId = toUser?.uid //toId also changes depending on user
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        // notifies every piece of data in messages node ... listen for new messages
        ref.addChildEventListener(object : ChildEventListener { // anonymous class

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) // retrieve chat message data

                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        Log.e(TAG, "FromID")
                        val currentUser = currentUser ?: return  // fix this
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                        if ("https://" in chatMessage.text) {
                            // retrieve metadata and store it in hashMap with key being url and metadata being value
                            // if image not loaded yet set recycled to false ... if image is (url and metadata url match), set to true

                            // OR STORE METADATA ATTRIBUTES (ARRAY?) IN FIREBASE DATABASE UNDER USER MESSAGES
                            // adapter.add(ChatFromItemsPreview(chatMessage, fromId!!, toId!!, p0.key!!))
                            addPreview(adapter, chatMessage, fromId!!, toId!!, p0.key!!, "from")
                        }
                    } else {
                        Log.e(TAG, "ToID")
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                        if ("https://" in chatMessage.text) {
                            addPreview(adapter, chatMessage, fromId!!, toId!!, p0.key!!, "to")
//                            if ("wen" in chatMessage.text) {
//                                val r = RichLinkViewSkype(applicationContext)
//                                r.setLinkTest("https://stackoverflow.com/questions/7149923/android-how-to-wait-for-code-to-finish-before-continuing", object: ViewListener {
//                                    override fun onSuccess(status: Boolean) {
//                                        Log.e("Debugging", "onSuccess")
//                                        Log.e("Debugging", r.metaData.imageurl)
//                                    }
//
//                                    override fun onError(e: Exception?) {
//                                        Log.e("Debugging", "onError")
//                                    }
//                                })
//                            }
//                            adapter.add(ChatToItemsPreview(chatMessage, fromId!!, toId!!, p0.key!!))
                            // why not attempt to set link first
                        }
                    }

                    recyclerview_chat_log.scrollToPosition(recyclerview_chat_log.adapter!!.itemCount -1)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    private fun addPreview(adapter: GroupAdapter<GroupieViewHolder>, chatMessage: ChatMessage, fromId: String, toId: String, key: String, fromTo: String) {
        val mapMeta = chatMessage.messagePreview
        // if chat message already has preview metadata attributes
        if (mapMeta.isNotEmpty()) {
            if (mapMeta["Url"] == "null") {
                Log.e("addPreview", "null")
                return
            } else {
                if (fromTo == "from") {
                    adapter.add(ChatFromItemsPreview(chatMessage))
                } else {
                    adapter.add(ChatToItemsPreview(chatMessage))
                }
                return
            }
        }

        // if chat message does not have preview metadata attributes
        val text = chatMessage.text.split("\\s+".toRegex())
        text.forEach {
            if (it.contains("https://")) {
                Log.e("twitter", it)
                val r = RichLinkViewSkype(applicationContext)
                r.setLinkTest(it, object: ViewListener {
                    override fun onSuccess(status: Boolean) {
                        Log.e("zillowUrl", r.metaData.url)
                        // r.metaData.url = it // prevent www instead of http bug
                        Log.e("zillowUrl", r.metaData.url)
                        if (fromTo == "from") {
                            Log.e("onSuccess", "from")
                            adapter.add(ChatFromItemsPreview(chatMessage, r.metaData))
                        } else {
                            adapter.add(ChatToItemsPreview(chatMessage, r.metaData))
                        }
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount -1)
                        storePreviewMetaInFirebase(r.metaData, it, fromId, toId, key)
                    }

                    override fun onError(e: Exception?) {
                        Log.e("twitter", e.toString())
                        storePreviewMetaInFirebase(MetaData(), "null", fromId, toId, key)
                    }
                })
                return
            }
        }
    }

    private fun storePreviewMetaInFirebase(meta: MetaData, text: String, fromId: String, toId: String, key: String) {
        val metaAttributes = HashMap<String, String>()
        metaAttributes["Title"] = meta.title
        metaAttributes["Description"] = meta.description
        metaAttributes["Favicon"] = meta.favicon
        metaAttributes["ImageUrl"] = meta.imageurl
        metaAttributes["Sitename"] = meta.sitename
        metaAttributes["Url"] = text // www instead of "http" bug resolver
        metaAttributes["MediaType"] = meta.mediatype

        val refFrom = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        val refTo = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")

        refFrom.child("$key/messagePreview").setValue(metaAttributes)
        refTo.child("$key/messagePreview").setValue(metaAttributes)
    }



    private fun performSendMessage() {
        Log.e("TAG", "performSendMessage called")
        val text = editText_chat_log.text.toString() // grab keyboard input

        if (text.isEmpty()) return // return if text field is empty

        val fromId = FirebaseAuth.getInstance().uid // current signed in user's id
        val user = intent.getParcelableExtra<User>(ContactsFragment.USER_KEY)
        val toId = user.uid // user we are sending message to id

        if (fromId == null) return

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push() // generate new node under app database
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").child(ref.key!!) // set same key as prior


//        val temp = HashMap<String, String>()
//        temp["Photo"] = "Imageurl"
        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, -System.currentTimeMillis() / 1000, HashMap()) // make negative time stamp ...
        Log.e("KEY : ", ref.key!!)
        Log.e("TO KEY : ", toRef.key!!)
        ref.setValue(chatMessage) // save chat message from sender's perspective in database
        toRef.setValue(chatMessage) // save chat message from receiver's perspective in database

        val latestMessagesRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val latestMessagesToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")

        latestMessagesRef.setValue(chatMessage) // since no push() was called, latest-messages ref node gets created every single time val changes
        latestMessagesToRef.setValue(chatMessage)

        editText_chat_log.text.clear() // clear edittext after message sent
    }


    private fun keyboardManagement() {
        // starts chat from bottom
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true // start chats at bottom of stack instead of top
        recyclerview_chat_log.layoutManager = layoutManager

        // pushes up recycler view when softkeyboard popups up
        recyclerview_chat_log.addOnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) { // if this is true, the keyboard has popped up
                Log.e("bottom", "$bottom")
                Log.e("oldBottom", "$oldBottom")
                recyclerview_chat_log.postDelayed(Runnable {
                    recyclerview_chat_log.scrollToPosition(
                        recyclerview_chat_log.adapter!!.itemCount -1) //push up recycler view to last text message or row
                }, 100)
            }
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid // get uid of current user
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) { // whenever data at specific node changes ... changes when user logs in
                currentUser = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun setUpClickListeners() {
        Glide.with(this).load(toUser?.profileImageUrl).into(imageview_chat_log_profile_img)

        textview_chat_log_username.text = toUser?.username

        send_button_chat_log.setOnClickListener {
            performSendMessage()
        }

        imageview_chat_log_back.setOnClickListener {
            onBackPressed()
        }


    }
}



//class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>() { //GroupieViewHolder is type parameter
//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        viewHolder.itemView.textview_from_row.text = text
//
//        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
//        Glide.with(viewHolder.itemView).load(user.profileImageUrl).apply(requestOptions).into(viewHolder.itemView.imageview_chat_from_row)
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.chat_from_row
//    }
//}
//
//class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
//    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
//        viewHolder.itemView.textview_to_row.text = text
//
//        // load user image into the star
//        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
//        Glide.with(viewHolder.itemView).load(user.profileImageUrl).apply(requestOptions).into(viewHolder.itemView.imageview_chat_to_row)
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.chat_to_row
//    }
//}