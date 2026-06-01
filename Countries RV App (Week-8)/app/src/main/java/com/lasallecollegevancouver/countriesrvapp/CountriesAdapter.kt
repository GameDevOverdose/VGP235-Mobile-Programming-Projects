package com.lasallecollegevancouver.countriesrvapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class CountryViewHolder (val linearLayout: ConstraintLayout): RecyclerView.ViewHolder(linearLayout)

class CountriesAdapter (val listener: RowListener) : RecyclerView.Adapter<CountryViewHolder>()
{
    override fun getItemCount(): Int = AppData.countries.count()

    override fun onCreateViewHolder(container: ViewGroup, p1: Int): CountryViewHolder
    {
        val countryTextView = LayoutInflater.from(container.context).inflate(R.layout.country_row,
            container, false) as ConstraintLayout

        return CountryViewHolder(countryTextView)
    }

    override fun onBindViewHolder(viewHolder: CountryViewHolder, index: Int)
    {
        val textView = viewHolder.itemView.findViewById<TextView>(R.id.countryNameTextView_id)
        val dividerView = viewHolder.itemView.findViewById<View>(R.id.dividerView_id)

        textView.text = AppData.countries[index]

        if (index == AppData.countries.count() - 1)
        {
            dividerView.visibility = View.GONE
        }
        else
        {
            dividerView.visibility = View.VISIBLE
        }

        viewHolder.itemView.setOnClickListener {

            textView.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(50)
                .withEndAction {
                    textView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(50)
                        .start()
                }
                .start()

            listener.onRowClicked(index)
        }
    }
}