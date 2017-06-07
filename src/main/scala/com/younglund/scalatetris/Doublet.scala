package com.younglund.scalatetris

case class Doublet[A, B] (var _1: A, var _2: B)
{
	implicit def toTuple() = (_1, _2)
}