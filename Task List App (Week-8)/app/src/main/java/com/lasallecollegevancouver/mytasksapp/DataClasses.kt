package com.lasallecollegevancouver.mytasksapp

data class Item (val name: String,
                 var completed: Boolean = false)

data class Group (val name: String,
                  var items: MutableList<Item> = mutableListOf())