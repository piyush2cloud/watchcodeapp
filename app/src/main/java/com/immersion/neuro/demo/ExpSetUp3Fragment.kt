package com.immersion.neuro.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.immersion.neuro.R

class ExpSetUp3Fragment : Fragment() {
    private val args: ExpSetUp3FragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exp_set_up3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val cbChecking = view.findViewById<CheckBox>(R.id.cbChecking)
        val cbDataRcv = view.findViewById<CheckBox>(R.id.cbDataRcv)
        val cbCalc = view.findViewById<CheckBox>(R.id.cbCalc)
        args.progress?.let {
            tvStatus.text = it.plus("%")
            val pVal = it.toInt()
            if (pVal == 75) {
                cbChecking.isChecked = true
                cbDataRcv.isChecked = true
            }
            if (pVal == 100) {
                cbChecking.isChecked = true
                cbDataRcv.isChecked = true
                cbCalc.isChecked = true
            }
            progressBar.progress = pVal
        }
    }

}