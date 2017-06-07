package com.younglund.scalatetris

import android.content.Context
import android.util.AttributeSet
import android.view.{View, SurfaceView, SurfaceHolder, GestureDetector, MotionEvent}

class MainView(context: Context, attrs: AttributeSet) extends SurfaceView(context, attrs) 
{
	val holder = getHolder
	val thread = new MainThread(holder, context)
	
	holder addCallback (new SurfaceHolder.Callback
	{
		def surfaceCreated(holder: SurfaceHolder) =
		{
			thread.start
		}
		def surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) =
		{
			thread.setCanvasSize(width, height)
		}
		def surfaceDestroyed(holder: SurfaceHolder) = {}
	})
	
	setFocusable(true)
	setLongClickable(true)
	setGesture()

	def setGesture() =
	{
		val gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener()
		{
			override def onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean =
			{
				//thread.addFling(velocityX, velocityY)
				return true
			}

			override def onDown(e: MotionEvent): Boolean =
			{
				thread.clicked((e.getX, e.getY))
				return true
			}
		})
		setOnTouchListener(new View.OnTouchListener()
		{
			def onTouch(v: View, e: MotionEvent): Boolean = gd.onTouchEvent(e)
		})
	}

	def onDraw() =
	{
		passThreadToMainActivity
	}

	def passThreadToMainActivity()
	{
		context.asInstanceOf[MainActivity].callMe(thread)
	}
}