package com.antirot.app.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antirot.app.data.local.dao.DailyTotal
import com.antirot.app.data.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InsightsViewModel @Inject constructor(
    usageRepository: UsageRepository,
) : ViewModel() {

    val weeklyTotals: StateFlow<List<DailyTotal>> = usageRepository.observeWeeklyTotals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
