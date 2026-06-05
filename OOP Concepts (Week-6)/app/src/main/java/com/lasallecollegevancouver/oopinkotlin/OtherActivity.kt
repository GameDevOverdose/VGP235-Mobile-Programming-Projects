package com.lasallecollegevancouver.oopinkotlin

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class OtherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.other_activity)

        findViewById<TextView>(R.id.textView_id).text = AppData.Companion.vehicles.count().toString()

        val myMap: HashMap<String, Any?> = hashMapOf("name" to "Mercedes",
            "color" to "Black",
            "BodyType" to "Sedan",
            "Year" to 2010,
            "VIN" to null,
            "Country" to "USA")

        val name: String = myMap["name"] as String
        val color: String = myMap["name"] as String
        val year: Int? = myMap["Year"] as? Int

        AppData.Companion.vehicles.add(Vehicle(name, color, year))
    }
}