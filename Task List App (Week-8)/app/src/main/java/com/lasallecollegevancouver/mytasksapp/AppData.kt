package com.lasallecollegevancouver.mytasksapp

class AppData
{
    companion object
    {
        var groups: MutableList<Group> = mutableListOf()

        fun initialize()
        {
            val item1 = Item("Buy Food")
            val item2 = Item("Wash the Car", true)
            val item3 = Item("Call the Repairman")
            val item4 = Item("Write the Paper", true)
            val item5 = Item("Speak to Prof")
            val item6 = Item("Buy cake and beer", true)

            val group1 = Group("Home", mutableListOf(item1, item2, item3))
            val group2 = Group("School", mutableListOf(item4, item5))
            val group3 = Group("Party", mutableListOf(item6))
            val group4 = Group("Event on Jun 28th")

            groups.addAll(listOf(group1, group2, group3, group4))
        }

        fun IsTaskPresent(groupIndex: Int, taskToCheck: String): Boolean
        {
            if(Item(taskToCheck, false) in groups[groupIndex].items
                || Item(taskToCheck, true) in groups[groupIndex].items)
            {
                return true
            }

            return false
        }
    }
}