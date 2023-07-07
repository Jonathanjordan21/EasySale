package com.example.easySale.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.easySale.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.example.easySale.*


class MainActivity : AppCompatActivity() {
    lateinit var mainMenu : MainMenu
    lateinit var label : Label
    private lateinit var statistic : Statistic

    private lateinit var bottomNavBar: BottomNavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mainMenu= MainMenu()
        label = Label()
        statistic = Statistic()


        init()
        Log.d("Main Menu Stats","${mainMenu.isVisible} ${mainMenu.isInLayout} ${mainMenu.isHidden}")
        Log.d("Label Stats","${label.isVisible} ${label.isInLayout} ${label.isHidden}")
        Log.d("statistic Stats","${statistic.isVisible} ${statistic.isInLayout} ${statistic.isHidden}")



    }

    private fun init(){
        bottomNavBar = findViewById(R.id.bottom_navbar)
        bottomNavBar.selectedItemId = R.id.home



//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
//        val navController = navHostFragment.navController
//        bottomNavBar.setupWithNavController(navController)


        bottomNavBar.setOnItemSelectedListener{
            supportFragmentManager.beginTransaction().apply {
                Log.d("CLicked Id : ", "${bottomNavBar.selectedItemId}")
                var id : Fragment? = null
                when (it.itemId) {
                    R.id.label -> {
                        replace(R.id.fragmentContainer, label)
                        id = label
                        TinyDB(this@MainActivity)
                            .remove(SUMMARY_DB.currentAcc(TinyDB(this@MainActivity)))
                        bottomNavBar.setBackgroundColor(resources.getColor(R.color.red, theme))
                    }
                    R.id.home -> {
                        replace(R.id.fragmentContainer, mainMenu)
                        id = mainMenu
                        TinyDB(this@MainActivity)
                            .remove(SUMMARY_DB.currentAcc(TinyDB(this@MainActivity)))
                        bottomNavBar.setBackgroundColor(resources.getColor(R.color.white, theme))
                    }
                    R.id.statistic -> {
                        replace(R.id.fragmentContainer, Statistic())
                        id = statistic
                        TinyDB(this@MainActivity)
                            .remove(SUMMARY_DB.currentAcc(TinyDB(this@MainActivity)))
                        bottomNavBar.setBackgroundColor(resources.getColor(R.color.blue_ice, theme))
                    }
                    R.id.receipt -> {
                        replace(R.id.fragmentContainer, Receipt())
                        id = null
                        bottomNavBar.setBackgroundColor(resources.getColor(R.color.green, theme))
                    }
                    else -> {}
                }

                val manager = supportFragmentManager

                for(fragment in manager.fragments){
                    Log.d("Looping ", "${fragment.id} $fragment")
                    if(fragment != id){
                        Log.d("Removing Fragment : ", "${fragment.id} $fragment")
                        remove(fragment)
                    } else Log.d("Comparing ", "$fragment $id")
                }

                commit()
            }
            true
        }
    }
}