package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceOptionsModal(
    resource: Resource,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit,
    onShare: () -> Unit,
    onToggleFavorite: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F1C30), // Dark background
        contentColor = Color.White,
        dragHandle = {
            // Custom Drag Handle
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Icon Placeholder (Optional, adds visual anchor)
            Box(
                 modifier = Modifier
                     .size(48.dp)
                     .background(
                         when(resource.type) {
                             ResourceType.Link -> Color(0xFFEF4444).copy(alpha = 0.1f)
                             ResourceType.Note -> Color(0xFF3B82F6).copy(alpha = 0.1f)
                             ResourceType.Todo -> Color(0xFFA855F7).copy(alpha = 0.1f)
                         }, 
                          CircleShape
                     ),
                 contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(resource.type) {
                        ResourceType.Link -> Icons.Default.Link
                        ResourceType.Note -> Icons.Default.Description
                        ResourceType.Todo -> Icons.Default.Check
                    }, 
                    contentDescription = null, 
                    tint = when(resource.type) {
                        ResourceType.Link -> Color(0xFFEF4444)
                        ResourceType.Note -> Color(0xFF3B82F6)
                        ResourceType.Todo -> Color(0xFFA855F7)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = resource.description.ifEmpty { "No description provided" },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Section 1
            OptionItem(icon = Icons.Outlined.Edit, text = "Edit Metadata", onClick = onEdit)
            OptionItem(icon = Icons.Outlined.Download, text = "Export as PDF", onClick = onExport)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.08f))

            // Section 2
            OptionItem(icon = Icons.Outlined.Share, text = "Share", onClick = onShare)
            
            val favoriteText = if (resource.isFavorite) "Remove from Favorites" else "Add to Favorites"
            val favoriteIcon = if (resource.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder
            val favoriteTint = if (resource.isFavorite) Color(0xFFF59E0B) else Color.White
            OptionItem(icon = favoriteIcon, text = favoriteText, iconColor = favoriteTint, onClick = onToggleFavorite)
            
            val archiveText = if (resource.isArchived) "Unarchive" else "Archive"
            val archiveIcon = if (resource.isArchived) Icons.Outlined.Unarchive else Icons.Outlined.Inventory2
            OptionItem(icon = archiveIcon, text = archiveText, onClick = onArchive)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.08f))

            // Section 3 (Delete)
            OptionItem(
                icon = Icons.Outlined.Delete, 
                text = "Delete Vault Item", 
                textColor = Color(0xFFEF4444), 
                iconColor = Color(0xFFEF4444),
                onClick = onDelete
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun OptionItem(
    icon: ImageVector,
    text: String,
    textColor: Color = Color.White,
    iconColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.titleSmall, color = textColor, fontWeight = FontWeight.SemiBold)
    }
}
