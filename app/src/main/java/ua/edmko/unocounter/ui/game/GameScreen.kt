package ua.edmko.unocounter.ui.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import ua.edmko.unocounter.R
import ua.edmko.unocounter.domain.entities.Game
import ua.edmko.unocounter.domain.entities.Player
import ua.edmko.unocounter.domain.entities.PlayerId
import ua.edmko.unocounter.domain.entities.Round
import ua.edmko.unocounter.domain.entities.Round.Companion.getRoundStub
import ua.edmko.unocounter.ui.components.EditDialog
import ua.edmko.unocounter.ui.components.GameButton
import ua.edmko.unocounter.ui.components.PlayerItem
import ua.edmko.unocounter.ui.components.Toolbar
import ua.edmko.unocounter.ui.theme.UNOcounterTheme
import ua.edmko.unocounter.ui.theme.baseDp
import ua.edmko.unocounter.utils.getColorByIndex

@Composable
fun GameScreen(viewModel: GameViewModel, gameId: String) {

    viewModel.fetchGame(gameId)
    val state by viewModel.viewStates().collectAsState()

    UNOcounterTheme() {
        Scaffold(
            topBar = { Toolbar(title = "Game") { viewModel.obtainEvent(NavigateBack) } },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) { paddings ->
            Surface(
                Modifier
                    .padding(paddings)
                    .fillMaxSize()
            ) {
                GameScreen(state, viewModel::obtainEvent)
            }

        }
    }
}

@Composable
fun GameScreen(state: GameViewState?, event: (GameEvent) -> Unit) {
    requireNotNull(state)
    if (state.isDialogShows && state.selectedPlayer?.name != null) EditDialog(
        title = stringResource(
            id = R.string.score_for_player,
            state.selectedPlayer.name
        ),
        textType = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        onClick = { score -> event(ConfirmEdition(score.toIntOrNull() ?: 0)) },
        onDismiss = { event(DismissDialog) })

    Box(Modifier.fillMaxSize()) {
        Column {
            ItemExplanation()
            PlayersList(
                state.game.calculatePlayersTotal(),
                currentRound = state.currentRound,
                onClick = { event(EditScore(it)) },
                winner = state.currentRound.winner,
                selectWinner = { event(SetWinner(it)) })
        }


        GameButton(
            text = stringResource(R.string.next_round),
            modifier = Modifier
                .padding(baseDp, 0.dp, baseDp, 40.dp)
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter),
            onClick = { event(NextRound) })
    }
}

@Composable
fun ItemExplanation() {
    Row() {
        Spacer(modifier = Modifier.fillMaxWidth(0.5f))
        Text(
            text = stringResource(R.string.total),
            color = Color.Red,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = stringResource(R.string.round),
            modifier = Modifier.width(60.dp),
            color = Color.Red
        )
        Text(text = stringResource(R.string.winner), color = Color.Red)
    }
}

@Composable
fun PlayersList(
    playersTotal: Map<Player, Int>,
    currentRound: Round?,
    onClick: (Player) -> Unit,
    winner: PlayerId? = null,
    selectWinner: (Player) -> Unit
) {
    var roundWinner by remember { mutableStateOf(winner) }
    LazyColumn() {
        itemsIndexed(playersTotal.toList()) { index, (player, total) ->
            PlayerItem(
                modifier = Modifier.clickable(enabled = winner != player.playerId) {
                    onClick.invoke(player)
                },
                name = player.name,
                color = getColorByIndex(index)
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .width(60.dp),
                    text = total.toString()
                )
                Text(
                    modifier = Modifier.width(60.dp),
                    text = currentRound?.result?.getOrDefault(player.playerId, 0)
                        .toString()
                )
                RadioButton(
                    modifier = Modifier.fillMaxHeight(),
                    selected = player.playerId == roundWinner,
                    onClick = {
                        roundWinner = player.playerId
                        selectWinner(player)
                    })
            }

        }
    }
}

@Preview
@Composable
fun GameScreenPreview() {
    UNOcounterTheme() {
        GameScreen(state = GameViewState()) {}
    }
}

@Preview
@Composable
fun PlayersListPreview() {
    UNOcounterTheme() {
        Surface() {
            PlayersList(Game.getGameStub().calculatePlayersTotal(), getRoundStub(), {}) {}
        }
    }
}