package registerlogin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.teachable.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import messages.NavHomeActivity
import models.User
import java.util.*
import kotlin.system.exitProcess

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_an_account_text_view.setOnClickListener {
            //launch login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK) // pick an item from data
            intent.type = "image/*" // specify pick type
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was ...
            selectedPhotoUri = data.data // uri represents location of where image is stored on device
            val bitmap : Bitmap?
            bitmap = if (Build.VERSION.SDK_INT >= 28) {
                val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            }

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

        }
    }

    private fun performRegister() {
        Log.e("Register Activity", "Hello world again")
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        // if email and password fields are empty
        if (email.isEmpty() or password.isEmpty()) return // turn this into a when branch to notify user that field is empty

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                Log.e("Register Activity", "First Part Success")
                if (!it.isSuccessful) return@addOnCompleteListener // nested function return (function inside onCreate())
                // else if successful
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.e("Register Activity", it.message)
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            Log.e("Register Activity", "No image selected")
            FirebaseAuth.getInstance().currentUser!!.delete()
            return // do not add user to database if they do not import a profile pic from camera roll
        }

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$fileName")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                TODO("what to do when url fails")
            }

        Log.e("Will this actually work", ref.child("/image/$fileName").toString())
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: "" //elvis operator
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            uid,
            username_edittext_register.text.toString(),
            profileImageUrl
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.e("RegisterActivity", "Added user to Firebase Database")


                val intent = Intent(this, NavHomeActivity::class.java)
                //clear off previous activities on activities stack
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.e("RegisterActivity", it.message)
            }
    }
}

//class User(val uid: String, val username: String, val profileImageUrl: String) { // put this into a module file
//    constructor() : this("", "", "") // default no argument constructor
//}
