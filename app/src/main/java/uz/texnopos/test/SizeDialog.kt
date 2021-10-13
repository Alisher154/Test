package uz.texnopos.test

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import uz.texnopos.test.databinding.DialogSizeBinding

class SizeDialog(context: Context,private var width: Int?, private var height: Int? = null) : Dialog(context) {
    init {
        show()
    }
    private lateinit var bind: DialogSizeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DialogSizeBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.apply {
            edtWidth.setText(if (width != null) width.toString() else "")
            edtHeight.setText(if (height != null) height.toString() else "")
            tvOk.onClick {
                if (validate()) {
                    save.invoke(edtWidth.textToString().toInt(), edtHeight.textToString().toInt())
                    dismiss()
                }


            }
            tvCancel.onClick { dismiss() }
        }

    }

    private var save: (width: Int, height: Int) -> Unit = { _, _ -> }

    fun onClickSaveButton(save: (width: Int, height: Int) -> Unit) {
        this.save = save
    }

    private fun validate(): Boolean {
        return when {
            bind.edtWidth.checkIsEmpty() -> {
                context.toast("Введите длину")
                false
            }
            bind.edtHeight.checkIsEmpty() -> {
                context.toast("Введите высоту")
                false
            }
            else -> true
        }
    }
}