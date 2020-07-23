package com.example.myapplication

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

class FlowLayout @JvmOverloads constructor(context: Context, attributeSet: AttributeSet) :
    ViewGroup(context, attributeSet) {
    private val mHorizontalSpace = 10
    private val mVerticalSpace = 10
    private var mOnItemClickListener: OnItemClickListener? = null


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val measuredHeightSize = MeasureSpec.getSize(heightMeasureSpec)
        val measuredWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val measuredHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        var realWidth = 0
        var realHeight = 0
        var lineWidth = 0
        var lineHeight = 0

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            val childWidth = childView.measuredWidth + mHorizontalSpace
            val childHeight = childView.measuredHeight + mVerticalSpace

            if (lineWidth + childWidth > measuredWidthSize) {
                //换行
                realWidth = max(lineWidth, childWidth)
                realHeight += lineHeight

                lineWidth = childWidth
                lineHeight = childHeight

            } else {
                lineWidth += childWidth
                lineHeight = max(lineHeight, childHeight)
            }

            //统计最后一行的宽度和高度
            if (i == childCount - 1) {
                realWidth = max(realWidth, childWidth)
                realHeight += lineHeight
            }
        }

        setMeasuredDimension(
            if (measuredWidthMode == MeasureSpec.EXACTLY) measuredWidthSize else realWidth,
            if (measuredHeightMode == MeasureSpec.EXACTLY) measuredHeightSize else realHeight
        )


    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var lineWidth = 0
        var lineHeight = 0
        var left = 0
        var top = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            childView.setOnClickListener {
                mOnItemClickListener?.onItemClick(childView)
            }
            val childWidth = childView.measuredWidth
            val childHeight = childView.measuredHeight

            if (lineWidth + childWidth + mHorizontalSpace > measuredWidth) {
                top += lineHeight + mVerticalSpace
                left = 0

                lineWidth = childWidth
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = max(lineHeight, childHeight)
            }

            childView.layout(left, top, left + childWidth, top + childHeight)
            left += childWidth + mHorizontalSpace

        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View)
    }


    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }


}