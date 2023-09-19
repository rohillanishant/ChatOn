package com.example.chattingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chattingapp.model.User
import com.example.chattingapp.model.forwardUsers
import com.example.chattingapp.ui.theme.AppColor
import com.example.chattingapp.ui.theme.ChattingAppTheme
import com.example.chattingapp.ui.theme.TopBarColor2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ForwardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val intent = intent
            val message:String= intent.getStringExtra("message").toString()
            Forward(message)
        }
    }
}
@Composable
fun Forward(message:String){
    var send by remember {
        mutableStateOf(false)
    }
    var clear by remember {
        mutableStateOf(false)
    }
    var user by remember {
        mutableStateOf("")
    }
    val senderDetails = remember {
        mutableStateListOf<User>()
    }
    val allUsers = remember { mutableStateListOf<forwardUsers>() }
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(
            value = user,
            onValueChange = {
                user=it
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search User",
                    modifier = Modifier.padding(start=10.dp)
                )
            },
            trailingIcon = {
                if(user!=""){
                    IconButton(onClick = { user=""}) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Toggle drawer",
                            modifier = Modifier.padding(start=10.dp)
                        )
                    }
                }
            },
            placeholder = { Text(text = "Search") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(94, 205, 243, 37)
            ),
            shape = RoundedCornerShape(15.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 10.dp, bottom = 10.dp)
        )


        DisposableEffect(Unit) {                            //so that one item won't appear multiple times
            val dRef = FirebaseDatabase.getInstance().getReference("User")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    allUsers.clear() // Clear the list before adding new data
                    for (postSnapshot in snapshot.children) {
                        val userId = postSnapshot.child("userId").value.toString()
                        val userName = postSnapshot.child("userName").value.toString()
                        val name = postSnapshot.child("name").value.toString()
                        val email = postSnapshot.child("email").value.toString()
                        val phoneNumber = postSnapshot.child("phoneNumber").value.toString()
                        val profilePhoto=postSnapshot.child("profilePhoto").value.toString()
                        val isOnline=postSnapshot.child("isOnline").value.toString().toBoolean()
                        if (userId != Firebase.auth.currentUser?.uid.toString() && (userName.startsWith(user) || name.startsWith(user))) {
                            allUsers.add(
                                forwardUsers(
                                    userId = userId,
                                    userName = userName,
                                    name = name,
                                    email = email,
                                    phoneNumber = phoneNumber ,
                                    profilePhoto = profilePhoto,
                                    isSelected = false
                                )
                            )
                        }else{
                          senderDetails.add(User(userId=userId, userName = userName, profilePhoto = profilePhoto,name=name,email= email, phoneNumber = phoneNumber))
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            dRef.addValueEventListener(listener)
            // This block will be called when the composable is removed from the UI
            onDispose {
                dRef.removeEventListener(listener) // Remove the listener to avoid leaks
            }
        }
        var countSelected by remember {
            mutableStateOf(0)
        }
        var  height by remember {
            mutableStateOf(1f)
        }
        height = if(countSelected>0){
            0.93f
        }else{
            1f
        }
        val filteredUsers = allUsers.filter { it.name.startsWith(user, ignoreCase = true) || it.userName.startsWith(user,true)}
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(height)
            .verticalScroll(rememberScrollState())) {
            for(i in filteredUsers){
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                    backgroundColor = Color.White,
                    elevation = 5.dp,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(
                            modifier = Modifier
                                .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                                .shadow(shape = RoundedCornerShape(25.dp), elevation = 0.dp)
                        ) {
                            AsyncImage(model = i.profilePhoto,
                                contentDescription = "photo",
                                modifier = Modifier
                                    // .shadow(elevation = 10.dp, shape = RoundedCornerShape(35.dp))
                                    .border(
                                        5.dp,
                                        color = Color(22, 139, 179, 37),
                                        shape = RoundedCornerShape(35.dp)
                                    )
                                    .clip(shape = RoundedCornerShape(35.dp))
                                    .size(70.dp)
                                    .clickable { },
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = i.name,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = 4.dp),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = i.userName,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = 4.dp),
                                fontFamily = FontFamily.Monospace,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 20.dp)
                                .background(
                                    if (i.isSelected) AppColor else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    BorderStroke(2.dp, TopBarColor2),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .size(30.dp)
                                .clickable {
                                    //check = !check
                                    i.isSelected = !i.isSelected
                                    if (i.isSelected) {
                                        countSelected++
                                    } else {
                                        countSelected--
                                    }
                                },
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            if (i.isSelected) Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(0.8f),verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceAround){
            if(countSelected>0){
                Button(onClick = {clear=true},
                    modifier = Modifier
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(15.dp)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = "Unselect All"
                        , fontWeight = FontWeight.Bold)
                }
                Button(onClick = {send=true},
                    modifier = Modifier
                        .width(130.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(15.dp)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Send"
                    , fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    if(clear){
        for(i in allUsers){
            i.isSelected=false
        }
        clear=false
    }

    if(send){
        for(i in allUsers){
            if(i.isSelected){
                Send(
                    message = message,
                    receiverUserId = i.userId,
                    receiverName = i.name,
                    receiverProfilePhoto = i.profilePhoto,
                    receiverUserName = i.userName,
                    senderDetails = arrayListOf(senderDetails[0].name,senderDetails[0].userName,senderDetails[0].profilePhoto)
                )
            }
        }
        send=false
    }

}