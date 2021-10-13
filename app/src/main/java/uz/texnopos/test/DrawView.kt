package uz.texnopos.test

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import uz.texnopos.test.timer.CoroutineTimer
import uz.texnopos.test.timer.CoroutineTimerListener
import java.util.*
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
    private var timer: CoroutineTimer? = null
    var width: Int? = null
    var height: Int? = null
    var speed = 200L
    var algorithmType = 1
    private var myPaths = arrayListOf<Pair<Int, Int>>()

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
        strokeWidth = 25
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        mCanvas!!.drawColor(Color.WHITE)
        try {
            for (fp in paths) {
                mPaint.color = fp.color
                mPaint.strokeWidth = fp.strokeWidth.toFloat()
                mCanvas!!.drawPath(fp.path, mPaint)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        canvas.drawBitmap(mBitmap!!, -12.5f, -12.5f, mBitmapPaint)
        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x + 15
        val y = event.y + 15
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchPaths.clear()
                val p = Pair((x / strokeWidth).toInt(), (y / strokeWidth).toInt())
                when (algorithmType) {
                    1 -> algorithm1(p)
                    2 -> algorithm2(p)
                    3 -> algorithm3(p)
                }
                startDraw()
            }
        }
        return true
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

    private fun touchUp() {
        mPath!!.lineTo(mX, mY)
        invalidate()
    }

    private fun pick(): List<Pair<Int, Int>> {
        val a = width!! / strokeWidth
        val b = height!! / strokeWidth
        val list = arrayListOf<Pair<Int, Int>>()
        var i = a * b * 2 / 5
        if (a > 1 && b > 1)
            while (i > 0) {
                val x = nextInt(1, a)
                val y = nextInt(1, b)
                val p = Pair(x, y)
                if (!myPaths.contains(p) && !list.contains(p)) {
                    list.add(p)
                    i--
                }
            }

        return list
    }

    private fun startDraw() {
        if (timer != null) timer!!.destroyTimer()
        val algorithm = touchPaths
        val size = algorithm.size
        currentColor = Color.RED
        timer = CoroutineTimer(object : CoroutineTimerListener {
            override fun onTick(timeLeft: Long?, error: Exception?) {
                val i = timeLeft!!.toInt()
                val x = algorithm[size - i].first * strokeWidth
                val y = algorithm[size - i].second * strokeWidth
                touchStart(x * 1.0f, y * 1.0f)
                touchUp()
            }
        })
        timer!!.startTimer(size.toLong(), speed)
    }

    fun generate() {
        currentColor = Color.BLACK
        if (timer != null) timer!!.destroyTimer()
        paths.clear()
        myPaths.clear()
        val generate = pick()
        val size = generate.size
        timer = CoroutineTimer(object : CoroutineTimerListener {
            override fun onTick(timeLeft: Long?, error: Exception?) {
                val i = timeLeft!!.toInt()
                val x = generate[size - i].first
                val y = generate[size - i].second
                myPaths.add(Pair(x, y))
                touchStart(x * strokeWidth * 1.0f, y * strokeWidth * 1.0f)
                touchUp()
                if (i == 1) Log.d(TAG, "onPause:size=${myPaths.size} $myPaths }")
            }
        })
        timer!!.startTimer(size.toLong(), speed)
    }

    private val touchPaths = mutableListOf<Pair<Int, Int>>()
    private fun algorithm1(p: Pair<Int, Int>) {
        val t1 = p.first > 0 && p.first < width!! / strokeWidth
        val t2 = p.second > 0 && p.second < height!! / strokeWidth
        if (t1 && t2 && !touchPaths.contains(p) && !myPaths.contains(p)) {
            touchPaths.add(p)
            algorithm1(Pair(p.first + 1, p.second))
            algorithm1(Pair(p.first, p.second + 1))
            algorithm1(Pair(p.first - 1, p.second))
            algorithm1(Pair(p.first, p.second - 1))
        } else return
    }

    private fun algorithm2(p: Pair<Int, Int>) {
        val t1 = p.first > 0 && p.first < width!! / strokeWidth
        val t2 = p.second > 0 && p.second < height!! / strokeWidth
        if (t1 && t2 && !touchPaths.contains(p) && !myPaths.contains(p)) {
            touchPaths.add(p)
            algorithm2(Pair(p.first, p.second + 1))
            algorithm2(Pair(p.first + 1, p.second))
            algorithm2(Pair(p.first, p.second - 1))
            algorithm2(Pair(p.first - 1, p.second))
        } else return
    }


    private fun algorithm3(p: Pair<Int, Int>) {
        val t1 = p.first > 0 && p.first < width!! / strokeWidth
        val t2 = p.second > 0 && p.second < height!! / strokeWidth
        if (t1 && t2 && !touchPaths.contains(p) && !myPaths.contains(p)) {
            touchPaths.add(p)
            algorithm3(Pair(p.first + 1, p.second))
            algorithm3(Pair(p.first, p.second - 1))
            algorithm3(Pair(p.first - 1, p.second))
            algorithm3(Pair(p.first, p.second + 1))
        } else return
    }
}
