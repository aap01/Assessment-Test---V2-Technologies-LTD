package com.aap.assessment_test___v2_technologies_ltd.domain.usecase

import com.aap.assessment_test___v2_technologies_ltd.data.model.entity.PreviousSurvey

interface GetPreviousSurveyUC {
    suspend fun get(): List<PreviousSurvey>
}