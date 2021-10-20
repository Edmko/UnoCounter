package ua.edmko.unocounter.ui.endgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.edmko.navigation.NavigationDirections
import ua.edmko.navigation.NavigationManager
import javax.inject.Inject

@HiltViewModel
class EndGameViewModel @Inject constructor(navigationManager: NavigationManager): ViewModel() {
    init {
        viewModelScope.launch {
            delay(2000L)
            navigationManager.navigate(NavigationDirections.back )
        }
    }
}