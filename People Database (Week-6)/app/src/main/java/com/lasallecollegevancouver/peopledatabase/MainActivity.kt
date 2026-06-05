package com.lasallecollegevancouver.peopledatabase

import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import org.w3c.dom.Text
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat

class MainActivity : AppCompatActivity() {

    fun FetchPeopleFromQuery(query: String) : Person?
    {
        for (person in AppData.people)
        {
            if (person.personContains(query))
            {
                return person
            }
        }

        return null
    }

    fun updateTextFields(query: String, textFields: MutableList<TextView>)
    {
        val personFound: Person? = FetchPeopleFromQuery(query)

        if (personFound == null)
        {
            textFields.forEach { it.text = "" }
            return
        }

        // Update each TextView with the corresponding value
        textFields[0].text = "Name: ${personFound.name}"
        textFields[1].text = "Last Name: ${personFound.lastName.orEmpty()}"
        textFields[2].text = "Age: ${personFound.age.toString()}"
        textFields[3].text = "Height: ${personFound.height.toString()}"
        textFields[4].text = "Address: ${personFound.address.orEmpty()}"
        textFields[5].text = "Eye Color: ${personFound.eyeColor.orEmpty()}"
        textFields[6].text = "Dad Name: ${personFound.dadName.orEmpty()}"
        textFields[7].text = "Mom Phone: ${personFound.momPhone ?: "N/A"}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val queryEditText = findViewById<EditText>(R.id.queryText_id)
        val parentLayout = findViewById<LinearLayout>(R.id.queryDisplay_id)

        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.afacad)
        val textFields = mutableListOf<TextView>()

        var query: String = ""
        var count: Int = 0

        val labels = listOf(
            "Name: ",
            "Last Name: ",
            "Age: ",
            "Height: ",
            "Address: ",
            "Eye Color: ",
            "Dad Name: ",
            "Mom Phone: "
        )

        for (i in labels.indices) {
            val textView = TextView(this)
            textView.text = labels[i]
            textView.textSize = 22f
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setTypeface(typeface)

            val param = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            param.setMargins(50, 50, 50, 0)
            textView.layoutParams = param

            textFields.add(textView)
            parentLayout.addView(textView)
        }

        queryEditText.addTextChangedListener { editable ->
            query = editable.toString()
            count++

            updateTextFields(query, textFields)
        }
    }
}