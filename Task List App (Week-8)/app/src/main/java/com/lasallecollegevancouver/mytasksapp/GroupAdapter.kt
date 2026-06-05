package com.lasallecollegevancouver.mytasksapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupViewHolder (rootLayout : LinearLayout): RecyclerView.ViewHolder (rootLayout)
{
    lateinit var groupNameTextView: TextView
    lateinit var groupCountTextView: TextView
    lateinit var groupBottomDivider: View

    init {
        groupNameTextView = itemView.findViewById(R.id.groupNameTextView_id)
        groupCountTextView = itemView.findViewById(R.id.groupCountTextView_id)
        groupBottomDivider = itemView.findViewById(R.id.groupBottomDivider_id)
    }

    fun bind (group: Group, last: Boolean = false)
    {
        groupBottomDivider.visibility = View.VISIBLE

        if(last)
        {
            groupBottomDivider.visibility = View.INVISIBLE
        }

        val active = group.items.filterNot { it.completed }

        val verb: String = if (active.count() != 1) "are" else "is"
        val item: String = if (group.items.count() != 1) "items" else "item"

        groupNameTextView.text = group.name
        groupCountTextView.text = "From ${group.items.count()} $item, ${active.count()} $verb active"
    }
}

class GroupAdapter (val listener: GroupListener) : RecyclerView.Adapter<GroupViewHolder> ()
{
    override fun getItemCount(): Int = AppData.groups.count()

    override fun onCreateViewHolder(container: ViewGroup, p1: Int): GroupViewHolder
    {
        val groupRootLayout = LayoutInflater.from(container.context)
            .inflate(R.layout.group_row,
                container,
                false)

        return GroupViewHolder(groupRootLayout as LinearLayout)
    }

    override fun onBindViewHolder(viewHolder: GroupViewHolder, index: Int)
    {
        val last: Boolean = (AppData.groups.count() - 1 == index)
        val group = AppData.groups[index]
        viewHolder.bind(group, last)

        viewHolder.itemView.setOnClickListener {
            listener.onGroupClicked(index)
        }

        viewHolder.itemView.setOnLongClickListener {
            listener.onGroupLongClicked(index)
            true
        }
    }

}