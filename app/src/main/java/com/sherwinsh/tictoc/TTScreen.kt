package com.sherwinsh.tictoc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.random.Random

@Composable
fun TTScreen() {
    val win = remember {
        mutableStateOf<Win?>(null)
    }
    val playerTurn = remember {
        mutableStateOf(true) //True -> player's turn
    }
    val moves = remember {
        //True-> player's move, false-> AI's move, null-> No move
        mutableStateListOf<Boolean?>(null, null, null, null, null, null, null, null, null)
    }
    val onTap: (Offset) -> Unit = {
        if (playerTurn.value && win.value == null) {
            val x = (it.x / 333).toInt()
            val y = (it.y / 333).toInt()
            val posInMoves = y * 3 + x
            if (moves[posInMoves] == null) {
                moves[posInMoves] = true
                playerTurn.value = false
                win.value = checkEndGame(moves)
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 30.sp,
            modifier = Modifier.padding(16.dp)
        )
        Header(playerTurn = playerTurn.value)
        Board(moves = moves, onTap)
        if (!playerTurn.value && win.value == null) {
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                delay(1500L)
                while (true) {
                    val i = Random.nextInt(9)
                    if (moves[i] == null) {
                        moves[i] = false
                        playerTurn.value = true
                        win.value = checkEndGame(moves)
                        break
                    }
                }
            }
        }
        if (win.value != null) {
            when (win.value) {
                Win.PLAYER -> {
                    Text(text = "Player has won \uD83C\uDF89", fontSize = 25.sp)
                }

                Win.AI -> {
                    Text(text = "AI has won \uD83D\uDE24", fontSize = 25.sp)
                }

                Win.DRAW -> {
                    Text(text = "It's a draw \uD83D\uDE33", fontSize = 25.sp)
                }

                null -> {
                    Text(text = "It's a draw \uD83D\uDE33", fontSize = 25.sp)
                }
            }
            Button(onClick = {
                playerTurn.value = true
                win.value = null
                for (i in 0..8) {
                    moves[i] = null
                }
            }) {
                Text(text = "Restart")
            }
        }
    }

}

fun checkEndGame(m: List<Boolean?>): Win? {
    var win: Win? = null
    if ((m[0] == true && m[1] == true && m[2] == true) ||
        (m[3] == true && m[4] == true && m[5] == true) ||
        (m[6] == true && m[7] == true && m[8] == true) ||
        (m[0] == true && m[4] == true && m[8] == true) ||
        (m[2] == true && m[4] == true && m[6] == true) ||
        (m[0] == true && m[3] == true && m[6] == true) ||
        (m[1] == true && m[4] == true && m[7] == true) ||
        (m[2] == true && m[5] == true && m[8] == true)
    ) {
        win = Win.PLAYER
    }
    if ((m[0] == false && m[1] == false && m[2] == false) ||
        (m[3] == false && m[4] == false && m[5] == false) ||
        (m[6] == false && m[7] == false && m[8] == false) ||
        (m[0] == false && m[4] == false && m[8] == false) ||
        (m[2] == false && m[4] == false && m[6] == false) ||
        (m[0] == false && m[3] == false && m[6] == false) ||
        (m[1] == false && m[4] == false && m[7] == false) ||
        (m[2] == false && m[5] == false && m[8] == false)
    ) {
        win = Win.AI
    }
    if (win == null) {
        var available = false
        for (i in 0..8) {
            if (m[i] == null) {
                available = true
            }
        }
        if (!available) {
            win = Win.DRAW
        }
    }
    return win
}