package com.lasallecollegevancouver.mytasksapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemsActivity : AppCompatActivity() , TaskListener
{
    var groupIndex: Int = 0

    override fun setCheckBox(taskItem: Item, checkBoxImageView: ImageView)
    {
        checkBoxImageView.setImageResource(
            if (!taskItem.completed)
                R.drawable.checkbox_cross
            else
                R.drawable.checkbox_tick
        )
    }

    override fun onTaskClicked(taskItem: Item, checkBoxImageView: ImageView)
    {
        taskItem.completed = !taskItem.completed;

        setCheckBox(taskItem, checkBoxImageView)
    }

    override fun onTaskLongClicked(taskIndex: Int)
    {
        AppData.groups[groupIndex].items.removeAt(taskIndex)

        taskAdapter.notifyDataSetChanged()
    }

    override fun onResume()
    {
        super.onResume()
    }

    lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.items_layout)

        val groupTitleTextView: TextView = findViewById<TextView>(R.id.groupTitleTextView_id)
        val backButton: Button = findViewById<Button>(R.id.backButton_id)

        val newItemText: EditText = findViewById<EditText>(R.id.newItemText_id)
        val addButton: Button = findViewById<Button>(R.id.addButton_id)
        val tasksRv: RecyclerView = findViewById<RecyclerView>(R.id.tasksBy_id)

        groupIndex = intent.getIntExtra("groupIndex", 0)
        val groupName = AppData.groups[groupIndex].name

        groupTitleTextView.text = groupName

        backButton.setOnClickListener {
            finish()
        }

        tasksRv.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(groupIndex, this)
        tasksRv.adapter = taskAdapter

        addButton.setOnClickListener {
            val newTask: String = newItemText.text.toString()

            if(newTask != "" && !AppData.IsTaskPresent(groupIndex, newTask))
            {
                AppData.groups[groupIndex].items.add((Item(newTask)))

                newItemText.text.clear()
                taskAdapter.notifyDataSetChanged()
            }
        }
    }
}