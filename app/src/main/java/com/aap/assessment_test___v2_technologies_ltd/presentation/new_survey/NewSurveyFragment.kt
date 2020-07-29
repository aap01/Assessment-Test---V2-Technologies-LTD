package com.aap.assessment_test___v2_technologies_ltd.presentation.new_survey

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import com.aap.assessment_test___v2_technologies_ltd.R
import com.aap.assessment_test___v2_technologies_ltd.data.model.entity.QuestionType
import com.aap.assessment_test___v2_technologies_ltd.data.model.entity.Survey
import com.aap.assessment_test___v2_technologies_ltd.data.util.ErrorResponse
import com.aap.assessment_test___v2_technologies_ltd.data.util.Loading
import com.aap.assessment_test___v2_technologies_ltd.data.util.SuccessResponse
import com.aap.assessment_test___v2_technologies_ltd.presentation.IFinishSurvey
import com.aap.assessment_test___v2_technologies_ltd.presentation.INewSurveyClick
import com.aap.assessment_test___v2_technologies_ltd.presentation.extentions.gone
import com.aap.assessment_test___v2_technologies_ltd.presentation.extentions.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_new_survey.*
import kotlinx.android.synthetic.main.loading.*
import org.koin.android.ext.android.inject
import org.koin.android.scope.currentScope

class NewSurveyFragment : Fragment() {

    var iFinishSurvey: IFinishSurvey? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_survey, container, false)
    }

    private lateinit var questionFragmentList: List<QuestionFragment>
    private lateinit var survey: Survey
    private val newSurveyVM: NewSurveyVM by inject()
    private lateinit var fragmentAdapter: FragmentAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObservers()
        setupBackPressing()
        setButtonActions()
        initAdapters()
        initUi()
        newSurveyVM.fetchNewSurvey()
    }

    private fun initUi() {
        viewPager.adapter = fragmentAdapter
    }

    private fun initAdapters() {
        fragmentAdapter = FragmentAdapter(childFragmentManager)
    }

    private fun setupBackPressing() {
        requireActivity().onBackPressedDispatcher.addCallback(this, object:
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewPager.currentItem == 0) activity?.onBackPressed()
                else viewPager.currentItem = viewPager.currentItem - 1
            }
        })
    }

    private fun initObservers() {
        newSurveyVM.newSurveyData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Loading -> loading.visible()
                is ErrorResponse ->  {
                    loading.gone()
                    showErrorDialog(it)
                }
                is SuccessResponse ->  {
                    loading.gone()
                    showSuccessResponse(it.body)
                }
            }
        })
    }

    private fun showSuccessResponse(body: Survey) {
        val tempList = mutableListOf<QuestionFragment>()
        survey = body
        survey.questions.forEach {
            when (it.type) {
                QuestionType.DROP_DOWN -> {
                    val dropdown = DropdownFragment()
                    dropdown.question = it
                    tempList.add(dropdown)
                }
                QuestionType.MULTI_CHOICE -> {
                    val mcq = MCQFragment()
                    mcq.question = it
                    tempList.add(mcq)
                }
                QuestionType.NUMBER -> {
                    val number = NumberFragment()
                    number.question = it
                    tempList.add(number)
                }
                QuestionType.CHECKBOX -> {
                    val checkbox = CheckboxFragment()
                    checkbox.question = it
                    tempList.add(checkbox)
                }
                QuestionType.TEXT -> {
                    val text = TextFragment()
                    text.question = it
                    tempList.add(text)
                }
            }
        }
        questionFragmentList = tempList
        fragmentAdapter.setFragmentList(questionFragmentList)
    }

    private fun showErrorDialog(it: ErrorResponse<Survey>) {
        Toast.makeText(context, it.errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun setButtonActions() {
        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        next.setOnClickListener {
            if (questionFragmentList[viewPager.currentItem].isValid()) {
                if (viewPager.currentItem == questionFragmentList.size - 1) {
                    newSurveyVM.storeSurvey(survey)
                    showThanks()
                }
                else if (viewPager.currentItem < questionFragmentList.size - 1) {
                    viewPager.currentItem += 1
                    if (viewPager.currentItem == questionFragmentList.size - 2) {
                        next.text = "Done"
                    }
                }
            }
        }
    }

    private fun showThanks() {
        MaterialAlertDialogBuilder(context)
            .setTitle("Thank You!")
            .setMessage("Congratulations! You have taken the survey successfully. You can access this survey from dashboard.")
            .setPositiveButton("OK") { dialog, which ->
                iFinishSurvey?.onFinished()
                dialog.dismiss()
            }
            .create()
            .show()
    }

}