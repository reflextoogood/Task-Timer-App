package com.example.tasks.firestore

import android.app.Activity
import android.util.Log
import com.example.tasks.DashboardActivity
import com.example.tasks.MainScreenActivity
import com.example.tasks.RegisterActivity
import com.example.tasks.models.User
import com.example.tasks.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore=FirebaseFirestore.getInstance()
    fun registerUser(activity: RegisterActivity,userInfo: User){
            mFireStore.collection(Constants.USERS)
                .document(userInfo.id)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.userRegisterationtofirestore()
                }
                .addOnFailureListener { e->
                    activity.failed()
                }
    }
    fun registerUserfromsignin(activity: DashboardActivity,userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisterationtofirestore()
            }
            .addOnFailureListener { e->
                activity.failed()
            }
    }
    fun getCurrentUserID():String{
        //An instance of current user using FirebaseAuth
        val currentuser=FirebaseAuth.getInstance().currentUser

        //A variable to assign the currentUserId if it is not null or else it will be blank
        var currentUserID=""
        if(currentuser!=null){
            currentUserID=currentuser.uid
        }
        return currentUserID
    }
    fun getUserDetails(activity:Activity){
        //Here we pass the collection name from which we want the data
        mFireStore.collection(Constants.USERS)
        //The document id to get the Fields of user
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName,document.toString())

                //Here we have received the document snapshot which is converted into the user Data Model object.
                val user = document.toObject(User::class.java)!!

//                val sharedPreferences= activity.getSharedPreferences(
//                    Constants.MY_TASK_PREFERENCES,
//                    Context.MODE_PRIVATE
//                )
//
//                val editor:SharedPreferences.Editor=sharedPreferences.edit()
//                //Key: logged_in_username
//                //Value: Name
//                editor.putString(Constants.LOGGED_IN_USERNAME,"${user.name}")
//                editor.apply()

                //TODO Step 6: Pass the result to the MainScreenActivity Activity.
                //START
                when(activity){
                    is MainScreenActivity->{
                        //Call a function of base activity for transferring the result to it
                        activity.userLoggedInSuccess(user)
                    }
                }
                //END
            }
            .addOnFailureListener { e->
                //HIde the progress dialog and print error in log file
//                when(activity){
//                    is DashboardActivity->{
//                        activity.hide()
//                    }
//                }
                Log.e(activity.javaClass.simpleName,"Error while getting user details",e)
            }
    }
}