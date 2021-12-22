package com.guhe.scalebarDemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.guhe.scalebar.ScaleBar
import com.guhe.scalebar.ScaleBar.OnScaleSlideListener
import com.guhe.scalebarDemo.R.*
import com.guhe.scalebarDemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, layout.activity_main)

        binding.bar1.apply {
            setShowScale(true)
            setProportion(1.5f)
            setLowText("小")
            setSlideProgress(3)
            setHighText("大")
            setPadding(5, 5, 5, 5)
//            setScales(arrayOf("1"))
            setOnScaleSlideListener(object : OnScaleSlideListener {
                override fun onBeforeSliding(position: Int, selectedScale: String?) {
                    Log.i("6688", "开始滑动   = $selectedScale")
                }

                override fun onSliding(position: Int, selectedScale: String?) {
                    binding.bar2.setSlideProgress(position)
                    binding.bar3.setSlideProgress(position)
                    binding.bar4.setSlideProgress(position)
                    binding.bar5.setSlideProgress(selectedScale)
                    binding.bar6.setSlideProgress(selectedScale)


                    Log.i("6688", "  $selectedScale")
                }

                override fun onEndSliding(position: Int, selectedScale: String?) {
                    Log.i("6688", "结束滑动   = $selectedScale")
                }

            })
            setOnClickListener {
                /*scaleBar.setSlideTextSize(25)
                runOnUiThread     scaleBar.setPadding(50,50,50,50)
                }*/
                Toast.makeText(context, "点到了", Toast.LENGTH_SHORT).show()
            }
        }

        binding.sbBar.setOnScaleSlideListener(object :OnScaleSlideListener{
            override fun onSliding(position: Int, selectedScale: String?) {
                Log.i("123456", "  $position")
            }

            override fun onEndSliding(position: Int, selectedScale: String?) {
                Log.i("123456", "结束滑动  $position")
            }

            override fun onBeforeSliding(position: Int, selectedScale: String?) {
                Log.i("123456", "开始滑动  $position")
            }

        })

        binding.btnChange.setOnClickListener {
            binding.bar1.setScales(arrayOf("1","2","3"))
            binding.bar1.setDownToMove(true)
        }

    }
}