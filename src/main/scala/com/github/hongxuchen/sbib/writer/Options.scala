package com.github.hongxuchen.sbib.writer

import enumeratum._

sealed trait StyleEnum extends EnumEntry

object StyleEnum extends Enum[StyleEnum] {
  val values = findValues

  case object Bracket extends StyleEnum

  case object Quote extends StyleEnum

}

sealed trait Template extends EnumEntry

object Template extends Enum[Template] {

  val values = findValues

  case object SIGPAN extends Template
}