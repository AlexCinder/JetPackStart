package com.example.jetpackstart.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jetpackstart.R
import com.example.jetpackstart.databinding.FragmentGameBinding
import com.example.jetpackstart.domain.entity.GameResult
import com.example.jetpackstart.domain.entity.GameSettings
import com.example.jetpackstart.domain.entity.Level

class GameFragment : Fragment() {
    private lateinit var level: Level
    private lateinit var result: GameResult
    private lateinit var gameSettings: GameSettings

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
        gameSettings = GameSettings(
            10,
            1,
            20,
            60
        )
        result = GameResult(true, 10, 30, gameSettings)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvQuestion.setOnClickListener {
            launchGameResultFragment(result)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun launchGameResultFragment(result: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, FinishGameFragment.newInstance(result))
            .addToBackStack(null)
            .commit()
    }

    private fun parseArgs() {
        level = requireArguments().getSerializable(KEY) as Level

    }

    companion object {
        const val NAME = "gameFragment"
        private const val KEY = "key"
        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY, level)
                }
            }
        }
    }
}