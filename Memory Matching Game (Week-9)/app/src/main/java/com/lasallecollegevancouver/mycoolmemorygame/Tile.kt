package com.lasallecollegevancouver.mycoolmemorygame

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView

enum class Status
{
    HIDDEN, FLIPPED, MATCHED
}

data class Tile (val myContext: Context, var num: Int) : AppCompatTextView(myContext)
{
    var status = Status.HIDDEN

    fun updateTile()
    {
        when(status)
        {
            Status.HIDDEN -> this.text = "?"
            Status.MATCHED -> this.text = "✅"
            Status.FLIPPED -> this.text = "$num"
        }
    }

    fun SetStatus(status: Status)
    {
        this.status = status
        updateTile()
    }
}