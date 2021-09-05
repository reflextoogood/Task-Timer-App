package com.example.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.tasks.models.Tasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main_screen.*
import java.util.*
import kotlin.collections.HashMap

class TaskEditOrDeleteActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var calctxt: EditText
    private lateinit var time: EditText
    private lateinit var title_task: EditText
    private lateinit var floatingbtn: FloatingActionButton
    private lateinit var calcbutton: ImageButton
    private lateinit var del_button: Button
    private lateinit var complete_button: Button
    private lateinit var aEmail: String
    private lateinit var aTitle: String
    private lateinit var aTime: String
    private lateinit var aDate: String
    private lateinit var idDocument: String

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var saveday = 0
    var savemonth = 0
    var saveyear = 0
    var savehour = 0
    var saveminute = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_edit_or_delete)

        title_task = findViewById(R.id.task_enter_update_id)
        calcbutton = findViewById(R.id.calendar_update_id)
        calctxt = findViewById(R.id.date_enter_update_id)
        time = findViewById(R.id.time_enter_update_id)
        floatingbtn = findViewById(R.id.floatingActionButton3)
        del_button = findViewById(R.id.deleted_button)
        complete_button = findViewById(R.id.completed_button)

        var intent = intent
        aTitle = intent.getStringExtra("Title").toString()
        aEmail = intent.getStringExtra("Email").toString()
        aDate = intent.getStringExtra("Date").toString()
        aTime = intent.getStringExtra("Time").toString()

        title_task.setText(aTitle)
        calctxt.setText(aDate)
        time.setText(aTime)

        pickdate_update()
        submit_update()

    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        saveday = dayOfMonth
        savemonth = month
        saveyear = year
        getDateTimeCalendar()
        TimePickerDialog(this, this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savehour = hourOfDay
        saveminute = minute
        time.setText("$savehour:$saveminute")
        calctxt.setText("$saveday/$savemonth/$saveyear")
    }

    fun pickdate_update() {
        calcbutton.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }
    }

    fun submit_update() {

        val db = Firebase.firestore
        db.collection("Tasks")
            .whereEqualTo("email", aEmail)
            .whereEqualTo("Title", aTitle)
            .whereEqualTo("date", aDate)
            .whereEqualTo("time", aTime)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen Failed", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val taskList = ArrayList<Tasks>()
                    val documents = snapshot.documents
                    documents.forEach {
                        idDocument = it.id
                    }
//                    Toast.makeText(this,"", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"There was an error fetching data!!", Toast.LENGTH_SHORT).show()
                }

            }

        floatingbtn.setOnClickListener {
            val a:String = title_task.text.toString()
            val b:String = calctxt.text.toString()
            val c:String = time.text.toString()
            val hashMap= HashMap<String,String>()
            hashMap.put("Title",a)
            hashMap.put("date",b)
            hashMap.put("time",c)
            db.collection("Tasks")
                .document(idDocument)
                .set(hashMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    Toast.makeText(this,"Successfully Updated Title ${title_task.text}", Toast.LENGTH_SHORT).show()
                    val goBackIntent= Intent(this,MainScreenActivity::class.java)
                    goBackIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(goBackIntent)
                }
                .addOnFailureListener {
                        e -> Log.w(TAG, "Error writing document", e)
                        Toast.makeText(this,"There was an error updating data!!", Toast.LENGTH_SHORT).show()
                }
        }

        complete_button.setOnClickListener {
            db.collection("Tasks").document(idDocument)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully completed!")
                    Toast.makeText(this,"${title_task.text} is Completed", Toast.LENGTH_SHORT).show()
                    val goBackIntent= Intent(this,MainScreenActivity::class.java)
                    goBackIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(goBackIntent)
                }
                .addOnFailureListener {
                        e -> Log.w(TAG, "Error completing document", e)
                    Toast.makeText(this,"There was some error!!", Toast.LENGTH_SHORT).show()
                }
        }

        del_button.setOnClickListener {
            db.collection("Tasks").document(idDocument)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    Toast.makeText(this,"${title_task.text} Deleted", Toast.LENGTH_SHORT).show()
                    val goBackIntent= Intent(this,MainScreenActivity::class.java)
                    goBackIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(goBackIntent)
                }
                .addOnFailureListener {
                        e -> Log.w(TAG, "Error completing document", e)
                    Toast.makeText(this,"There was some error!!", Toast.LENGTH_SHORT).show()
                }
        }

    }
}