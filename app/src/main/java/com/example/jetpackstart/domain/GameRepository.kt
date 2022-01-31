package com.example.jetpackstart.domain

import com.example.jetpackstart.domain.entity.GameSettings
import com.example.jetpackstart.domain.entity.Level
import com.example.jetpackstart.domain.entity.Question

interface GameRepository {
    fun generateQuestion(
        maxSumValue: Int,
        numberOfOptions: Int
    ): Question

    fun getGameSettings(level: Level): GameSettings
}