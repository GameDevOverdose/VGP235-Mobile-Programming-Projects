package com.lasallecollegevancouver.mytasksapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GroupsActivity : AppCompatActivity(), GroupListener
{

    override fun onGroupClicked(index: Int)
    {
        val intent = Intent(this, ItemsActivity::class.java)

        intent.putExtra("groupIndex", index)
        startActivity(intent)
    }

    override fun onGroupLongClicked(index: Int)
    {
        AppData.groups.removeAt(index)
        groupsAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        groupsAdapter.notifyDataSetChanged()
    }

    lateinit var groupsAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.groups_layout)

        AppData.initialize()

        val newGroupButton = findViewById<Button>(R.id.newGroupButton_id)
        val groupsRv = findViewById<RecyclerView>(R.id.groupsBy_id)
        groupsRv.layoutManager = LinearLayoutManager(this)

        groupsAdapter = GroupAdapter(this)
        groupsRv.adapter = groupsAdapter

        newGroupButton.setOnClickListener(newGroup())
    }
}