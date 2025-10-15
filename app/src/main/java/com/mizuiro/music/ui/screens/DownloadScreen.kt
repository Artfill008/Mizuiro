package com.mizuiro.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mizuiro.music.download.*
import com.mizuiro.music.ui.components.*
import com.mizuiro.music.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Download Management Screen
 * 
 * Beautiful download management with Mizuiro aesthetic
 */
@Composable
fun DownloadScreen(
    modifier: Modifier = Modifier,
    downloadManager: DownloadManager,
    onTrackSelected: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val downloadState by downloadManager.downloadState.collectAsState()
    val downloadQueue by downloadManager.downloadQueue.collectAsState()
    val downloadedTracks by downloadManager.downloadedTracks.collectAsState()
    val downloadProgress by downloadManager.downloadProgress.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showSmartDownload by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MizuiroBase)
            .padding(16.dp)
    ) {
        // Header with ASCII art
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "╔══════════════════════════════════════╗",
                style = H3,
                color = SteelBlue,
                fontFamily = VT323
            )
            Text(
                text = "║           DOWNLOAD MANAGER            ║",
                style = H3,
                color = SteelBlue,
                fontFamily = VT323
            )
            Text(
                text = "╚══════════════════════════════════════╝",
                style = H3,
                color = SteelBlue,
                fontFamily = VT323
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Smart Downloads & Offline Listening (◠‿◠)",
                style = H2,
                color = FadedBlack,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Download your favorite music for offline enjoyment",
                style = Caption,
                color = SteelBlue,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Kawaii Mascot
        KawaiiMascot(
            state = if (downloadState.isDownloading) "downloading" else "idle",
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Download Statistics Card
        DownloadStatsCard(
            downloadState = downloadState,
            downloadedTracks = downloadedTracks,
            onSmartDownload = { showSmartDownload = true }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Navigation
        DownloadTabNavigation(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Content
        when (selectedTab) {
            0 -> DownloadQueueSection(
                downloadQueue = downloadQueue,
                downloadProgress = downloadProgress,
                onPauseDownload = { scope.launch { downloadManager.pauseDownload(it) } },
                onResumeDownload = { scope.launch { downloadManager.resumeDownload(it) } },
                onCancelDownload = { scope.launch { downloadManager.cancelDownload(it) } }
            )
            1 -> DownloadedTracksSection(
                downloadedTracks = downloadedTracks,
                onTrackSelected = onTrackSelected,
                onDeleteTrack = { scope.launch { downloadManager.deleteDownloadedTrack(it) } }
            )
            2 -> SmartDownloadSection(
                downloadManager = downloadManager,
                onSmartDownload = { scope.launch { downloadManager.smartDownload() } }
            )
        }
    }
    
    // Smart Download Dialog
    if (showSmartDownload) {
        SmartDownloadDialog(
            downloadManager = downloadManager,
            onDismiss = { showSmartDownload = false }
        )
    }
}

@Composable
private fun DownloadStatsCard(
    downloadState: DownloadState,
    downloadedTracks: List<com.mizuiro.music.data.model.Track>,
    onSmartDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoolWhite),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Download Statistics",
                style = H3,
                color = FadedBlack,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Downloaded",
                    value = "${downloadedTracks.size}",
                    color = MizuiroBase
                )
                
                StatItem(
                    label = "In Queue",
                    value = "${downloadState.totalDownloads}",
                    color = SteelBlue
                )
                
                StatItem(
                    label = "Completed",
                    value = "${downloadState.completedDownloads}",
                    color = BlushPink
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MizuiroButton(
                text = "🎵 Smart Download",
                onClick = onSmartDownload,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = H2,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = Caption,
            color = FadedBlack
        )
    }
}

@Composable
private fun DownloadTabNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Queue", "Downloaded", "Smart")
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, tab ->
            MizuiroButton(
                text = tab,
                onClick = { onTabSelected(index) },
                secondary = selectedTab != index,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DownloadQueueSection(
    downloadQueue: List<DownloadItem>,
    downloadProgress: Map<String, DownloadProgress>,
    onPauseDownload: (String) -> Unit,
    onResumeDownload: (String) -> Unit,
    onCancelDownload: (String) -> Unit
) {
    if (downloadQueue.isEmpty()) {
        EmptyState(
            icon = "📥",
            title = "No Downloads in Queue",
            description = "Your download queue is empty. Add some tracks to download!",
            actionText = "Smart Download",
            onAction = { /* Handle smart download */ }
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(downloadQueue) { item ->
                DownloadQueueItem(
                    item = item,
                    progress = downloadProgress[item.id],
                    onPause = { onPauseDownload(item.id) },
                    onResume = { onResumeDownload(item.id) },
                    onCancel = { onCancelDownload(item.id) }
                )
            }
        }
    }
}

@Composable
private fun DownloadedTracksSection(
    downloadedTracks: List<com.mizuiro.music.data.model.Track>,
    onTrackSelected: (String) -> Unit,
    onDeleteTrack: (String) -> Unit
) {
    if (downloadedTracks.isEmpty()) {
        EmptyState(
            icon = "🎵",
            title = "No Downloaded Tracks",
            description = "Download some tracks to listen offline!",
            actionText = "Smart Download",
            onAction = { /* Handle smart download */ }
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(downloadedTracks) { track ->
                DownloadedTrackItem(
                    track = track,
                    onTrackSelected = { onTrackSelected(track.id) },
                    onDeleteTrack = { onDeleteTrack(track.id) }
                )
            }
        }
    }
}

@Composable
private fun SmartDownloadSection(
    downloadManager: DownloadManager,
    onSmartDownload: () -> Unit
) {
    var recommendations by remember { mutableStateOf<List<DownloadRecommendation>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        recommendations = downloadManager.getSmartDownloadRecommendations()
    }
    
    Column {
        Text(
            text = "Smart Download Recommendations",
            style = H3,
            color = FadedBlack,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "AI-powered suggestions based on your listening habits",
            style = Caption,
            color = SteelBlue
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        MizuiroButton(
            text = "🚀 Start Smart Download",
            onClick = onSmartDownload,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (recommendations.isEmpty()) {
            EmptyState(
                icon = "🤖",
                title = "No Recommendations",
                description = "We need more data about your music preferences to make recommendations.",
                actionText = "Listen to Music",
                onAction = { /* Handle navigation to music */ }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recommendations.take(10)) { recommendation ->
                    DownloadRecommendationItem(
                        recommendation = recommendation,
                        onDownload = { 
                            scope.launch {
                                downloadManager.downloadTrack(recommendation.track, recommendation.priority)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadQueueItem(
    item: DownloadItem,
    progress: DownloadProgress?,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoolWhite),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.track.title,
                        style = H4,
                        color = FadedBlack,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.track.artist,
                        style = Caption,
                        color = SteelBlue
                    )
                }
                
                Text(
                    text = when (item.status) {
                        DownloadStatus.PENDING -> "⏳"
                        DownloadStatus.DOWNLOADING -> "⬇️"
                        DownloadStatus.PAUSED -> "⏸️"
                        DownloadStatus.COMPLETED -> "✅"
                        DownloadStatus.FAILED -> "❌"
                        DownloadStatus.CANCELLED -> "🚫"
                    },
                    style = H3
                )
            }
            
            if (progress != null && item.status == DownloadStatus.DOWNLOADING) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress bar
                LinearProgressIndicator(
                    progress = progress.progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MizuiroBase,
                    trackColor = IcyGrey
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(progress.progress * 100).toInt()}%",
                        style = Caption,
                        color = FadedBlack
                    )
                    Text(
                        text = formatBytes(progress.downloadedBytes) + " / " + formatBytes(progress.totalBytes),
                        style = Caption,
                        color = SteelBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (item.status) {
                    DownloadStatus.PENDING, DownloadStatus.DOWNLOADING -> {
                        MizuiroButton(
                            text = "Pause",
                            onClick = onPause,
                            secondary = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    DownloadStatus.PAUSED -> {
                        MizuiroButton(
                            text = "Resume",
                            onClick = onResume,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    else -> {}
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                MizuiroButton(
                    text = "Cancel",
                    onClick = onCancel,
                    secondary = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DownloadedTrackItem(
    track: com.mizuiro.music.data.model.Track,
    onTrackSelected: () -> Unit,
    onDeleteTrack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoolWhite),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = H4,
                    color = FadedBlack,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = track.artist,
                    style = Caption,
                    color = SteelBlue
                )
                Text(
                    text = "📱 Offline",
                    style = Caption,
                    color = BlushPink
                )
            }
            
            Row {
                MizuiroButton(
                    text = "Play",
                    onClick = onTrackSelected,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                MizuiroButton(
                    text = "🗑️",
                    onClick = onDeleteTrack,
                    secondary = true
                )
            }
        }
    }
}

@Composable
private fun DownloadRecommendationItem(
    recommendation: DownloadRecommendation,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CoolWhite),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recommendation.track.title,
                    style = H4,
                    color = FadedBlack,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = recommendation.track.artist,
                    style = Caption,
                    color = SteelBlue
                )
                Text(
                    text = recommendation.reason,
                    style = Caption,
                    color = BlushPink
                )
            }
            
            MizuiroButton(
                text = "Download",
                onClick = onDownload
            )
        }
    }
}

@Composable
private fun SmartDownloadDialog(
    downloadManager: DownloadManager,
    onDismiss: () -> Unit
) {
    // Smart download dialog implementation
    // This would show a dialog with download options and confirmations
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024 * 1024)}GB"
        bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)}MB"
        bytes >= 1024 -> "${bytes / 1024}KB"
        else -> "${bytes}B"
    }
}
