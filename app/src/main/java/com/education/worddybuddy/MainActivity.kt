package com.education.worddybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.consume
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.education.worddybuddy.ui.theme.WorddyBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorddyBuddyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WordGridScreen()
                }
            }
        }
    }
}

private data class GridPosition(val row: Int, val column: Int)

@Composable
private fun WordGridScreen(modifier: Modifier = Modifier) {
    val grid = remember { defaultLetterGrid() }
    val selectedCells = remember { mutableStateListOf<GridPosition>() }
    val cellBounds = remember { mutableStateMapOf<GridPosition, Rect>() }
    var selectedWord by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = if (selectedWord.isEmpty()) "Drag to select letters" else "Selected word: $selectedWord",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        val cellSpacing = 8.dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    fun updateSelection(position: Offset) {
                        val positionCell = cellBounds.entries.firstOrNull { (_, bounds) ->
                            bounds.contains(position)
                        }?.key

                        if (positionCell != null && positionCell !in selectedCells) {
                            selectedCells.add(positionCell)
                            selectedWord = selectedCells.joinToString(separator = "") { pos ->
                                grid[pos.row][pos.column].toString()
                            }
                        }
                    }

                    detectDragGestures(
                        onDragStart = { offset ->
                            selectedCells.clear()
                            selectedWord = ""
                            updateSelection(offset)
                        },
                        onDragEnd = {
                            if (selectedCells.isEmpty()) {
                                selectedWord = ""
                            }
                        },
                        onDragCancel = {
                            selectedCells.clear()
                            selectedWord = ""
                        }
                    ) { change, _ ->
                        updateSelection(change.position)
                        change.consume()
                    }
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(cellSpacing)
            ) {
                grid.forEachIndexed { rowIndex, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(cellSpacing)
                    ) {
                        row.forEachIndexed { columnIndex, letter ->
                            val position = GridPosition(rowIndex, columnIndex)
                            val isSelected = position in selectedCells
                            val cellShape = RoundedCornerShape(12.dp)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .background(
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        shape = cellShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.outlineVariant
                                        },
                                        shape = cellShape
                                    )
                                    .onGloballyPositioned { coordinates ->
                                        val topLeft = coordinates.positionInParent()
                                        val size = coordinates.size
                                        val bottomRight = Offset(
                                            topLeft.x + size.width,
                                            topLeft.y + size.height
                                        )
                                        cellBounds[position] = Rect(topLeft, bottomRight)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letter.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun defaultLetterGrid(): List<List<Char>> = listOf(
    listOf('W', 'O', 'R', 'D', 'S', 'E'),
    listOf('L', 'E', 'A', 'R', 'N', 'R'),
    listOf('P', 'U', 'Z', 'Z', 'L', 'E'),
    listOf('S', 'E', 'A', 'R', 'C', 'H'),
    listOf('Q', 'U', 'I', 'Z', 'E', 'S'),
    listOf('T', 'R', 'A', 'C', 'E', 'S')
)

@Preview(showBackground = true)
@Composable
private fun WordGridPreview() {
    WorddyBuddyTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WordGridScreen()
        }
    }
}
