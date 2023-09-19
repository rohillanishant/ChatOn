package com.example.chattingapp

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.w3c.dom.Text
@Composable
fun AppBar(
    selected: Int,
    onNavigationIconClick: () -> Unit,
    onMoreClicked: () -> Unit,
) {
    TopAppBar(
        title = {
                // Show the title based on the selected tab
                Text(text = when (selected) {
                    0 -> "ChatOn"
                    1 -> "Explore"
                    2 -> "My Profile"
                    3 -> "Settings"
                    else -> "Help"
                })
        },
        backgroundColor = Color(22, 139, 179, 37),
        contentColor = Color(99, 123, 158, 255),
        navigationIcon = { IconButton(onClick = onNavigationIconClick) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Toggle drawer"
            )
        }
        },
        actions = {
            IconButton(onClick = onMoreClicked) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
            }
        }
    )
}
@Composable
fun ChatTopBar(userName:String,
               profilePhoto:String,
               onNavigationIconClick: () -> Unit,
               onMoreClicked: () -> Unit){
    TopAppBar(
        title = {
            // Show the title based on the selected tab
            Box(
                modifier = Modifier
                    // .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                    .shadow(shape = RoundedCornerShape(25.dp), elevation = 0.dp)
            ) {
                AsyncImage(model =profilePhoto,
                    contentDescription = "photo",
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .border(
                            5.dp,
                            color = Color(22, 139, 179, 37),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(shape = RoundedCornerShape(20.dp))
                        .size(40.dp)
                        .clickable { },
                    contentScale = ContentScale.Crop
                )
            }
            Text(text = userName,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top=8.dp,start=5.dp, end = 100.dp))
        },
        backgroundColor = Color.White,
        contentColor = Color.Black,
        navigationIcon = { IconButton(onClick = onNavigationIconClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(30.dp)
            )
        }
        },
        actions = {
            IconButton(onClick = onMoreClicked) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
            }
            IconButton(onClick = onMoreClicked) {
                Icon(imageVector = Icons.Default.Call, contentDescription = "Call")
            }
        }
    )
}