package com.codepath.apps.twitterclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import android.view.WindowManager




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val FRAGMENT_TITLE = "title"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ComposeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComposeFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(FRAGMENT_TITLE)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get field from view
        val etCompose = view.findViewById<EditText>(R.id.etCompose)
        // Fetch arguments from bundle and set title
        val title = arguments?.getString(FRAGMENT_TITLE, "Enter tweet")
        dialog?.setTitle(FRAGMENT_TITLE)
        // Show soft keyboard automatically and request focus to field
        etCompose.requestFocus()
        dialog?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param title Fragment title.
         * @return A new instance of fragment ComposeFragment.
         */
        @JvmStatic
        fun newInstance(title: String) =
            ComposeFragment().apply {
                arguments = Bundle().apply {
                    putString(FRAGMENT_TITLE, title)
                }
            }
    }
}