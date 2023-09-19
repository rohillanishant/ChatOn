package com.example.chattingapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chattingapp.model.MenuItem

@Composable
fun DrawerHeader(name:String,userName:String,profilePhoto:String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(255, 255, 255, 255), Color(
                            141,
                            153,
                            247,
                            255
                        )
                    )
                )
            )
            .padding(vertical = 64.dp, horizontal = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        AsyncImage(model =  profilePhoto,
            contentDescription = "photo",
            modifier = Modifier
                .border(5.dp,color = Color.White, shape = RoundedCornerShape(75.dp))
                .clip(shape = RoundedCornerShape(50.dp))
                .size(100.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .padding(end = 5.dp)

        ) {
            Text(
                text = name.toUpperCase(),
                fontSize = 22.sp,
                color =  Color.Black,
                fontWeight = FontWeight.W500,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = userName,
                fontSize = 20.sp,
                color = Color.Black,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.W500,
                fontFamily = FontFamily.Serif
            )
        }
    }
}
@Composable
fun DrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    itemTextStyle: TextStyle = TextStyle(fontSize = 18.sp),
    onItemClick: (MenuItem) -> Unit,
    selected:Int
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = if(selected==item.id){Brush.horizontalGradient(
                                listOf(
                                    Color(255, 255, 255, 255), Color(155, 165, 221, 255)
                                )
                            )

                      //  Color(22,139,179,37)
                    }else{
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                    })
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(16.dp),
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}