package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResourceCard(
    resource: Resource,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    val cardBackgroundColor = Color(0xFF0F1C30) // Dark Blue from screenshot approximation
    val iconBackgroundColor = when(resource.type) {
        ResourceType.Link -> Color(0xFFEF4444).copy(alpha = 0.2f) // Red bg
        ResourceType.Note -> Color(0xFF3B82F6).copy(alpha = 0.2f) // Blue bg
        ResourceType.Todo -> Color(0xFFA855F7).copy(alpha = 0.2f) // Purple bg
    }
    val iconColor = when(resource.type) {
        ResourceType.Link -> Color(0xFFEF4444)
        ResourceType.Note -> Color(0xFF3B82F6)
        ResourceType.Todo -> Color(0xFFA855F7)
    }
    val iconVector = when(resource.type) {
        ResourceType.Link -> Icons.Default.Link
        ResourceType.Note -> Icons.Default.Description
        ResourceType.Todo -> Icons.Default.Check
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBackgroundColor)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            // Header: Icon + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBackgroundColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(iconVector, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (resource.isFavorite) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp).clickable { onFavoriteClick() })
                        Spacer(modifier = Modifier.width(12.dp))
                    } else {
                         // Optional: Unfilled star or nothing? Screenshot shows filled star if favorite.
                         // Maybe show outline if not? Or just show nothing for now.
                    }
                    
                    Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = TextGrey, modifier = Modifier.size(20.dp).clickable { onMoreClick() })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Description
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = resource.description.ifEmpty { "No description" },
                style = MaterialTheme.typography.bodySmall,
                color = TextGrey,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Todo Specific "DUE" Row
            if (resource.type == ResourceType.Todo && (!resource.dueDate.isNullOrEmpty() || !resource.priority.isNullOrEmpty())) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "DUE:", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = TextGrey.copy(alpha = 0.7f), 
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                    
                    if (!resource.dueDate.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFA855F7).copy(alpha=0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, null, tint = Color(0xFFA855F7), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(resource.dueDate, style = MaterialTheme.typography.labelSmall, color = Color(0xFFA855F7), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    if (!resource.priority.isNullOrEmpty()) {
                        val priorityColor = when(resource.priority) {
                            "High" -> Color(0xFFEF4444)
                            "Medium" -> Color(0xFFF59E0B)
                            else -> Color(0xFF6366F1)
                        }
                        Box(
                            modifier = Modifier
                                .background(priorityColor.copy(alpha=0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Flag, null, tint = priorityColor, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(resource.priority, style = MaterialTheme.typography.labelSmall, color = priorityColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Footer / Metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category Chip
                if (!resource.category.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFF2E65F3).copy(alpha=0.3f), RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E65F3).copy(alpha=0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(resource.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E65F3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp)) // Placeholder to keep spacing alignment if needed
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (resource.assetCount > 0) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF59E0B).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AttachFile, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(resource.assetCount.toString(), style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(Icons.Default.CalendarToday, null, tint = TextGrey, modifier = Modifier.size(14.dp))
                         Spacer(modifier = Modifier.width(6.dp))
                         Text(resource.dateCreated.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextGrey, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
