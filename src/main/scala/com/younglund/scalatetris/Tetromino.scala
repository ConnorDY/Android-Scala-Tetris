package com.younglund.scalatetris

import scala.collection.mutable.ArrayBuffer

import android.graphics.{Canvas, Paint, Rect}

object Tetrominoes
{
	val Colour = new Colours

	var shapes = ArrayBuffer[ArrayBuffer[Int]]()
	shapes += ArrayBuffer(Direction.RIGHT, Direction.DOWN, Direction.LEFT)
	shapes += ArrayBuffer(Direction.LEFT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT)
	shapes += ArrayBuffer(Direction.UP, Direction.DOWN, Direction.DOWN, Direction.RIGHT)
	shapes += ArrayBuffer(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.DOWN)
	shapes += ArrayBuffer(Direction.RIGHT, Direction.LEFT, Direction.LEFT, Direction.RIGHT, Direction.DOWN)

	val cols = Array(
		Colour.deepPurple600,
		Colour.pink200,
		Colour.deepPurple200,
		Colour.lightBlue200,
		Colour.green200,
		Colour.amber200
	)
}

class Tetromino(dirs: ArrayBuffer[Int])
{
	// Init vars
	val Colour = new Colours
	var col: Int = _

	var alive: Boolean = true

	var pos = Doublet(0, 0)

	var directions = dirs

	// Functions
	def setPos(p: Doublet[Int, Int]) =
	{
		if (alive) pos = p
	}

	def setColour(c: Int) =
	{
		col = c
	}

	def fall(board: Board) =
	{
		if (!willFit(board, Doublet(0, 1))) alive = false
		else setPos(Doublet(pos._1, pos._2 + 1))
	}

	def rotateClockwise() =
	{
		for (i <- 0 until directions.size)
		{
			val dir = directions(i)

			if (dir == Direction.RIGHT) directions(i) = Direction.DOWN
			else if (dir == Direction.DOWN) directions(i) = Direction.LEFT
			else if (dir == Direction.LEFT) directions(i) = Direction.UP
			else if (dir == Direction.UP) directions(i) = Direction.RIGHT
		}
	}

	def rotateCounterClockwise() =
	{
		for (i <- 0 until directions.size)
		{
			val dir = directions(i)

			if (dir == Direction.RIGHT) directions(i) = Direction.UP
			else if (dir == Direction.DOWN) directions(i) = Direction.RIGHT
			else if (dir == Direction.LEFT) directions(i) = Direction.DOWN
			else if (dir == Direction.UP) directions(i) = Direction.LEFT
		}
	}

	def mirror() =
	{
		for (i <- 0 until directions.size)
		{
			val dir = directions(i)

			if (dir == Direction.RIGHT) directions(i) = Direction.LEFT
			else if (dir == Direction.LEFT) directions(i) = Direction.RIGHT
		}
	}

	def tryRotateClockwise(board: Board):Option[Boolean] =
	{
		if (col == 1) return None
		rotateCounterClockwise
		if (!willFit(board, Doublet(0, 0))) rotateClockwise
		return None
	}

	def tryRotateCounterClockwise(board: Board):Option[Boolean] =
	{
		if (col == 1) return None
		rotateClockwise
		if (!willFit(board, Doublet(0, 0))) rotateCounterClockwise
		return None
	}

	def lock(board: Board) =
	{
		var triedPos = ArrayBuffer[Doublet[Int, Int]]()
		var nPos = Doublet(0, 0)

		board.fillPos(pos, col)
		triedPos += pos

		for (dir <- directions)
		{
			dir match
			{
				case 0 => nPos._1 += 1
				case 1 => nPos._2 += 1
				case 2 => nPos._1 -= 1
				case 3 => nPos._2 -= 1
			}

			val testPos = Doublet(pos._1 + nPos._1, pos._2 + nPos._2)
			if (!triedPos.contains(testPos))
			{
				board.fillPos(testPos, col)
				triedPos += testPos
			}
		}
	}

	def willFit(board: Board, newPos: Doublet[Int, Int]): Boolean =
	{
		var nPos = newPos

		if (board.isFree(Doublet(pos._1 + nPos._1, pos._2 + nPos._2)) != 0)
			return false

		for (dir <- directions)
		{
			dir match
			{
				case 0 => nPos._1 += 1
				case 1 => nPos._2 += 1
				case 2 => nPos._1 -= 1
				case 3 => nPos._2 -= 1
			}

			if (board.isFree(Doublet(pos._1 + nPos._1, pos._2 + nPos._2)) != 0)
				return false
		}

		return true
	}

	def draw(g: Canvas) =
	{
		def buildRect(pos: (Int, Int)): Rect =
			new Rect(8 + pos._1 * (64 + 8),
				     8 + pos._2 * (64 + 8),
				     8 + pos._1 * (64 + 8) + 64,
				     8 + pos._2 * (64 + 8) + 64)

		var dPos = Doublet(0, 0)
		g drawRect(buildRect((pos._1, pos._2)), Tetrominoes.cols(col))

		for (dir <- directions)
		{
			dir match
			{
				case 0 => dPos._1 += 1
				case 1 => dPos._2 += 1
				case 2 => dPos._1 -= 1
				case 3 => dPos._2 -= 1
			}

			g drawRect(buildRect((pos._1 + dPos._1, pos._2 + dPos._2)), Tetrominoes.cols(col))
		}
	}
}