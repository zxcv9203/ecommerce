package kr.hhplus.be.server.common.util

fun String.isNumeric(): Boolean = this.all { it.isDigit() }
