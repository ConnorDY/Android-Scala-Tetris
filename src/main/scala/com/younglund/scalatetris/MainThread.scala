package com.younglund.scalatetris

import scala.collection.mutable.ArrayBuffer

import android.content.Context
import android.view.{SurfaceHolder}
import android.graphics.{Canvas, Rect}

class MainThread(holder: SurfaceHolder, context: Context) extends Thread
{
	//  Init vars
	val quantum: Long = (1.asInstanceOf[Long]/60)*1000 // 60fps

	var canvasWidth: Int = _
	var canvasHeight: Int = _

	var score: Int = 0
	var multiplier: Float = 1.0f

	val rand = new scala.util.Random
	val Colour = new Colours
	val board = new Board

	var tetrominoes =  ArrayBuffer[Tetromino]()
 
 	// Main game code
	override def run =
	{
		var isRunning: Boolean = true
		var timeToFall: Int = 60
		var fallTimer: Int = timeToFall
		var gameTimer: Int = 0

		while (isRunning)
		{
			val t0 = System.currentTimeMillis

			// Game logic
			if (fallTimer <= 0)
			{
				var i: Int = 0

				while (i < tetrominoes.size)
				{
					// Make tetrominoes fall
					if (tetrominoes(i).alive)
					{
						tetrominoes(i).fall(board)
						i += 1
					}
					// Remove 'dead' tetrominoes
					else
					{
						tetrominoes(i).lock(board)
						tetrominoes.remove(i)
					}
				}

				// Adjust timers and mutliplier (if necessary)
				fallTimer = timeToFall
				gameTimer += 1

				if (gameTimer % 100 == 0)
				{
					multiplier += .1f
					timeToFall -= 2
				}
			}

			fallTimer -= 1

			// Clear lines (if needed)
			for (y <- board.height - 1 to 0 by -1)
				board.checkLine(y)

			// Calculate score
			if (board.linesCleared > 0)
			{
				board.linesCleared match
				{
					case 1 => score += (100 * multiplier).toInt
					case 2 => score += (250 * multiplier).toInt
					case 3 => score += (500 * multiplier).toInt
					case 4 => score += (1000 * multiplier).toInt
				}
				board.linesCleared = 0
			}

			// Add new tetromino(es)
			if (tetrominoes.size == 0)
				addTetromino

			// Draw game
			withCanvas { g =>
				drawGame(g)
			}
			
			// Sleep till the next frame
			val t1 = System.currentTimeMillis
			if (t1 - t0 < quantum) Thread.sleep(quantum - (t1 - t0))
			else ()
		}
	}

	// Add tetromino
	def addTetromino() =
	{
		val i: Int = rand.nextInt(Tetrominoes.shapes.size)

		tetrominoes += new Tetromino(Tetrominoes.shapes(i))
		tetrominoes(0).setPos(Doublet(board.width / 2, 0))
		tetrominoes(0).setColour(i + 1)

		while (rand.nextInt(3) != 0)
			tetrominoes(0).rotateClockwise

		if (rand.nextInt(1) == 0)
			tetrominoes(0).mirror
	}

	// Move current piece
	def moveRight() =
	{
		if (tetrominoes.size == 1)
		{
			val pos = tetrominoes(0).pos
			if (tetrominoes(0).willFit(board, Doublet(1, 0)))
				tetrominoes(0).setPos(Doublet(pos._1 + 1, pos._2))
		}
	}

	def moveLeft() =
	{
		if (tetrominoes.size == 1)
		{
			val pos = tetrominoes(0).pos
			if (tetrominoes(0).willFit(board, Doublet(-1, 0)))
				tetrominoes(0).setPos(Doublet(pos._1 - 1, pos._2))
		}
	}

	def moveUp() =
	{
		if (tetrominoes.size == 1)
		{
			var pos = tetrominoes(0).pos
			while (tetrominoes(0).willFit(board, Doublet(0, 1)))
			{
				tetrominoes(0).setPos(Doublet(pos._1, pos._2 + 1))
				pos = Doublet(pos._1, pos._2 + 1)
			}
		}
	}

	def moveDown() =
	{
		if (tetrominoes.size == 1)
		{
			val pos = tetrominoes(0).pos
			if (tetrominoes(0).willFit(board, Doublet(0, 1)))
				tetrominoes(0).setPos(Doublet(pos._1, pos._2 + 1))
		}
	}

	def clicked(coords: (Float, Float)):Option[Boolean] =
	{
		if (tetrominoes.size != 1) return None
		if (coords._2 < canvasHeight / 3 - 2)
		{
			if (coords._1 > canvasWidth / 2 + 2 && coords._1 < canvasWidth - 4 - canvasWidth / 4)
				tetrominoes(0).tryRotateCounterClockwise(board)
			else if (coords._1 > canvasWidth + 2 - canvasWidth / 4)
				tetrominoes(0).tryRotateClockwise(board)
		}
		else if (coords._2 > canvasHeight / 3 + 2 && coords._2 < canvasHeight - canvasHeight / 3 - 2)
		{
			if (coords._1 > canvasWidth / 2 + 2 && coords._1 < canvasWidth - 4 - canvasWidth / 4)
				moveLeft
			else if (coords._1 > canvasWidth + 2 - canvasWidth / 4)
				moveRight
		}
		else if (coords._2 > canvasHeight - canvasHeight / 3 + 2)
		{
			if (coords._1 > canvasWidth / 2 + 2 && coords._1 < canvasWidth - 4 - canvasWidth / 4)
				moveUp
			else if (coords._1 > canvasWidth + 2 - canvasWidth / 4)
				moveDown
		}
		return None
	}

	def setCanvasSize(w: Int, h: Int) =
	{
		canvasWidth = w
		canvasHeight = h

		board.setSize(((canvasWidth.toFloat / (64 + 8)) / 3).toInt, (canvasHeight.toFloat / (64 + 8)).toInt)
	}

	def withCanvas(f: Canvas => Unit) =
	{
		val canvas = holder.lockCanvas(null)
		try {
			f(canvas)
		} finally {
			holder.unlockCanvasAndPost(canvas)
		}
	}

	def drawControls(g: Canvas) =
	{
		// Rotate CC
		g drawRect(new Rect(
			canvasWidth / 2 + 2,
			2,
			canvasWidth - 4 - canvasWidth / 4,
			canvasHeight / 3 - 2
		), Colour.greenA200)

		// Rotate C
		g drawRect(new Rect(
			canvasWidth + 2 - canvasWidth / 4,
			2,
			canvasWidth - 2,
			canvasHeight / 3 - 2
		), Colour.greenA400)

		// Move left
		g drawRect(new Rect(
			canvasWidth / 2 + 2,
			canvasHeight / 3 + 2,
			canvasWidth - 4 - canvasWidth / 4,
			canvasHeight - canvasHeight / 3 - 2
		), Colour.greenA400)

		// Move right
		g drawRect(new Rect(
			canvasWidth + 2 - canvasWidth / 4,
			canvasHeight / 3 + 2,
			canvasWidth - 2,
			canvasHeight - canvasHeight / 3 - 2
		), Colour.greenA200)

		// Lock down
		g drawRect(new Rect(
			canvasWidth / 2 + 2,
			canvasHeight - canvasHeight / 3 + 2,
			canvasWidth - 4 - canvasWidth / 4,
			canvasHeight - 2
		), Colour.greenA200)

		// Speed up
		g drawRect(new Rect(
			canvasWidth + 2 - canvasWidth / 4,
			canvasHeight - canvasHeight / 3 + 2,
			canvasWidth - 2,
			canvasHeight - 2
		), Colour.greenA400)
	}

	// Draw step
	def drawGame(g: Canvas) =
	{
		def buildRect(pos: Doublet[Int, Int]): Rect =
			new Rect(8 + pos._1 * (64 + 8),
					 8 + pos._2 * (64 + 8),
					 8 + pos._1 * (64 + 8) + 64,
					 8 + pos._2 * (64 + 8) + 64)

		// Clear screen
		g drawRect(new Rect(-1, -1, canvasWidth + 1, canvasHeight + 1), Colour.black)

		// Draw grid
		for (
			x <- 0 until board.width;
			y <- 0 until board.height;
			val pos = Doublet(x, y)
		)
		{
			g drawRect(buildRect(pos), Tetrominoes.cols(board.isFree(pos)))
		}

		// Draw tetrominoes
		for (tetromino <- tetrominoes)
			tetromino.draw(g)

		// Draw controls
		drawControls(g)

		// Draw score
		g drawText("Score: " + score, canvasWidth / 3 + 4, 104, Colour.deepPurple50)
	}
}