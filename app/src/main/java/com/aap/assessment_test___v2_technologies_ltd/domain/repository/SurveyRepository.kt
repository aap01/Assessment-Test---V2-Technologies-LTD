package com.aap.assessment_test___v2_technologies_ltd.domain.repository

import com.aap.assessment_test___v2_technologies_ltd.data.model.entity.PreviousSurvey
import com.aap.assessment_test___v2_technologies_ltd.data.model.entity.Survey

interface SurveyRepository {
    suspend fun fetchNewSurvey(): Survey
    suspend fun storeSurvey(survey: Survey)
    suspend fun getPreviousSurveys(): List<PreviousSurvey>
}