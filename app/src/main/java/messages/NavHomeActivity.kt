package messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.teachable.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_nav_home.*
import kotlinx.android.synthetic.main.nav_header.*
import models.User
import navdrawerfragments.ArchivesFragment
import navdrawerfragments.ContactsFragment
import navdrawerfragments.LatestMessagesFragment
import navdrawerfragments.SettingsFragment
import registerlogin.LoginActivity
import registerlogin.RegisterActivity

class NavHomeActivity : AppCompatActivity() {

    companion object { // can only be accessed by class name, not by instances
        var currentUser: User? = null
    }

    private var fragmentToSetTag: String? = null
    private var prevItemTag: String? = null


    override fun onCreate(savedInstanceState: Bundle?){
//        var handler = Handler()
//        handler.postDelayed({
//            Log.e("TAG", transactionComplete.toString())
//        }, 1500)
//        Log.e("LatestMessages", "oncreate is called")
        super.onCreate(savedInstanceState)
        // check if user is signed in
        if (!verifyUserIsLoggedIn()) {
            return
        }
        //retrieve current user
        fetchCurrentUser()


        // if this is a saved instance state
        if (savedInstanceState != null) {
            with (savedInstanceState) {
                prevItemTag = getString("prevItemTag")
                Log.e("savedInstanceState", "prevItemTag: $prevItemTag")
            }
        }

        Log.e("LatestMessages", "set content view is called")
        setContentView(R.layout.activity_nav_home)

        // set support action bar
        setSupportActionBar(toolbar)
        // action bar hamburger icon rotate
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        // set default fragment layout
        manageFragmentTransaction("latestMessagesFragTag")
        // set up navigation drawer ui
        nav_view.post {
            Glide.with(this).load(currentUser?.profileImageUrl).into(imageview_nav_header)
            textview_nav_header.text = "Hi, " + currentUser?.username + "!"
            nav_view.setCheckedItem(R.id.nav_chats)
        }
        // add drawer listeners
        addNavDrawerListeners()


    }

    private fun manageFragmentTransaction(selectedFrag: String) {
        when (selectedFrag) {
            "contactsFragTag" -> {
                supportFragmentManager.fragments.forEach {
                    Log.e("fragments", it.toString())
                }
                if (supportFragmentManager.findFragmentByTag("contactsFragTag") != null) {
                    Log.e("TAG", "already there")
                    supportFragmentManager.beginTransaction()
                        .show(supportFragmentManager.findFragmentByTag("contactsFragTag")!!)
                        .commit()
                } else {
                    Log.e("TAG", "not there yet")
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, ContactsFragment(), "contactsFragTag")
                        .commit()
                }

//                if (supportFragmentManager.findFragmentByTag("latestMessagesFragTag") != null) { // combine these if statements
//                    Log.e("TAG", "already there part 2")
//                    supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag("latestMessagesFragTag")!!).commit()
//                }
            }

            "latestMessagesFragTag" -> {
                supportFragmentManager.fragments.forEach {
                    Log.e("fragments", it.toString())
                }
                if (supportFragmentManager.findFragmentByTag("latestMessagesFragTag") != null) {
                    supportFragmentManager.beginTransaction()
                        .show(supportFragmentManager.findFragmentByTag("latestMessagesFragTag")!!)
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction().add(
                        R.id.fragment_container,
                        LatestMessagesFragment(),
                        "latestMessagesFragTag"
                    ).commit()
                }

            }

            "archivesFragTag" -> {
                if (supportFragmentManager.findFragmentByTag("archivesFragTag") != null) {
                    supportFragmentManager.beginTransaction()
                        .show(supportFragmentManager.findFragmentByTag("archivesFragTag")!!)
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, ArchivesFragment(), "archivesFragTag")
                        .commit()
                }
            }

            "settingsFragTag" -> {
                if (supportFragmentManager.findFragmentByTag("settingsFragTag") != null) {
                    supportFragmentManager.beginTransaction()
                        .show(supportFragmentManager.findFragmentByTag("settingsFragTag")!!)
                        .commit()
                } else {
                    Log.e("TAG", "settings fragment generated")
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, SettingsFragment(), "settingsFragTag")
                        .commit()
                }
            }
        }

        if (prevItemTag != null && prevItemTag != selectedFrag) {
            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag(prevItemTag)!!).commit()
        }
        // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ContactsFragment()).commit()
        fragmentToSetTag = null
        prevItemTag = selectedFrag
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

    private fun addNavDrawerListeners() {
        // set up navigation view listener
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_chats -> {
                    Log.e("TAG", "chats selected")
                    fragmentToSetTag = "latestMessagesFragTag"
                }

                R.id.nav_contacts -> {
                    Log.e("TAG", "contacts selected")
                    fragmentToSetTag = "contactsFragTag"
                }

                R.id.nav_archives -> {
                    Log.e("TAG", "archives selected")
                    fragmentToSetTag = "archivesFragTag"
                }

                R.id.nav_settings -> {
                    Log.e("TAG", "settings selected")
                    fragmentToSetTag = "settingsFragTag"
                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

        // add drawer layout listener
        drawer_layout.addDrawerListener(object: DrawerLayout.DrawerListener {
            override fun onDrawerClosed(drawerView: View) { // prevent drawer lag
                Log.e("TAG", "fragmentToSetTag: $fragmentToSetTag, prevItemTag: $prevItemTag")
                if (fragmentToSetTag != null && fragmentToSetTag != prevItemTag) {
                    manageFragmentTransaction(fragmentToSetTag!!)
                }
            }
            override fun onDrawerOpened(drawerView: View) {
            }
            override fun onDrawerStateChanged(newState: Int) {
            }
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }
        })
    }

//    // private val latestMessagesMap = mutableMapOf<String, List<Any>>()
//    private val latestMessagesIndexMap = HashMap<String, Int>() //maps key to index on recyclerview adapter
//    private var keyPosList = mutableListOf<String>()
//    private var prevChildChanged = listOf<Any>()



//    private fun refreshRecyclerViewMessages(keyAlreadyExists: Boolean, currIndex: Int, chatMessage: ChatMessage, sameToUser: Boolean = false) {
//        if (keyAlreadyExists) {
//            Log.e("LMPos", currIndex.toString())
//            Log.e("LMHashMapBefore", latestMessagesIndexMap.toString())
//            adapter.removeGroup(currIndex)
//            adapter.add(0, LatestMessageRow(chatMessage))
//            if (sameToUser) {
//                adapter.notifyDataSetChanged()
//            } else {
//                adapter.notifyItemChanged(0)
//            }
//
//        } else {
//            adapter.add(LatestMessageRow(chatMessage))
//        }
//    }
//
//    private fun refresh(currIndex: Int, chatMessage: ChatMessage, insertPos: Int = 0) {
//        if (currIndex == -1) { // populate recyclerview on Create
//            adapter.add(LatestMessageRow(chatMessage))
//            return
//        }
//
//        adapter.removeGroupAtAdapterPosition(currIndex)
//        adapter.add(insertPos, LatestMessageRow(chatMessage))
//
//        if (currIndex == 0) {
//            adapter.notifyDataSetChanged()
//        } else {
//            adapter.notifyItemChanged(0)
//        }
//    }
//
//    private fun listenForLatestMessages() {
//        val fromId = FirebaseAuth.getInstance().uid // only need fromId because there is only one child under fromId node
//        Log.e("chatMessageAdded", fromId!!)
//        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
//
//        ref.runTransaction(object: Transaction.Handler {
//            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
//                if (p2 != null) {
//                    Log.e("LatestMessages", p2.toString())
//                    transactionComplete = true
//                    val sortedChildren = p2.children.sortedBy { it.getValue(ChatMessage::class.java)!!.timeStamp }
//                    sortedChildren.forEach {
//                        keyPosList.add(it.key!!)
//                        val chatMessage = it.getValue(ChatMessage::class.java)
//                        refresh(-1, chatMessage!!)
//                    }
//                }
//            }
//
//            override fun doTransaction(p0: MutableData): Transaction.Result {
//                Log.e("TAG", "runTransactionCalled")
//                return Transaction.success(p0)
//            }
//
//        })
//        ref.orderByChild("timeStamp").addChildEventListener(object: ChildEventListener {
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                layout_latest_messages.visibility = VISIBLE
//                // if a new user messages us (or vice versa)
//                // key is that of user who is messaging us (or the toId)
//                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
//                Log.e("chatMessageAdded", chatMessage.text)
//                Log.e("chatMessageAdded", chatMessage.timeStamp.toString())
//
////                if (latestMessagesIndexMap.size == 0) {
////                    keyAtZero = p0.key.toString()
////                }
////
////                if (latestMessagesIndexMap.containsKey(p0.key)) {
//////                    val currentLatestMessageIndex = latestMessagesIndexMap[p0.key!!]?.get(1)
//////                    latestMessagesIndexMap[p0.key!!] = listOf(chatMessage, currentLatestMessageIndex!!)
////                    refreshRecyclerViewMessages(true, latestMessagesIndexMap[p0.key]!!, chatMessage)
////                } else {
////                    latestMessagesIndexMap[p0.key!!] = latestMessagesIndicies
////                    refreshRecyclerViewMessages(false, latestMessagesIndicies, chatMessage)
////                    latestMessagesIndicies ++
////                }
//
////                refresh(-1, chatMessage)
////                keyPosList.add(p0.key!!)
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//                if (!transactionComplete) {
//                    return
//                }
//                // if a new user messages us again
//                // if we message the same user
//                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
//                val currIndex = keyPosList.indexOf(p0.key)
//                Log.e("chatMessageChanged", chatMessage.text)
//                if (prevChildChanged.isNotEmpty()) {
//                    val prevKey = prevChildChanged[0]
//                    val prevChat = prevChildChanged[1] as ChatMessage
//                    if (chatMessage.timeStamp < prevChat.timeStamp) {
//                        refresh(currIndex, chatMessage)
//                        if (currIndex != 0) {
//                            keyPosList.add(0, keyPosList.removeAt(currIndex))
//                        }
//                    } else {
//                        val insertPos = keyPosList.indexOf(prevKey) + 1
//                        refresh(currIndex, chatMessage, insertPos)
//                        keyPosList.add(insertPos, keyPosList.removeAt(currIndex))
//                    }
//                } else {
//                    refresh(currIndex, chatMessage)
//                    if (currIndex != 0) {
//                        keyPosList.add(0, keyPosList.removeAt(currIndex))
//                    }
//                }
//
//                prevChildChanged = listOf(p0.key!!, chatMessage)
//
////                if (keyAtZero == p0.key) {
////                    refreshRecyclerViewMessages(true, latestMessagesIndexMap[p0.key.toString()]!!, chatMessage, true)
////                    return
////                }
////
////                refreshRecyclerViewMessages(true, latestMessagesIndexMap[p0.key]!!, chatMessage)
////                keyAtZero = p0.key.toString()
////                latestMessagesIndexMap.forEach { (t, u) ->
////                    if (t!= p0.key!! && latestMessagesIndexMap[t]!! < latestMessagesIndexMap[p0.key.toString()]!!) {
////                        latestMessagesIndexMap[t] = latestMessagesIndexMap[t]!! + 1
////                    }
////                }
////
////                latestMessagesIndexMap[p0.key!!] = 0
////
////                Log.e("LMHashMapAfter", latestMessagesIndexMap.toString())
//
//
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//            }
//            override fun onChildRemoved(p0: DataSnapshot) {
//            }
//            override fun onCancelled(p0: DatabaseError) {
//            }
//        })
//    }
//
//
//
//    private fun fetchCurrentUser() {
//        val uid = FirebaseAuth.getInstance().uid // get uid of current user
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//
//            override fun onDataChange(p0: DataSnapshot) { // whenever data at specific node changes ... changes when user logs in
//                currentUser = p0.getValue(User::class.java)
//                setUpToolbar()
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//            }
//        })
//    }
//
//    private fun setUpToolbar() {
//        // drawer_layout_custom.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
//        Glide.with(this).load(currentUser?.profileImageUrl).into(imageview_latest_message_toolbar) // load imageview
//
//        imageview_new_message_toolbar.setOnClickListener {
//            val intent = Intent(this, NewMessageActivity::class.java)
//            startActivity(intent)
//        }
//
//        imageview_sign_out_toolbar.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(this, RegisterActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//        }
//
//        Log.e("TAG", "done setting up toolbar")
//    }
//


    private fun verifyUserIsLoggedIn(): Boolean {
        // check if user is logged into the app
        val uid = FirebaseAuth.getInstance().uid
        return if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            // finish()
            false
        } else {
            true
        }
    }
//
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) { // if drawer is open
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (prevItemTag != "latestMessagesFragTag") { // if we are not on home fragment
            // REMEMBER TO ADD FOR ARCHIVES AND SETTINGS BRANCH
            supportFragmentManager.beginTransaction().show(supportFragmentManager.findFragmentByTag("latestMessagesFragTag")!!).commit()
            supportFragmentManager.beginTransaction().hide(supportFragmentManager.findFragmentByTag(prevItemTag)!!).commit()
            // change the selection marker
            nav_view.setCheckedItem(R.id.nav_chats)
//            prevItemId = R.id.nav_chats
            prevItemTag = "latestMessagesFragTag"
        } else { // if we are on home fragment
            super.onBackPressed()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        Log.e("savedinstancestate", "called")
        outState.run {
            putString("prevItemTag", prevItemTag)
        }
        super.onSaveInstanceState(outState)
    }

}
