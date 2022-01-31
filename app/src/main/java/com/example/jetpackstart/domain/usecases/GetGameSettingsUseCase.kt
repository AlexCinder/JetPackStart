package com.example.jetpackstart.domain.usecases

import com.example.jetpackstart.domain.GameRepository
import com.example.jetpackstart.domain.entity.GameSettings
import com.example.jetpackstart.domain.entity.Level

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(level: Level): GameSettings {
        return repository.getGameSettings(level)
    }
}