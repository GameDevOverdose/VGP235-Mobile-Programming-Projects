package com.lasallecollegevancouver.countriesrvapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class HomeActivity : AppCompatActivity(), RowListener {

    lateinit var selectedCountrytextView: TextView
    lateinit var selectedCountryImageView: ImageView

    override fun onRowClicked(index: Int)
    {
        selectedCountrytextView.text = AppData.countries[index]
        AppData.countryImages[AppData.countries[index]]?.let {
            selectedCountryImageView.setImageResource(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        val countriesRv = findViewById<RecyclerView>(R.id.countriesRv_id)
        countriesRv.layoutManager = LinearLayoutManager(this)
        countriesRv.adapter = CountriesAdapter(this)

        selectedCountrytextView = findViewById<TextView>(R.id.textView_id)
        selectedCountrytextView.text = ""

        selectedCountryImageView = findViewById<ImageView>(R.id.flagImageView_id)
    }
}