package com.immersion.neuro.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.immersion.neuro.R

class Demo1Activity : AppCompatActivity() {
    val destList = arrayListOf<Int>(
        R.id.loginFragment,
        R.id.magicNumberFragment,
        R.id.onboardin2Fragment,
        R.id.onboarding3Fragment,
        R.id.onboardingFragment,
        R.id.onborading4Fragment,
        R.id.pairBLE2Fragment,
        R.id.pairBLEScreenFragment,
        R.id.pairSamsungFragment,
        R.id.pairTestFragment,
        R.id.pairWearOSFragment,
        R.id.signupFragment,
        R.id.test1Fragment,
        R.id.testFragment,
        R.id.test2Fragment,
        R.id.testResultFragment,
        R.id.demoActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo1)
        val navController = findNavController(R.id.nav_host_fragment)

        val next = findViewById<ImageView>(R.id.btnNavigate)
        var index = 0
        next.setOnClickListener {
            if (index < destList.size - 1) {
                index++
                navController.navigate(destList[index])
            }
        }
    }
}