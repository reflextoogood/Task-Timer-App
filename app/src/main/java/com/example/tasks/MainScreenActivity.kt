package com.example.tasks


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasks.firestore.FirestoreClass
import com.example.tasks.models.Tasks
import com.example.tasks.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_screen.*


class MainScreenActivity : AppCompatActivity(),TaskAdapter.OnItemClickListener{

    private var doubleBackToExitPressedOnce = false

    private lateinit var usernametext:TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var floatbutton:FloatingActionButton
    private lateinit var name:String
    private lateinit var email:String
    private lateinit var id:String

    private companion object{
        private const val TAG="MainScreenActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        usernametext=findViewById(R.id.toptextid)
        floatbutton=findViewById(R.id.floatingActionButton)

        //to authenticate the query to retrieve list items


        //        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                id = profile.uid
                name = profile.displayName.toString()
                email = profile.email.toString()

            }
        }



//       this line is needed for the welcome screen title
        FirestoreClass().getUserDetails(this@MainScreenActivity)


        fetchdetails()


        floatbutton.setOnClickListener {
            startActivity(Intent(this@MainScreenActivity,TaskCreateActivity::class.java))
        }


        //This is for local storage of name of user
//        val sharedPreferences=getSharedPreferences(Constants.MY_TASK_PREFERENCES, Context.MODE_PRIVATE)
//        val username=sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")
//        usernametext.text="Welcome, $username!"

    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.milogout){
            Log.i(TAG,"Logout")
            //Logout the user
            auth.signOut()
            val logoutIntent=Intent(this,DashboardActivity::class.java)
            logoutIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
        }
        if(item.itemId==R.id.changepasswordid){
            startActivity(Intent(this@MainScreenActivity,ChangePasswordActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    fun userLoggedInSuccess(user: User){
        usernametext.text="Welcome, "+user.name+"!"
        Log.i("Name:",user.name)
        Log.i("Name:",user.email)
        Log.i("Name:",user.id)
    }

    fun fetchdetails(){
        //      These lines of code is for the recyclerview
        val db=Firebase.firestore
        db.collection("Tasks")
            .whereEqualTo("email",email)
            .orderBy("TimeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if(e!=null){
                    Log.w(TAG,"Listen Failed",e)
                    return@addSnapshotListener
                }
                if(snapshot != null) {
                    val taskList = ArrayList<Tasks>()
                    val documents = snapshot.documents
                    documents.forEach {
                        val spec = it.toObject(Tasks::class.java)
                        if(spec != null){
                            taskList.add(spec!!)
                        }
                    }
                    reuser.adapter=TaskAdapter(taskList,this,this)
                    reuser.layoutManager=LinearLayoutManager(this)
                }
            }

    }

    override fun onItemClick(position: Int) {
//        Toast.makeText(this,"Item Clicked", Toast.LENGTH_SHORT).show()
    }
}