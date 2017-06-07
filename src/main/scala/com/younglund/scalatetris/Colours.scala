package com.younglund.scalatetris

import android.graphics.{Paint, Color}

class Colours
{
	val black = new Paint
	black.setColor(Color.BLACK)
	black.setStyle(Paint.Style.FILL)

	val pink200 = new Paint
	pink200.setARGB(255, 244, 143, 177)
	pink200.setStyle(Paint.Style.FILL)

	val deepPurple50 = new Paint
	deepPurple50.setARGB(255, 237, 231, 246)
	deepPurple50.setStyle(Paint.Style.FILL)
	deepPurple50.setTextSize(64)

	val deepPurple200 = new Paint
	deepPurple200.setARGB(255, 179, 157, 219)
	deepPurple200.setStyle(Paint.Style.FILL)

	val deepPurple600 = new Paint
 	deepPurple600.setARGB(255, 94, 53, 177)
 	deepPurple600.setStyle(Paint.Style.FILL)

 	val lightBlue200 = new Paint
	lightBlue200.setARGB(255, 129, 212, 250)
	lightBlue200.setStyle(Paint.Style.FILL)

	val green200 = new Paint
	green200.setARGB(255, 165, 214, 167)
	green200.setStyle(Paint.Style.FILL)

	val greenA200 = new Paint
	greenA200.setARGB(255, 105, 240, 174)
	greenA200.setStyle(Paint.Style.FILL)

	val greenA400 = new Paint
	greenA400.setARGB(255, 0, 230, 118)
	greenA400.setStyle(Paint.Style.FILL)

	val amber200 = new Paint
	amber200.setARGB(255, 255, 224, 130)
	amber200.setStyle(Paint.Style.FILL)
}