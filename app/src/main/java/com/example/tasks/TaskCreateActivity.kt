package com.example.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class TaskCreateActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    private lateinit var calcbutton:ImageButton
    private lateinit var calctxt:EditText
    private lateinit var time:EditText
    private lateinit var floatingbtn:FloatingActionButton
    private lateinit var auth: FirebaseAuth
    private lateinit var name:String
    private lateinit var email:String
    private lateinit var id:String
    private lateinit var TaskNewlyEntered:EditText

    var day=0
    var month=0
    var year=0
    var hour=0
    var minute=0

    var saveday=0
    var savemonth=0
    var saveyear=0
    var savehour=0
    var saveminute=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_create)

        calcbutton=findViewById(R.id.calendarid)
        calctxt=findViewById(R.id.dateenterid)
        time=findViewById(R.id.timeenterid)
        floatingbtn=findViewById(R.id.floatingActionButton2)
        TaskNewlyEntered=findViewById(R.id.taskenterid)

        pickdate()
        submit()
    }
    private fun getDateTimeCalendar(){
        val cal=Calendar.getInstance()
        day=cal.get(Calendar.DAY_OF_MONTH)
        month=cal.get(Calendar.MONTH)
        year=cal.get(Calendar.YEAR)
        hour=cal.get(Calendar.HOUR)
        minute=cal.get(Calendar.MINUTE)

    }
    private fun pickdate(){
        calcbutton.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this,this,year,month,day).show()
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        saveday=dayOfMonth
        savemonth=month
        saveyear=year
        getDateTimeCalendar()
        TimePickerDialog(this,this,hour,minute,true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savehour=hourOfDay
        saveminute= minute
        time.setText("$savehour:$saveminute")
        calctxt.setText("$saveday/$savemonth/$saveyear")
    }

    private fun submit() {

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user?.let {
            for (profile in it.providerData) {
                id = profile.uid
                name = profile.displayName.toString()
                email = profile.email.toString()

            }
        }

        val db = Firebase.firestore

        floatingbtn.setOnClickListener {
            val hashMap= HashMap<String,Any>()
            hashMap.put("Title",TaskNewlyEntered.text.toString())
            hashMap.put("date",calctxt.text.toString())
            hashMap.put("email",email)
            hashMap.put("time",time.text.toString())
            hashMap.put("TimeStamp", FieldValue.serverTimestamp())
            db.collection("Tasks")
                .add(hashMap)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                    Toast.makeText(this,"Successfully added Title ${TaskNewlyEntered.text}", Toast.LENGTH_SHORT).show()
                    val goBackIntent= Intent(this,MainScreenActivity::class.java)
                    goBackIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(goBackIntent)
                }
                .addOnFailureListener {
                        e -> Log.w(ContentValues.TAG, "Error writing document", e)
                    Toast.makeText(this,"There was an error adding data!!", Toast.LENGTH_SHORT).show()
                }
        }
    }

}