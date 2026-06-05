package com.lasallecollegevancouver.mytasksapp

import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

fun GroupsActivity.newGroup() : View.OnClickListener
{
    return View.OnClickListener{
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New Group")
        builder.setTitle("Enter the name for your new group")

        val myInput = EditText(this)
        builder.setView(myInput)

        builder.setPositiveButton("Add") { _, _ ->
            val newGroup = Group(myInput.text.toString())
            AppData.groups.add(newGroup)

            groupsAdapter.notifyDataSetChanged()
            groupsAdapter.notifyItemInserted(AppData.groups.count())
        }

        builder.setNegativeButton("Cancel") {_, _ ->}
        builder.create().show()
    }
}