package com.gaurav.smartcook.ui.runrecipie.steps

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gaurav.smartcook.ui.runrecipie.RecipieSummaryViewModel
import com.gaurav.smartcook.ui.theme.AppTheme

@Composable
fun StepsScreen(
    viewModel: RecipieSummaryViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val recipe = viewModel.recipie
    
    StepsScreenContent(
        recipeName = recipe?.name ?: "Recipe Steps",
        steps = recipe?.steps ?: emptyList(),
        onBack = onBack,
        onFinish = onFinish
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsScreenContent(
    recipeName: String,
    steps: List<String>,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val isFinished = currentStepIndex >= steps.size && steps.isNotEmpty()


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = recipeName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: TTS Logic */ }) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))


                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(24.dp)
                ) {
                    AnimatedContent(
                        targetState = if (isFinished) -1 else currentStepIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                        slideOutHorizontally { -it } + fadeOut()
                            } else {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                        slideOutHorizontally { it } + fadeOut()
                            }.using(SizeTransform(clip = false))
                        }, label = "StepAnimation"
                    ) { targetState ->
                        if (targetState != -1 && steps.isNotEmpty()) {
                            val scrollState = rememberScrollState()
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text(
                                    text = "STEP ${targetState + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                Box(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
                                    Text(
                                        text = steps[targetState],
                                        style = MaterialTheme.typography.bodyMedium, // Reduced from headlineSmall
                                        lineHeight = 26.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else if (steps.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No instructions found.", style = MaterialTheme.typography.bodyLarge)
                            }
                        } else {

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(100.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "Enjoy your food!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(48.dp))
                                Button(
                                    onClick = onFinish,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        "Finish Cooking", 
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isFinished && steps.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalIconButton(
                                onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                                enabled = currentStepIndex > 0,
                                modifier = Modifier.size(64.dp),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "Previous",
                                    modifier = Modifier.size(32.dp)
                                )
                            }


                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(steps.size) { index ->
                                    val isSelected = index == currentStepIndex
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(if (isSelected) 10.dp else 8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary 
                                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            )
                                            .clickable { currentStepIndex = index }
                                    )
                                }
                            }

                            FilledIconButton(
                                onClick = { currentStepIndex++ },
                                modifier = Modifier.size(64.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StepsScreenPreview() {
    AppTheme {
        StepsScreenContent(
            recipeName = "Classic Tomato Pasta",
            steps = listOf(
                "Boil water in a large pot with salt. Ensure you have enough water for the pasta to move freely.",
                "Add pasta and cook until al dente. Check the package for specific timing instructions.",
                "Drain and toss with tomato sauce. You can add a little pasta water to help the sauce bind.",
                "Garnish with basil and serve hot. A sprinkle of parmesan cheese is also highly recommended."
            ),
            onBack = {},
            onFinish = {}
        )
    }
}
