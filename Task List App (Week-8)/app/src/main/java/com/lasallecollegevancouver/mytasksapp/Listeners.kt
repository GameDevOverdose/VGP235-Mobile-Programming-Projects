package com.lasallecollegevancouver.mytasksapp

import android.widget.ImageView

interface GroupListener
{
    fun onGroupClicked (index: Int)
    fun onGroupLongClicked (index: Int)
}

interface TaskListener
{
    fun onTaskClicked (taskItem: Item, checkBoxImageView: ImageView)
    fun onTaskLongClicked (taskIndex: Int)
    fun setCheckBox(taskItem: Item, checkBoxImageView: ImageView)

}