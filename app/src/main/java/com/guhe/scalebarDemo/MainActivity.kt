package com.guhe.scalebarDemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.guhe.scalebar.ScaleBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scaleBar = findViewById<ScaleBar>(R.id.bar1)
        val scaleBar2 = findViewById<ScaleBar>(R.id.bar2)
        val scaleBar3 = findViewById<ScaleBar>(R.id.bar3)
        val scaleBar4 = findViewById<ScaleBar>(R.id.bar4)
        val scaleBar5 = findViewById<ScaleBar>(R.id.bar5)
        val scaleBar6 = findViewById<ScaleBar>(R.id.bar6)
        scaleBar?.apply {
            setShowScale(true)
            setProportion(1.5f)
            setLowText("小")
            setSlideProgress(3)
            setHighText("大")
            setPadding(5,5,5,5)
//            setScales(arrayOf("1"))
            setOnScaleSlideListener(object : ScaleBar.OnScaleSlideListener {
                override fun onBeforeSliding(position: Int, selectedScale: String?) {
                    Log.i("6688", "开始滑动   = $selectedScale")
                }

                override fun onSliding(position: Int, selectedScale: String?) {
                    scaleBar2.setSlideProgress(position)
                    scaleBar3.setSlideProgress(position)
                    scaleBar4.setSlideProgress(position)
                    scaleBar5.setSlideProgress(selectedScale)
                    scaleBar6.setSlideProgress(selectedScale)


                    Log.i("6688", "  $selectedScale")
                }

                override fun onEndSliding(position: Int, selectedScale: String?) {
                    Log.i("6688", "结束滑动   = $selectedScale")
                }

            })
            setOnClickListener {
                scaleBar.setSlideTextSize(25)
                runOnUiThread {
                    scaleBar.setPadding(50,50,50,50)
                }
                Toast.makeText(context, "点到了", Toast.LENGTH_SHORT).show()
            }
        }
    }
}