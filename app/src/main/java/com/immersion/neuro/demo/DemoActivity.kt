package com.immersion.neuro.demo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.immersion.neuro.R

class DemoActivity : AppCompatActivity() {

    val destList = arrayListOf<Int>(
        R.id.navigation_home,
        R.id.homeReportsFragment,
        R.id.homeReportListFragment,
        R.id.homePendingAddFragment,
        R.id.asyncMagicFragment,
        R.id.asyncMagicReadyFragment,
        R.id.asyncMagicEndFragment,
        R.id.expSetupFragment,
        R.id.asyncMobile1Fragment,
        R.id.homePendingConnectFragment,
        R.id.homePendingLiveFragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        val next = findViewById<ImageView>(R.id.btnNavigate)
        var index = 0
        next.setOnClickListener {
            if (index < destList.size - 1) {
                index++
                navController.navigate(destList[index])
            } else {
                index++
                if (index == destList.size)
                    navController.navigate(R.id.expSetupFragment, bundleOf().apply {
                        putString("progress", "75")
                    })
                else if (index == destList.size + 1)
                    navController.navigate(R.id.expSetupFragment, bundleOf().apply {
                        putString("progress", "100")
                    })
            }
        }
    }
}