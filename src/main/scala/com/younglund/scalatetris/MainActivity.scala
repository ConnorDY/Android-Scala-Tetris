package com.younglund.scalatetris

import android.app.Activity
import android.os.Bundle
	
class MainActivity extends Activity
{
	var paused = false
	var resumed = false
	var destroyed = false

	override def onCreate(savedInstanceState: Bundle) =
	{
		super.onCreate(savedInstanceState)
		requestWindowFeature(1) // no title bar
		setContentView(R.layout.main)
	}

	override def onPause() = 
	{
		super.onPause
		paused = true
	}

	override def onResume() =
	{
		super.onResume
		resumed = true
	}

	override def onDestroy() =
	{
		super.onDestroy
		destroyed = true
	}

	def callMe(thread: MainThread) =
	{
		if (paused)
		{
			paused = false
			thread.stop
		}

		if (resumed)
		{
			resumed = false
			thread.start
		}

		if (destroyed)
		{
			destroyed = false
			thread.stop
		}
	}
}