package ua.edmko.unocounter.ui.players

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ua.edmko.unocounter.base.BaseViewModel
import ua.edmko.unocounter.domain.entities.Player
import ua.edmko.unocounter.domain.interactor.AddPlayer
import ua.edmko.unocounter.domain.interactor.DeletePlayer
import ua.edmko.unocounter.domain.interactor.ObservePlayers
import ua.edmko.unocounter.domain.interactor.UpdatePlayer
import ua.edmko.unocounter.navigation.NavigationDirections
import ua.edmko.unocounter.navigation.NavigationManager
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val observePlayers: ObservePlayers,
    private val addPlayer: AddPlayer,
    private val updatePlayer: UpdatePlayer,
    private val deletePlayer: DeletePlayer,
    private val navigationManager: NavigationManager
) : BaseViewModel<PlayersViewState, PlayersEvent>(navigationManager) {
    init {
        viewState = PlayersViewState()
        viewModelScope.launch {
            observePlayers.createObservable(Unit).collect { players ->
                viewState = viewState.copy(players = players)
            }
        }
    }

    override fun obtainEvent(viewEvent: PlayersEvent) {
        when (viewEvent) {
            is AddPlayerButton -> viewState = viewState.copy(isDialogShows = true)
            is CreatePlayer -> createPlayer(viewEvent.name)
            is UpdatePlayersSelection -> updatePlayer(viewEvent.player)
            is DeletePlayerEvent -> deletePlayer(viewEvent.player)
            is NavigateBack -> viewModelScope.launch { navigationManager.navigate(NavigationDirections.back) }
            is DismissDialog -> viewState = viewState.copy(isDialogShows = false)
        }
    }

    private fun createPlayer(name: String) {
        viewModelScope.launch {
            val player = Player(name = name)
            addPlayer.executeSync(AddPlayer.Params(player))
            viewState = viewState.copy(isDialogShows = false)
        }

    }

    private fun deletePlayer(player: Player) {
        viewModelScope.launch {
            deletePlayer.executeSync(DeletePlayer.Params(player.playerId))
        }
    }

    private fun updatePlayer(player: Player) {
        viewModelScope.launch {
            updatePlayer.executeSync(UpdatePlayer.Params(player))
        }
    }
}