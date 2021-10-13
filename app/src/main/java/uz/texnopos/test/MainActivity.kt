package uz.texnopos.test

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import uz.texnopos.test.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private var mWidth: Int? = null
    private var mHeight: Int? = null
    private var orientation: Int = 0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        orientation = resources.configuration.orientation
        val algorithms = arrayListOf("Algorithm 1", "Algorithm 2", "Algorithm 3")
        val adapter = ArrayAdapter(this@MainActivity,
            android.R.layout.simple_spinner_dropdown_item, algorithms)
        bind.apply {

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                btnSize!!.onClick {
                    showDialog()
                }
            }

            image1.apply {
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?,
                        position: Int, id: Long,
                    ) {
                        if (position>=0) drawView.algorithmType = position + 1
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // another interface callback
                    }
                }
                drawView.viewTreeObserver()
            }
            image2.apply {
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?, view: View?,
                        position: Int, id: Long,
                    ) {
                        if (position>=0) drawView.algorithmType = position + 1
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                drawView.viewTreeObserver()
            }

            slider.addOnChangeListener { _, value, _ ->
                image1.drawView.speed=1000/value.toLong()
                image2.drawView.speed=1000/value.toLong()
            }

            btnGenerate.onClick {
                if (validate()){
                    image1.drawView.apply {
                        width=mWidth
                        height=mHeight
                        generate()
                    }
                    image2.drawView.apply {
                        width=mWidth
                        height=mHeight
                        generate()
                    }
                }
            }
        }
    }

    fun DrawView.viewTreeObserver() {
        val vto = this.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                this@viewTreeObserver.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = this@viewTreeObserver.measuredWidth
                val height = this@viewTreeObserver.measuredHeight
                mWidth = width
                mHeight = height
                bind.apply {
                    image1.drawView.apply {
                        this.width=mWidth
                        this.height=mHeight
                    }
                    image2.drawView.apply {
                        this.width=mWidth
                        this.height=mHeight
                    }
                }
                bind.apply {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        edtWidth!!.setText(if (mWidth != null) mWidth.toString() else "")
                        edtHeight!!.setText(if (mHeight != null) mHeight.toString() else "")
                        edtWidth.doOnTextChanged { text, _, _, _ -> if (!text.isNullOrEmpty()) mWidth=text.toString().toInt()+25  }
                        edtHeight.doOnTextChanged { text, _, _, _ -> if (!text.isNullOrEmpty()) mHeight=text.toString().toInt()+25  }
                    }
                }

                this@viewTreeObserver.init(height, width)
            }
        })
    }

    private fun showDialog() {
        SizeDialog(this,mWidth,mHeight).apply {
            onClickSaveButton { width, height ->
                mWidth = width
                mHeight = height
            }
        }
    }

    private fun validate(): Boolean {
        if (orientation != Configuration.ORIENTATION_LANDSCAPE) return true
        return when {
            bind.edtWidth!!.checkIsEmpty() -> {
                toast("Введите длину")
                false
            }
            bind.edtHeight!!.checkIsEmpty() -> {
                toast("Введите высоту")
                false
            }
            else -> true
        }
    }

}