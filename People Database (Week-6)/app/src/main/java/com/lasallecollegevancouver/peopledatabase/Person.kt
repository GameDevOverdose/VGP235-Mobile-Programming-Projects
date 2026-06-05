package com.lasallecollegevancouver.peopledatabase

class Person(
    var name: String,
    var lastName: String? = "",
    var age: Int? = -1,
    var height: Float? = 0.0f,
    var address: String? = "",
    var eyeColor: String? = "",
    var dadName: String? = "",
    var momPhone: String? = ""
)
{
    fun personContains(query: String): Boolean
    {
        val q = query.lowercase()

        return name.lowercase().contains(q) ||
               lastName.orEmpty().lowercase().contains(q) ||
               age?.toString()?.contains(q) == true ||
               height?.toString()?.contains(q) == true ||
               address.orEmpty().lowercase().contains(q) ||
               eyeColor.orEmpty().lowercase().contains(q) ||
               dadName.orEmpty().lowercase().contains(q) ||
               momPhone?.contains(q) == true
    }
}