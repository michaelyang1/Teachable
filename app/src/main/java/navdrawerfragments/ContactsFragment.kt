package navdrawerfragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.teachable.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_contacts.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import messages.ChatLogActivity
import models.User


class ContactsFragment: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        fetchUsers(view)
        return view
    }

    companion object { // similar to @staticmethod in Python
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(view: View) {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        ref.orderByChild("username").addListenerForSingleValueEvent(object: ValueEventListener { // anonymous class
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        if (user.uid != currentUserUid)
                            adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view -> // item refers to contact row

                    val userItem = item as UserItem // cast item as UserItem object

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    // finish() //return back to main screen
                }
                view.recyclerview_contacts.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    class UserItem(val user: User): Item<GroupieViewHolder>() { //needs to implement groupieviewholder interface methods
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.username_textview_new_message.text = user.username
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            Glide.with(viewHolder.itemView).load(user.profileImageUrl).apply(requestOptions).into(viewHolder.itemView.imageView_new_message)
        }

        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }
    }
}