package com.aviasales.test.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aviasales.test.R
import com.aviasales.test.features.search.view.SearchFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SearchFragment.newInstance()).commit()
        }
    }

}