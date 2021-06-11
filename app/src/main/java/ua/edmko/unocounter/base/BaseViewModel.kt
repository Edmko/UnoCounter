package ua.edmko.unocounter.base

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.edmko.unocounter.navigation.NavigationCommand
import ua.edmko.unocounter.navigation.NavigationManager
import ua.edmko.unocounter.utils.LIFECYCLE
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<S : ViewState, E : Event>(private val navigationManager: NavigationManager) :
    ViewModel(), CoroutineScope {

    init {
        Log.d(LIFECYCLE, "Create ${this.javaClass.simpleName}")
    }

    private val scopeJob: Job = SupervisorJob()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    private fun handleError(throwable: Throwable) {
        Log.e("Exception", throwable.message ?: "")

    }

    override val coroutineContext: CoroutineContext = scopeJob + Dispatchers.Main + errorHandler

    private val _viewStates: MutableStateFlow<S?> = MutableStateFlow(null)
    fun viewStates(): StateFlow<S?> = _viewStates

    protected var viewState: S
        get() = _viewStates.value
            ?: throw UninitializedPropertyAccessException("\"viewState\" was queried before being initialized")
        set(value) {
            /** StateFlow doesn't work with same values */
            if (_viewStates.value == value) {
                _viewStates.value = null
            }
            _viewStates.value = value
        }

    abstract fun obtainEvent(viewEvent: E)

    protected suspend fun navigateTo(destination: NavigationCommand) {
        navigationManager.navigate(destination)
    }

    override fun onCleared() {
        Log.d(LIFECYCLE, "Cleared ${this.javaClass.simpleName}")
        super.onCleared()
    }
}