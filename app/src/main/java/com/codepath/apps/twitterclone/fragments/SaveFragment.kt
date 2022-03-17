package com.codepath.apps.twitterclone.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.codepath.apps.twitterclone.R
import com.codepath.apps.twitterclone.interfaces.SaveTweetDialogListener

class SaveFragment : DialogFragment() {

    lateinit var saveTweetDialogListener: SaveTweetDialogListener
    lateinit var draft: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonSave(view)
        setButtonDelete(view)
    }

    fun setButtonSave(view: View) {
        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveTweetDialogListener.onSaveDialog(true)
        }
    }

    fun setButtonDelete(view: View) {
        view.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            saveTweetDialogListener.onSaveDialog(false)
        }
    }

    companion object {
        fun newInstance() =
            SaveFragment().apply {
            }
    }
}