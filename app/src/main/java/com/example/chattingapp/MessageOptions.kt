package com.example.chattingapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.chattingapp.model.Message
import com.example.chattingapp.model.User
import com.example.chattingapp.model.forwardUsers
import com.example.chattingapp.ui.theme.AppColor
import com.example.chattingapp.ui.theme.TopBarColor2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

@SuppressLint("SuspiciousIndentation")
@Composable
fun MessageClicked(message:Message,onClick:()-> Unit,receiverDetails:kotlin.collections.ArrayList<String>,senderDetails:kotlin.collections.ArrayList<String>){
    val receiverUserId = receiverDetails[0]
    var isDialog by remember {
        mutableStateOf(true)
    }
    var iscopyText by remember {
        mutableStateOf(false)
    }
    var forwardMessage by remember {
        mutableStateOf(false)
    }
    var isDeleteForMe by remember {
        mutableStateOf(false)
    }
    var isDeleteForEveryone by remember {
        mutableStateOf(false)
    }
    if (isDialog) {
        AlertDialog(onDismissRequest = { isDialog=false
                                       onClick()},
            backgroundColor = Color.White,
            shape = RoundedCornerShape(15.dp),
            buttons = {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth(0.9f)
                        .padding(15.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        onClick = { iscopyText=true },
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(15.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.copy),
                                contentDescription = "Copy Icon",
                                Modifier.size(27.dp),
                                tint = Color.Black
                            )
                            Text(
                                text = "Copy Text",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        onClick = { forwardMessage=true},
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .shadow(elevation = 2.dp, RoundedCornerShape(15.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.forward),
                                contentDescription = "Forward Icon",
                                Modifier.size(27.dp),
                                tint = Color.Black
                            )
                            Text(
                                text = "Forward",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        onClick = {isDeleteForMe=true},
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .shadow(elevation = 2.dp, RoundedCornerShape(15.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Icon",
                                tint = Color.Red
                            )
                            Text(
                                text = "Delete for Me",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    if(!message.received){
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                            onClick = {isDeleteForEveryone=true},
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .fillMaxWidth()
                                .shadow(elevation = 2.dp, RoundedCornerShape(15.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Icon",
                                    tint = Color.Red
                                )
                                Text(
                                    text = "Delete for Everyone",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        )
        if(iscopyText){
            iscopyText=copyText(message)
            onClick()
        }
        if(isDeleteForMe){
            isDeleteForMe=deleteForMe(message,receiverUserId)
            onClick()
        }
        if(isDeleteForEveryone){
            isDeleteForEveryone= deleteForEveryone(message,receiverUserId)
            onClick()
        }
        if(forwardMessage){
          val intent=Intent(LocalContext.current,ForwardActivity::class.java)
            intent.putExtra("message",message.message)
            intent.putStringArrayListExtra("senderDetails", senderDetails)
            LocalContext.current.startActivity(intent)
            onClick()
            forwardMessage=false
        }
    }
}

fun deleteForMe(message: Message,receiverUserId: String):Boolean{
    val userId=Firebase.auth.currentUser?.uid!!
    val dbRef=FirebaseDatabase.getInstance().getReference("User").child(userId).child("Chats").child(receiverUserId)
    dbRef.child(message.messageId).removeValue()
    return false
}
fun deleteForEveryone(message: Message,receiverUserId: String):Boolean{
    val senderUserId=Firebase.auth.currentUser?.uid!!
    var dbRef=FirebaseDatabase.getInstance().getReference("User").child(senderUserId).child("Chats").child(receiverUserId)
    dbRef.child(message.messageId).removeValue()
    dbRef=FirebaseDatabase.getInstance().getReference("User").child(receiverUserId).child("Chats").child(senderUserId)
    dbRef.child(message.messageId).removeValue()
    return false
}
@Composable
fun copyText(message: Message):Boolean{
    val clipboard = LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", message.message)
    Toast.makeText(LocalContext.current,"Text Copied", Toast.LENGTH_SHORT).show()
    clipboard.setPrimaryClip(clipData)
    return false
}