package info.aiavci.twitterfeedapp

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet

class MenuItemView: CardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}