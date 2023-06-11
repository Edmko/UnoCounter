package ua.edmko.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ua.edmko.core.extension.getColorByIndex
import ua.edmko.core.ui.components.EditDialog
import ua.edmko.core.ui.components.GameButton
import ua.edmko.core.ui.components.PlayerItem
import ua.edmko.core.ui.components.Toolbar
import ua.edmko.core.ui.theme.AppTheme
import ua.edmko.core.ui.theme.baseHorizontalPadding
import ua.edmko.core.ui.theme.getAppRadioButtonColors
import ua.edmko.domain.entities.Game
import ua.edmko.domain.entities.Player
import ua.edmko.domain.entities.PlayerId
import ua.edmko.domain.entities.Round

@Composable
fun GameScreen() {
    val viewModel: GameViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()
    state?.let { GameScreen(it, viewModel::obtainEvent) }
}

@Composable
internal fun GameScreen(state: GameViewState, event: (GameEvent) -> Unit) {
    Scaffold(
        topBar = { Toolbar(title = stringResource(R.string.game)) { event(NavigateBack) } },
        modifier = Modifier
            .fillMaxSize(),
        backgroundColor = AppTheme.colors.surface,
    ) { paddings ->
        if (state.isDialogShows && state.selectedPlayer?.name != null) {
            EditDialog(
                title = stringResource(
                    id = R.string.score_for_player,
                    state.selectedPlayer.name,
                ),
                textType = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                onClick = { score -> event(ConfirmEdition(score.toIntOrNull() ?: 0)) },
                onDismiss = { event(DismissDialog) },
            )
        }

        Box(
            Modifier
                .fillMaxSize()
                .padding(paddings),
        ) {
            Column {
                PlayersList(
                    state.game.calculatePlayersTotal(),
                    currentRound = state.currentRound,
                    onClick = { event(EditScore(it)) },
                    winner = state.currentRound.winner,
                    selectWinner = { event(SetWinner(it)) },
                )
            }

            GameButton(
                text = stringResource(R.string.next_round),
                modifier = Modifier
                    .padding(baseHorizontalPadding, 0.dp, baseHorizontalPadding, 40.dp)
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.BottomCenter),
                onClick = { event(NextRound) },
            )
        }
    }
}

@Composable
fun PlayersList(
    playersTotal: Map<Player, Int>,
    currentRound: Round?,
    onClick: (Player) -> Unit,
    winner: PlayerId? = null,
    selectWinner: (Player) -> Unit,
) {
    LazyColumn() {
        item {
            PlayerItem(
                height = 30.dp,
                name = "",
                color = Color.Transparent,
            ) {
                Text(
                    text = stringResource(R.string.total),
                    color = AppTheme.colors.primary,
                    modifier = Modifier
                        .weight(1f),
                    textAlign = TextAlign.Center,
                    style = AppTheme.typography.body1,
                )
                Text(
                    text = stringResource(R.string.round),
                    modifier = Modifier
                        .weight(1f),
                    color = AppTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = AppTheme.typography.body1,
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.winner),
                    color = AppTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    style = AppTheme.typography.body1,
                )
            }
        }
        itemsIndexed(playersTotal.toList()) { index, (player, total) ->
            PlayerItem(
                modifier = Modifier.clickable(enabled = winner != player.playerId) {
                    onClick.invoke(player)
                },
                name = player.name,
                color = index.getColorByIndex(),
            ) {
                Box(
                    Modifier
                        .height(30.dp)
                        .weight(1f)
                        .padding(vertical = 0.dp, horizontal = 10.dp),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = total.toString(),
                        color = AppTheme.colors.onSurface,
                        textAlign = TextAlign.Center,
                        style = AppTheme.typography.body1,
                    )
                }

                Box(
                    Modifier
                        .height(30.dp)
                        .weight(1f)
                        .padding(vertical = 0.dp, horizontal = 10.dp)
                        .background(
                            color = if (player.playerId == winner) {
                                AppTheme.colors.surface
                            } else {
                                AppTheme.colors.background
                            },
                            shape = AppTheme.shapes.medium,
                        ),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = currentRound?.result?.getOrDefault(player.playerId, 0).toString(),
                        color = AppTheme.colors.onSurface,
                        textAlign = TextAlign.Center,
                        style = AppTheme.typography.body1,
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    RadioButton(
                        modifier = Modifier.align(Alignment.Center),
                        selected = player.playerId == winner,
                        onClick = { selectWinner(player) },
                        colors = getAppRadioButtonColors(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun GameScreenPreview() {
    AppTheme {
        GameScreen(state = GameViewState.Stub) {}
    }
}

@Preview
@Composable
fun PlayersListPreview() {
    AppTheme {
        Surface(
            color = AppTheme.colors.surface,
        ) {
            PlayersList(Game.getGameStub().calculatePlayersTotal(), Round.empty, {}) {}
        }
    }
}
