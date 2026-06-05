package com.lasallecollegevancouver.peopledatabase

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class AppData
{

    companion object
    {
        val person_1 = Person(
            name = "Amber",
            lastName = "Smith",
            age = (20..40).random(),
            height = (150..190).random() / 100.0f,  // Random height between 1.50m and 1.90m
            address = "123 Maple Street",
            eyeColor = listOf("Blue", "Brown", "Green").random(),
            dadName = "John",
            momPhone = (100000000..999999999).random().toString()
        )

        val person_2 = Person(
            name = "Bert",
            lastName = "Johnson",
            age = (20..50).random(),
            height = (150..200).random() / 100.0f,
            address = "456 Oak Avenue",
            eyeColor = listOf("Hazel", "Brown", "Gray").random(),
            dadName = "Michael",
            momPhone = (100000000..999999999).random().toString()
        )

        val person_3 = Person(
            name = "Tania",
            lastName = "Williams",
            age = (18..35).random(),
            height = (145..180).random() / 100.0f,
            address = "789 Pine Road",
            eyeColor = listOf("Green", "Blue", "Brown").random(),
            dadName = "David",
            momPhone = (100000000..999999999).random().toString()
        )

        val person_4 = Person(
            name = "Cilvia",
            lastName = "Brown",
            age = (25..45).random(),
            height = (150..185).random() / 100.0f,
            address = "321 Elm Street",
            eyeColor = listOf("Blue", "Gray", "Hazel").random(),
            dadName = "Robert",
            momPhone = (100000000..999999999).random().toString()
        )

        var people: Array<Person> = arrayOf(
            person_1,
            person_2,
            person_3,
            person_4
        )
    }
}