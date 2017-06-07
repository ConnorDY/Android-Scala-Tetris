package com.younglund.scalatetris

class Board()
{
	var width: Int = _
	var height: Int = _
	var linesCleared: Int = 0
	var grid:Array[Array[Int]] = Array(Array())

	def setSize(w: Int, h: Int)
	{
		width = w
		height = h

		grid = Array.ofDim[Int](width, height)
		for (x <- 0 until width; y <- 0 until height)
			grid(x)(y) = 0
	}

	def fillPos(pos: Doublet[Int, Int], colour: Int) =
	{
		if (
			pos._1 >= 0 &&
			pos._2 >= 0 &&
			pos._1 < width &&
			pos._2 < height
		) grid(pos._1)(pos._2) = colour
	}

	def isFree(pos: Doublet[Int, Int]): Int =
	{
		if (pos._2 < 0) return 0
		else if (
			pos._1 >= 0 &&
			pos._1 < width &&
			pos._2 < height
		)  return grid(pos._1)(pos._2)
		else return -1
	}

	def checkLine(y: Int):Option[Boolean] =
	{
		for (x <- 0 until width)
			if (grid(x)(y) == 0) return None

		shiftDown(y)
		return None
	}

	def shiftDown(y: Int) =
	{
		for (x <- 0 until width; y2 <- y to 1 by -1)
			grid(x)(y2) = grid(x)(y2 - 1)

		linesCleared += 1

		checkLine(y)
	}
}