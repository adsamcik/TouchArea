package com.adsamcik.touchdelegate

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

/**
 * Touch delegate that can handle dragging/translation
 */
class DraggableTouchDelegate(private val mOffsetRect: Rect, override val view: View) :
		AbstractTouchDelegate {
	override val parentView: View = view.parent as? View
			?: throw ExceptionInInitializerError("View does not have a parent, it cannot be rootview!")

	/**
	 * If true all move events and up event are sent to the view
	 * If false no move event or up event is sent to view
	 * True or False value is set during down event
	 */
	private var mDelegateTargeted = false

	/**
	 * Returns current hit rectangle
	 *
	 * Value is not cached
	 */
	private val hitRect: Rect
		get() {
			val tX = view.x.toInt()
			val tY = view.y.toInt()
			return Rect(tX - mOffsetRect.left,
			            tY - mOffsetRect.top,
			            tX + view.width + mOffsetRect.right,
			            tY + view.height + mOffsetRect.bottom)
		}

	/**
	 * Updates offsets of the delegate
	 *
	 */
	@Suppress("UNUSED")
	fun updateOffsets(left: Int, top: Int, right: Int, bottom: Int) {
		mOffsetRect.left = left
		mOffsetRect.top = top
		mOffsetRect.right = right
		mOffsetRect.bottom = bottom
	}

	/**
	 * Updates offsets of the delegate
	 *
	 * @param rect Rectangle of values copied for the delegate
	 */
	@Suppress("UNUSED")
	fun updateOffsets(rect: Rect) {
		mOffsetRect.left = rect.left
		mOffsetRect.top = rect.top
		mOffsetRect.right = rect.right
		mOffsetRect.bottom = rect.bottom
	}

	/**
	 * Called when touch event occurs
	 */
	override fun onTouchEvent(event: MotionEvent): Boolean {
		var sendToDelegate = false
		when (event.action) {
			MotionEvent.ACTION_UP -> {
				if (mDelegateTargeted) {
					mDelegateTargeted = false
					sendToDelegate = true
				}
			}
			MotionEvent.ACTION_DOWN -> {
				if (hitRect.contains(event.x.toInt(), event.y.toInt())) {
					mDelegateTargeted = true
					sendToDelegate = true
				}
			}
			MotionEvent.ACTION_MOVE -> {
				if (mDelegateTargeted)
					sendToDelegate = true
			}
			MotionEvent.ACTION_CANCEL -> {
				mDelegateTargeted = false
				sendToDelegate = true
			}
		}

		if (sendToDelegate)
			return view.dispatchTouchEvent(event)

		return false
	}

}
