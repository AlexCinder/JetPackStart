package com.example.jetpackstart.presentation

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jetpackstart.data.GameRepositoryImpl
import com.example.jetpackstart.domain.entity.GameResult
import com.example.jetpackstart.domain.entity.GameSettings
import com.example.jetpackstart.domain.entity.Level
import com.example.jetpackstart.domain.entity.Question
import com.example.jetpackstart.domain.usecases.GenerateQuestionUseCase
import com.example.jetpackstart.domain.usecases.GetGameSettingsUseCase
import java.util.*

class GameViewModel() : ViewModel() {

    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase
            by lazy { GetGameSettingsUseCase(repository) }

    private lateinit var settings: GameSettings
    private lateinit var level: Level

    private var timer: CountDownTimer? = null

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _timeUntilGameEnd = MutableLiveData<String>()
    val timeUntilGameEnd: LiveData<String>
        get() = _timeUntilGameEnd

    private var countOfRightAnswers = 0
    private var countOfAnswers = 0

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCount = MutableLiveData<Boolean>()
    val enoughCount: LiveData<Boolean>
        get() = _enoughCount

    private val _enoughPercent = MutableLiveData<Boolean>()
    val enoughPercent: LiveData<Boolean>
        get() = _enoughPercent

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _result = MutableLiveData<GameResult>()
    val result: LiveData<GameResult>
        get() = _result

    fun startGame(level: Level): Int {
        getGameSettings(level)
        generateQuestion()
        startTimer()
        updateProgress()
        return settings.minCountOfRightAnswers
    }

    fun chooseAnswer(answer: Int) {
        checkAnswer(answer)
        updateProgress()
        generateQuestion()
    }

    private fun checkAnswer(answer: Int) {
        val rightAnswer = question.value
        if (answer == rightAnswer?.rightAnswer) {
            countOfRightAnswers++
        }
        countOfAnswers++
    }

    private fun getGameSettings(level: Level) {
        this.level = level
        this.settings = getGameSettingsUseCase(level)
        _minPercent.value = settings.minPercentOfRightAnswers
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(settings.maxValue)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            settings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("TAG", "onTick: $millisUntilFinished")
                _timeUntilGameEnd.value =
                    getConvertedTimeUntilFinishTheGame(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun updateProgress() {
        val percent = getPercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = countOfRightAnswers.toString()
        _enoughCount.value = countOfRightAnswers >= settings.minCountOfRightAnswers
        _enoughPercent.value = percent >= settings.minPercentOfRightAnswers
    }

    private fun getPercentOfRightAnswers(): Int {
        if (countOfAnswers == 0) {
            return 0
        }
        return (((countOfRightAnswers / countOfAnswers.toDouble())) * 100).toInt()
    }

    private fun getConvertedTimeUntilFinishTheGame(millisUntilFinished: Long): String {
        val minutes = millisUntilFinished / MILLIS_IN_SECONDS / SECONDS_IN_MINUTE
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS % SECONDS_IN_MINUTE
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTE = 60
    }

    private fun finishGame() {
        _result.value = GameResult(
            winner = _enoughCount.value == true && _enoughPercent.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestion = countOfAnswers,
            gameSettings = settings
        )
    }


    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        timer = null
    }

}