package com.lasallecollegevancouver.myfragmentapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

// whichever class that listens in for the events from the fragment
interface FragmentListener
{
    fun removeButtonClicked()
}

class MyFragment : Fragment()
{
    companion object
    {
        fun instance() : MyFragment
        {
            return MyFragment()
        }
    }

    lateinit var listener: FragmentListener

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val myFragView = inflater.inflate(R.layout.my_fragment_layout,
                                          container,
                                          false)

        // do something with fragment
        val deleteFragButton: Button = myFragView.findViewById<Button>(R.id.deleteFragmentButton_id)

        deleteFragButton.setOnClickListener {
            listener.removeButtonClicked()
        }

        return myFragView
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)

        if(context is FragmentListener)
        {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
    }
}