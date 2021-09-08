package navdrawerfragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.teachable.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_latest_messages.view.*
import messages.ChatLogActivity
import models.ChatMessage
import views.LatestMessageRow


class LatestMessagesFragment: Fragment() {
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private var transactionComplete = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_latest_messages, container, false)
        listenForLatestMessages()
        view.layout_latest_messages.visibility = GONE
        view.recyclerview_latest_messages.adapter = adapter
        view.recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL)) // add line separation
        // set item click listener on your adapter
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(activity, ChatLogActivity::class.java)

            val row = item as LatestMessageRow

            intent.putExtra(ContactsFragment.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        view.layout_latest_messages.visibility = View.VISIBLE

        // change recyclerview durations
        // view.recyclerview_latest_messages.itemAnimator = null
//        view.recyclerview_latest_messages.itemAnimator!!.changeDuration = 0
//        view.recyclerview_latest_messages.itemAnimator!!.addDuration = 0
        return view
    }

    // private val latestMessagesMap = mutableMapOf<String, List<Any>>()
    private var keyPosList = mutableListOf<String>()
    private var prevChildChanged = listOf<Any>()
    private val defaultItemAnimator = DefaultItemAnimator()

    private fun refresh(currIndex: Int, chatMessage: ChatMessage, insertPos: Int = 0) {
        if (currIndex == -1) { // populate recyclerview on Create
            adapter.add(LatestMessageRow(chatMessage))
        } else if (currIndex == 0) { // adjust top of recyclerview
            view?.recyclerview_latest_messages?.itemAnimator = null // prevent blinking on first row
            (adapter.getItem(0) as LatestMessageRow).chatMessage.text = chatMessage.text
            adapter.notifyItemChanged(0)
        } else { // keeps transitioning animation
            view?.recyclerview_latest_messages?.itemAnimator = defaultItemAnimator
            adapter.removeGroupAtAdapterPosition(currIndex)
            adapter.add(insertPos, LatestMessageRow(chatMessage))
        }
//        adapter.removeGroupAtAdapterPosition(currIndex)
//        adapter.add(insertPos, LatestMessageRow(chatMessage))
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid // only need fromId because there is only one child under fromId node
        Log.e("chatMessageAdded", fromId!!)
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        ref.runTransaction(object: Transaction.Handler {
            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                if (p2 != null) {
                    Log.e("LatestMessages", p2.toString())
                    transactionComplete = true
                    val sortedChildren = p2.children.sortedBy { it.getValue(ChatMessage::class.java)!!.timeStamp }
                    sortedChildren.forEach {
                        keyPosList.add(it.key!!)
                        val chatMessage = it.getValue(ChatMessage::class.java)
                        refresh(-1, chatMessage!!)
                        Log.e("LatestMessages", chatMessage.text)
                    }
                }
            }

            override fun doTransaction(p0: MutableData): Transaction.Result {
                Log.e("TAG", "runTransactionCalled")
                return Transaction.success(p0)
            }

        })
        ref.orderByChild("timeStamp").addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                // if a new user messages us (or vice versa)
                // key is that of user who is messaging us (or the toId)
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                Log.e("chatMessageAdded", chatMessage.text)
                Log.e("chatMessageAdded", chatMessage.timeStamp.toString())

//                if (latestMessagesIndexMap.size == 0) {
//                    keyAtZero = p0.key.toString()
//                }
//
//                if (latestMessagesIndexMap.containsKey(p0.key)) {
////                    val currentLatestMessageIndex = latestMessagesIndexMap[p0.key!!]?.get(1)
////                    latestMessagesIndexMap[p0.key!!] = listOf(chatMessage, currentLatestMessageIndex!!)
//                    refreshRecyclerViewMessages(true, latestMessagesIndexMap[p0.key]!!, chatMessage)
//                } else {
//                    latestMessagesIndexMap[p0.key!!] = latestMessagesIndicies
//                    refreshRecyclerViewMessages(false, latestMessagesIndicies, chatMessage)
//                    latestMessagesIndicies ++
//                }

//                refresh(-1, chatMessage)
//                keyPosList.add(p0.key!!)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                view!!.recyclerview_latest_messages.clearAnimation()
                if (!transactionComplete) {
                    return
                }
                // if a new user messages us again
                // if we message the same user
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                val currIndex = keyPosList.indexOf(p0.key)
                Log.e("chatMessageChanged", chatMessage.text)
                if (prevChildChanged.isNotEmpty()) {
                    val prevKey = prevChildChanged[0]
                    val prevChat = prevChildChanged[1] as ChatMessage
                    if (chatMessage.timeStamp < prevChat.timeStamp) {
                        Log.e("chatMessageChanged", currIndex.toString())
                        refresh(currIndex, chatMessage)
                        if (currIndex != 0) {
                            keyPosList.add(0, keyPosList.removeAt(currIndex))
                        }
                    } else {
                        Log.e("chatMessageChangedElse", currIndex.toString())
                        val insertPos = keyPosList.indexOf(prevKey) + 1
                        refresh(currIndex, chatMessage, insertPos)
                        keyPosList.add(insertPos, keyPosList.removeAt(currIndex))
                    }
                } else {
                    Log.e("chatMessageChangedFirst", currIndex.toString())
                    refresh(currIndex, chatMessage)
                    if (currIndex != 0) {
                        keyPosList.add(0, keyPosList.removeAt(currIndex))
                    }
                }

                prevChildChanged = listOf(p0.key!!, chatMessage)

//                if (keyAtZero == p0.key) {
//                    refreshRecyclerViewMessages(true, latestMessagesIndexMap[p0.key.toString()]!!, chatMessage, true)
//                    return
//                }
//
//                refreshRecyclerViewMessages(true, latestMessagesIndexMap[p0.key]!!, chatMessage)
//                keyAtZero = p0.key.toString()
//                latestMessagesIndexMap.forEach { (t, u) ->
//                    if (t!= p0.key!! && latestMessagesIndexMap[t]!! < latestMessagesIndexMap[p0.key.toString()]!!) {
//                        latestMessagesIndexMap[t] = latestMessagesIndexMap[t]!! + 1
//                    }
//                }
//
//                latestMessagesIndexMap[p0.key!!] = 0
//
//                Log.e("LMHashMapAfter", latestMessagesIndexMap.toString())


            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }



//    private fun fetchCurrentUser() {
//        val uid = FirebaseAuth.getInstance().uid // get uid of current user
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//
//            override fun onDataChange(p0: DataSnapshot) { // whenever data at specific node changes ... changes when user logs in
//                NavHomeActivity.currentUser = p0.getValue(User::class.java)
//                // setUpToolbar()
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//            }
//        })
//    }

//    private fun setUpToolbar() {
//        // drawer_layout_custom.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
//        Glide.with(activity!!.applicationContext).load(NavHomeActivity.currentUser?.profileImageUrl).into(imageview_latest_message_toolbar) // load imageview
//
//        imageview_new_message_toolbar.setOnClickListener {
//            val intent = Intent(activity, NewMessageActivity::class.java)
//            startActivity(intent)
//        }
//
//        imageview_sign_out_toolbar.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(activity, RegisterActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//
//        Log.e("TAG", "done setting up toolbar")
//    }



}