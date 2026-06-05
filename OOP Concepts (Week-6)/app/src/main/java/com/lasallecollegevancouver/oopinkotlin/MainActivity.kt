package com.lasallecollegevancouver.oopinkotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.UUID

class MainActivity : AppCompatActivity() {

    fun findVehicle(vehicles: Array<Vehicle>, findName: String)
    {
        for (i in 0 until vehicles.size) {

            if(findName == vehicles[i].name)
            {
                Log.d("vehicle", "${findName}'s id is: ${vehicles[i].uuid}")
                return
            }
        }

        Log.d("vehicle", "${findName} not found in database")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findVehicle(AppData.vehicles, "Ford")

        findViewById<Button>(R.id.myButton_id).setOnClickListener {
            val intent = Intent(this, OtherActivity::class.java)

            startActivity(intent)
        }

        //val myUser = User("John", "j@gmail.com", 23)

        //myUser.name = ""
        //myUser.name = "j@hotmail.com"

        //myUser.findFriend();
    }
}

class User (var name: String,
            var email: String,
            var age: Int,
            val uuid: UUID = UUID.randomUUID())
{
    fun printName()
    {
        Log.d("tag", name)
    }

    fun findFriend()
    {
        var f = Friend()
        val myFriend = Friend("", 23)
    }
}

class Friend ()
{
    var name: String? = null
    lateinit var email: String
    var age: Int = 0
    val uuid: UUID = UUID.randomUUID()

    init {
        name = "default"
    }

    constructor(n: String, e: String) : this()
    {
        this.name = n
        this.email = e
    }

    constructor(e: String, a: Int) : this()
    {
        this.email = e
        this.age = a

        Log.d("UUID", "We created your user and their id is: ${this.uuid}")
    }
}