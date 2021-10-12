package uz.texnopos.test

import android.app.Dialog
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import uz.texnopos.test.databinding.ActivityMainBinding
import uz.texnopos.test.databinding.DialogSizeBinding
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private var mWidth: Int? = null
    private var mHeight: Int? = null
    private var orientation: Int = 0
    private var speed =80L
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
                btnSize!!.setOnClickListener {
                    showDialog()
                }
            }

            image1.apply {
                spinner.adapter = adapter
                drawView.viewTreeObserver()
            }
            image2.apply {
                spinner.adapter = adapter
                drawView.viewTreeObserver()
            }

            slider.addOnChangeListener { _, value, _ ->
                image1.drawView.speed=1000/value.toLong()
                image2.drawView.speed=1000/value.toLong()
            }

            btnGenerate.onClick {
                if (mWidth!=null&&mHeight!=null){
                    image1.drawView.generate()
                    image2.drawView.generate()
                }
            }
            image1.drawView.onClick {

            }
            image2.drawView.onClick {

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
                        this.width=mWidth!!+25
                        this.height=mHeight!!+25
                    }
                    image2.drawView.apply {
                        this.width=mWidth!!+25
                        this.height=mHeight!!+25
                    }
                }
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    bind.edtWidth!!.setText(if (mWidth != null) mWidth.toString() else "")
                    bind.edtHeight!!.setText(if (mHeight != null) mHeight.toString() else "")
                }
                this@viewTreeObserver.init(height, width)
            }
        })
    }

    private fun showDialog() {
        Dialog(this).apply {
            val dBind = DialogSizeBinding.inflate(LayoutInflater.from(context))
            setContentView(dBind.root)
            dBind.apply {
                edtWidth.setText(if (mWidth != null) mWidth.toString() else "")
                edtHeight.setText(if (mHeight != null) mHeight.toString() else "")
                tvCancel.onClick { dismiss() }
                tvOk.onClick {
                    val validate = when {
                        edtWidth.checkIsEmpty() -> {
                            toast("Введите длину")
                            false
                        }
                        edtHeight.checkIsEmpty() -> {
                            toast("Введите высоту")
                            false
                        }
                        else -> true
                    }
                    if (validate) {
                        mWidth = edtWidth.textToString().toInt()
                        mHeight = edtHeight.textToString().toInt()
                        bind.apply {
                            image1.drawView.apply {
                                this.width=mWidth!!+25
                                this.height=mHeight!!+25
                            }
                            image2.drawView.apply {
                                this.width=mWidth!!+25
                                this.height=mHeight!!+25
                            }
                        }
                        dismiss()
                    }
                }
            }
            show()
        }
    }

    private fun validate(): Boolean {
        return when {
            mWidth == null -> {
                toast("Введите длину")
                false
            }
            mHeight == null -> {
                toast("Введите высоту")
                false
            }
            else -> true
        }
    }

}