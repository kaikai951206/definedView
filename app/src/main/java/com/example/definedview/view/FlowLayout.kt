package com.example.definedview.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

class FlowLayout @JvmOverloads constructor(context: Context, attributeSet: AttributeSet) : ViewGroup(context, attributeSet) {
    companion object {
        const val TAG = "FlowLayout"
    }

    private val mHorizontalSpacing = dp2px(16)
    private val mVerticalSpacing = dp2px(8)

    //记录所有的行，一行一行的存储
    private var allLines = ArrayList<List<View>>()

    //记录每一行的行高
    private var lineHeights = ArrayList<Int>()

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }

    private fun initMeasureParams() {
        allLines = ArrayList<List<View>>()
        lineHeights = ArrayList<Int>()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        initMeasureParams()
        //度量子view的大小,度量所有子View

        val selfWidth = MeasureSpec.getSize(widthMeasureSpec)//viewgroup解析的宽度
        val selfHeight = MeasureSpec.getSize(heightMeasureSpec)//viewgroup解析的高度

        var lineViews = ArrayList<View>()//保存一行中所有的view
        var lineWidthUsed = 0 //记录这行已经使用了多宽的size
        var lineHeight = 0 //一行的行高

        var parentNeededWidth = 0 //measure过程中，子view要求的父viewGroup的宽
        var parentNeededHeight = 0 //measure过程中，子view要求的父viewGroup的高

        for (i in 0 until childCount) {
            val childView = getChildAt(i)

            val childLP = childView.layoutParams
            val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childLP.width)
            val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, childLP.height)

            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)

            //获取子view 的宽高
            val childMeasuredWidth = childView.measuredWidth
            val childMeasuredHeight = childView.measuredHeight

            //通过宽度来判断是否需要换行，通过换行后的每行的行高来获取整个viewGroup的行高
            //如果需要换行
            if (childMeasuredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {
                allLines.add(lineViews)
                lineHeights.add(lineHeight)
                //一旦换行，我们就可以判断当前行需要的宽和高了，所以此时要记录下来
                parentNeededHeight += lineHeight + mVerticalSpacing
                parentNeededWidth = max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing)

                lineViews = ArrayList<View>()
                lineWidthUsed = 0
                lineHeight = 0
            }
            // view 是分行layout的，所以要记录每一行有哪些view，这样可以方便layout布局
            lineViews.add(childView)
            //每行都会有自己的宽和高
            lineWidthUsed += childMeasuredWidth + mHorizontalSpacing
            lineHeight = max(lineHeight, childMeasuredHeight)

            //如果当前childView是最后一行的最后一个

            //如果当前childView是最后一行的最后一个
            if (i == childCount - 1) { //最后一行
                lineHeights.add(lineHeight)
                allLines.add(lineViews)
                parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed)
                parentNeededHeight += lineHeight
            }

        }

        //根据子View的度量结果，来重新度量自己ViewGroup
        // 作为一个ViewGroup，它自己也是一个View,它的大小也需要根据它的父亲给它提供的宽高来度量
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val realWidth = if (widthMode == MeasureSpec.EXACTLY) selfWidth else parentNeededWidth
        val realHeight = if (heightMode == MeasureSpec.EXACTLY) selfHeight else parentNeededHeight

        setMeasuredDimension(realWidth, realHeight)

    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        val lineCount = allLines.size
        var curL = 0
        Log.d(TAG, "onLayout: getLeft(): $left")
        var curT = 0
        Log.d(TAG, "onLayout: getTop(): $top")
        for (i in 0 until lineCount) {
            val lineViews = allLines[i]
            val lineHeight = lineHeights[i]
            for (j in lineViews.indices) {
                val view = lineViews[j]
                val left = curL
                val top = curT
                val bottom = top + view.measuredHeight
                val right = left + view.measuredWidth
                view.layout(left, top, right, bottom)
                curL = right + mHorizontalSpacing
            }
            curL = 0
            curT += lineHeight + mVerticalSpacing
        }

    }
}
















