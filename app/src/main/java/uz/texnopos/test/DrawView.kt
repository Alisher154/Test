package uz.texnopos.test

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DrawView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var mPath: Path? = null
    private val mPaint: Paint = Paint()
    private val paths = ArrayList<Stroke>()
    private var currentColor = 0
    private var strokeWidth = 0
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)

    init {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.SQUARE
        mPaint.alpha = 0xff
    }

    fun init(height: Int, width: Int) {
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)

        currentColor = Color.BLACK
        strokeWidth = 4
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        val backgroundColor = Color.WHITE
        mCanvas!!.drawColor(backgroundColor)

        try {
            for (fp in paths) {
                mPaint.color = fp.color
                mPaint.strokeWidth = fp.strokeWidth.toFloat()
                mCanvas!!.drawPath(fp.path, mPaint)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        canvas.drawBitmap(mBitmap!!, 0f, 0f, mBitmapPaint)
        canvas.restore()
    }

    private fun determine(x: Float, y: Float) {
        mPath = Path()
        val fp = Stroke(currentColor, strokeWidth, mPath!!)
        paths.add(fp)
        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mPath!!.lineTo(x, y)
    }

    private fun drawing(x: Int, y: Int) {
        determine(x * 1f, y * 1f)

        invalidate()
    }

    private fun algorithm1(): List<Pair<Int, Int>> {
        val k=200
        val a = mBitmap!!.width - k
        val b = mBitmap!!.height - k
        val list = arrayListOf<Pair<Int, Int>>()
        for (i in k..a step 4) list.add(Pair(i, k))
        for (i in k..b step 4) list.add(Pair(a, i))
        for (i in a downTo k step 4) list.add(Pair(i, b))
        for (i in b downTo k step 4) list.add(Pair(k, i))
        return list
    }
    private fun algorithm2(): List<Pair<Int, Int>> {
        var k=50
        var l=0
        val a = mBitmap!!.width - k
        val b = mBitmap!!.height - k
        val list = arrayListOf<Pair<Int, Int>>()
        repeat(5){
            for (i in k-50..a-l step 4) list.add(Pair(i, k))
            for (i in k..b-l step 4) list.add(Pair(a-l, i))
            for (i in a-l downTo k step 4) list.add(Pair(i, b-l))
            for (i in b-l downTo k+50 step 4) list.add(Pair(k, i))
            k+=50
            l+=50
        }

        return list
    }

    fun algo1(speed: Long) {
        paths.clear()
        val algorithm = algorithm2()
        Timer(speed) {it,timer->
            if (it<algorithm.size) {
                val x = algorithm[it].first
                val y = algorithm[it].second
                drawing(x, y)
            }
            else {
                timer.cancel()
            }
        }.startTimer()
    }
}
