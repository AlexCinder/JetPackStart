package com.example.jetpackstart.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackstart.R
import com.example.jetpackstart.databinding.FragmentGameBinding
import com.example.jetpackstart.domain.entity.GameResult
import com.example.jetpackstart.domain.entity.Level
import com.example.jetpackstart.domain.entity.Question

class GameFragment : Fragment() {


    private var listOfQuestions = mutableListOf<TextView>()
    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(this)[GameViewModel::class.java]
    }
    private val minCountOfRightAnswers: Int by lazy { viewModel.startGame(level) }
    private lateinit var level: Level
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
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
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectTextViews()
        setOnClickListeners()
        initObservables()
        viewModel.startGame(level)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectTextViews() {
        with(binding) {
            listOfQuestions.add(tvOption1)
            listOfQuestions.add(tvOption2)
            listOfQuestions.add(tvOption3)
            listOfQuestions.add(tvOption4)
            listOfQuestions.add(tvOption5)
            listOfQuestions.add(tvOption6)
        }
    }

    private fun setOnClickListeners() {
        for (textView in listOfQuestions) {
            textView.setOnClickListener {
                viewModel.chooseAnswer(textView.text.toString().toInt())
            }
        }
    }

    private fun createQuestion(question: Question) {
        for (i in listOfQuestions.indices) {
            listOfQuestions[i].text = question.options[i].toString()
        }
        with(binding) {
            tvLeftNumber.text = question.visibleNumber.toString()
            tvSum.text = question.sum.toString()
        }
    }

    private fun initObservables() {
        with(viewModel) {
            question.observe(viewLifecycleOwner) {
                createQuestion(it)
            }
            timeUntilGameEnd.observe(viewLifecycleOwner) {
                binding.tvTimer.text = it
            }
            percentOfRightAnswers.observe(viewLifecycleOwner) {
                binding.progressBar.setProgress(it, true)
            }
            progressAnswers.observe(viewLifecycleOwner) {
                binding.tvAnswersProgress.text = String.format(
                    requireActivity().resources.getString(R.string.progress_answers),
                    it,
                    minCountOfRightAnswers
                )
            }
            enoughCount.observe(viewLifecycleOwner) {
                binding.tvAnswersProgress.setTextColor(getColorFromResources(it))
            }
            enoughPercent.observe(viewLifecycleOwner) {
                val color = getColorFromResources(it)
                binding.progressBar.progressTintList = ColorStateList.valueOf(color)
            }
            minPercent.observe(viewLifecycleOwner) {
                val color = ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_blue_light
                )
                with(binding){
                    progressBar.secondaryProgress = it
                    progressBar.secondaryProgressTintList = ColorStateList.valueOf(color)
                }
            }
            result.observe(viewLifecycleOwner){
                launchGameResultFragment(it)
            }
        }
    }

    private fun getColorFromResources(state: Boolean): Int {
        val colorId = if (state) {
            android.R.color.holo_green_dark
        } else android.R.color.holo_red_light
        return ContextCompat.getColor(requireContext(), colorId)
    }

    private fun launchGameResultFragment(result: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, FinishGameFragment.newInstance(result))
            .addToBackStack(null)
            .commit()
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY)?.let {
            level = it
        }
    }

    companion object {
        const val NAME = "gameFragment"
        private const val KEY = "key"
        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY, level)
                }
            }
        }
    }
}