package com.example.tasks

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.tasks.models.Tasks


class TaskAdapter(private val tasklist:ArrayList<Tasks>,private val listener:OnItemClickListener,val context: Context):RecyclerView.Adapter<TaskAdapter.ViewHolder>(){

     inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener{

        val title: TextView = itemView.findViewById(R.id.titleviewid)
        val date: TextView = itemView.findViewById(R.id.dateviewid)
        val time: TextView = itemView.findViewById(R.id.timeviewid)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
//                val intent = Intent(context,TaskEditOrDeleteActivity::class.java)
//                intent.putExtra()
//                startActivity(context,intent,null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_design,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = tasklist[position]
        holder.title.text=currentItem.Title
        holder.date.text=currentItem.date
        holder.time.text=currentItem.time

        holder.itemView.setOnClickListener {
            val intent = Intent(context,TaskEditOrDeleteActivity::class.java)
            intent.putExtra("Title",currentItem.Title)
            intent.putExtra("Email",currentItem.email)
            intent.putExtra("Time",currentItem.time)
            intent.putExtra("Date",currentItem.date)
            startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
        return tasklist.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}