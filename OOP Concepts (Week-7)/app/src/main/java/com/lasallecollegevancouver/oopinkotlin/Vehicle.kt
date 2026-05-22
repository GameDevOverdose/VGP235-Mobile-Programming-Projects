package com.lasallecollegevancouver.oopinkotlin

import java.util.UUID

class Vehicle()
{
    lateinit var name: String
    lateinit var color: String
    var year: Int? = null
    val uuid: UUID = UUID.randomUUID()

    constructor(n: String, c: String, y: Int) : this()
    {
        this.name = n
        this.color = c
        this.year = y
    }

    constructor(map: HashMap<String, Any?>) : this()
    {
        (map["name"] as? String).let {
            this.name = it.toString()
        }

        (map["color"] as? String).let {
            this.color = it.toString()
        }

        (map["year"] as? Int).let {
            this.year = it
        }
    }
}