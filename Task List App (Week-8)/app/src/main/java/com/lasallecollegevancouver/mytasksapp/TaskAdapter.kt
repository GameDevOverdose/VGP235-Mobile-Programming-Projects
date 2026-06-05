package com.lasallecollegevancouver.mytasksapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskViewHolder (rootLayout: LinearLayout): RecyclerView.ViewHolder(rootLayout)
{
    lateinit var groupNameTextView: TextView

    init
    {
        groupNameTextView = itemView.findViewById(R.id.taskNameTextView_id)
    }
}

class TaskAdapter(val groupIndex: Int, val listener: TaskListener) : RecyclerView.Adapter<TaskViewHolder> ()
{
    override fun getItemCount(): Int = AppData.groups[groupIndex].items.count()

    override fun onCreateViewHolder(container: ViewGroup, p1: Int): TaskViewHolder
    {
        val groupRootLayout = LayoutInflater.from(container.context)
            .inflate(R.layout.tasks_layout,
                container,
                false)

        return TaskViewHolder(groupRootLayout as LinearLayout)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, taskIndex: Int)
    {
        val taskItem = AppData.groups[groupIndex].items[taskIndex]

        holder.groupNameTextView.text = taskItem.name

        val checkBoxImageView = holder.itemView.findViewById<ImageView>(R.id.checkBoxImageView_id)

        listener.setCheckBox(taskItem, checkBoxImageView)

        holder.itemView.setOnClickListener {
            listener.onTaskClicked(taskItem, checkBoxImageView)
        }

        holder.itemView.setOnLongClickListener {
            listener.onTaskLongClicked(taskIndex)
            true
        }
    }
}