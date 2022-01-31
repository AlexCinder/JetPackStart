package com.example.jetpackstart.domain.entity

data class GameSettings(
    val maxValue: Int,
    val minCountOfRightAnswers: Int,
    val minPercentOfRightAnswers: Int,
    val gameTimeInSeconds: Int
) {
}