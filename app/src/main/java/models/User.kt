package models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// parcelize implements all User fields into Parceable object
@Parcelize // done by including android extension in build.gradle file
class User(val uid: String, val username: String, val profileImageUrl: String, val email: String): Parcelable {
    constructor() : this("", "", "", "") // default no argument constructor
}