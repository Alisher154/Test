package uz.texnopos.test

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import uz.texnopos.test.timer.CoroutineTimer
import uz.texnopos.test.timer.CoroutineTimerListener
import java.util.*
import kotlin.math.abs
import kotlin.random.Random.Default.nextInt

class DrawView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var mX = 0f
    private var mY = 0f

    private var mPath: Path? = null
    private val mPaint: Paint = Paint()
    private val paths = ArrayList<Stroke>()
    private var currentColor = 0
    private var strokeWidth = 0
    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private val mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var timer:CoroutineTimer?=null
    var width:Int?=null
    var height:Int?=null
    private var myPaths= arrayListOf<Pair<Int,Int>>()
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
        strokeWidth = 36
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


    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = Stroke(currentColor, strokeWidth, mPath!!)
        paths.add(fp)
        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y
        invalidate()
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
        invalidate()
    }

    private fun touchUp() {
        mPath!!.lineTo(mX, mY)
        invalidate()
    }
    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }


    private fun algorithm1(count:Int): List<Pair<Int, Int>> {
        val a = width!!/36
        val b = height!!/36
        val list = arrayListOf<Pair<Int, Int>>()
        var i=count
        while (i>0){
            val x = nextInt(1,width!!/36)*36
            val y = nextInt(1,height!!/36)*36
            val p=Pair(x,y)
            if (!myPaths.contains(p)&&!list.contains(p)) {
                list.add(p)
                i--
            }
        }

        return list
    }
    private fun algorithm2(): List<Pair<Int, Int>> {
        var k = 1
        var l = 0
        val a = width!!
        val b = height!!
        val list = arrayListOf<Pair<Int, Int>>()
        repeat(6) {
            for (i in k - 1..a/36) if (!myPaths.contains(Pair(i*36,k*36))) list.add(Pair(i*36, k*36))
            for (i in k..b/36) if (!myPaths.contains(Pair((a/36 - l)*36, i*36))) list.add(Pair((a/36 - l)*36, i*36))
            for (i in a/36 - l downTo k) if (!myPaths.contains(Pair(i*36, (b/36 - l)*36))) list.add(Pair(i*36, (b/36 - l)*36))
            for (i in b/36 - l downTo k + 1) if (!myPaths.contains(Pair(k*36, i*36))) list.add(Pair(k*36, i*36))
            k += 1
            l += 1
        }
        Log.d(TAG, "algorithm2: $list")
        return list
    }

    fun generate(speed: Long) {
        currentColor=Color.BLACK
        if (timer != null) timer!!.destroyTimer()
        paths.clear()
        myPaths.clear()
        timer = CoroutineTimer(object : CoroutineTimerListener {
            override fun onTick(timeLeft: Long?, error: Exception?) {
                val i = timeLeft!!.toInt()
                val generate=algorithm1(50)
                val x=generate[50-i].first
                val y=generate[50-i].second
                myPaths.add(Pair(x,y))
                touchStart(x * 1.0f, y * 1.0f)
                touchUp()
                if(i==1) Log.d(TAG, "onPause:size=${myPaths.size} $myPaths }")
            }
        })
        timer!!.startTimer(50L, speed)
    }

    fun algo1(speed: Long) {
        if (timer != null) timer!!.destroyTimer()
        val algorithm = algorithm1(205)
        val size = algorithm.size
        currentColor=Color.RED
        timer = CoroutineTimer(object : CoroutineTimerListener {
            override fun onTick(timeLeft: Long?, error: Exception?) {
                val i = timeLeft!!.toInt()
                val x = algorithm[size - i].first
                val y = algorithm[size-i].second
//                when (i) {
//                    size -> touchStart(x*1.0f,y*1.0f)
//                    0 -> touchUp()
//                    else -> touchMove(x*1.0f,y*1.0f)
//                }
                touchStart(x * 1.0f, y * 1.0f)
                touchUp()
            }
        })
        timer!!.startTimer(size.toLong(),speed)
    }
}
