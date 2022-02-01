package com.example.jetpackstart.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.jetpackstart.R
import com.example.jetpackstart.databinding.FragmentGameFinishedBinding
import com.example.jetpackstart.domain.entity.GameResult
import com.example.jetpackstart.domain.entity.GameSettings
import java.io.Serializable

class FinishGameFragment : Fragment() {

    private lateinit var settings: GameSettings
    private lateinit var result: GameResult
    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    retryGame()
                }
            }
        )
        binding.buttonRetry.setOnClickListener { retryGame() }
        initViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs() {
        requireArguments().getParcelable<GameResult>(RESULT)?.let {
            result = it
            settings = it.gameSettings
        }
    }

    private fun retryGame() {
        requireActivity().supportFragmentManager.popBackStack(
            GameFragment.NAME,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun initViews() {
        with(binding) {
            if (result.winner) {
                emojiResult.setImageResource(R.drawable.ic_smile)
            } else
                emojiResult.setImageResource(R.drawable.ic_sad)
            tvRequiredAnswers.text = String.format(
                requireActivity().resources.getString(R.string.required_score),
                settings.minCountOfRightAnswers.toString()
            )
            tvRequiredPercentage.text = String.format(
                requireActivity().resources.getString(R.string.required_percentage),
                settings.minPercentOfRightAnswers.toString()
            )
            tvScoreAnswers.text = String.format(
                requireActivity().resources.getString(R.string.score_answers),
                result.countOfRightAnswers.toString()
            )
            val percent = getPercentOfRightAnswers()

            tvScorePercentage.text = String.format(
                requireActivity().resources.getString(R.string.score_percentage),
                percent.toString()
            )

        }
    }

    private fun getPercentOfRightAnswers(): Int {
        return with(result) {
            ((countOfRightAnswers / countOfQuestion.toDouble()) * 100).toInt()
        }
    }

    companion object {

        private const val RESULT = "result"

        fun newInstance(result: GameResult): FinishGameFragment {
            return FinishGameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(RESULT, result)
                }
            }
        }
    }
}