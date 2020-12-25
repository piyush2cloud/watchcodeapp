package com.immersion.neuro.demo

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.immersion.neuro.R


class HomePendingAddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_pending_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val alert = ViewDialog()
        alert.showDialog(requireActivity())
    }

    class ViewDialog {
        fun showDialog(activity: Activity) {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.layout_add_magic_number)
            dialog.getWindow()?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val ivCancel = dialog.findViewById<ImageView>(R.id.ivCancel)
            ivCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }
}