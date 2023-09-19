package com.example.chattingapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chattingapp.model.Message
import com.example.chattingapp.model.User
import com.example.chattingapp.model.forwardUsers
import com.example.chattingapp.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                }
            }
            val intent = intent
            val receiverDetails:ArrayList<String> = intent.getStringArrayListExtra("receiverDetails") as ArrayList<String>
            val senderDetails:kotlin.collections.ArrayList<String> = intent.getStringArrayListExtra("senderDetails") as ArrayList<String>
            Chat(receiverDetails,senderDetails)
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Chat(receiverDetails:kotlin.collections.ArrayList<String>,senderDetails:kotlin.collections.ArrayList<String>){
        val receiverUserId = receiverDetails[0]
        val receiverUserName=receiverDetails[1]
        val receiverName=receiverDetails[2]
        val receiverProfilePhoto=receiverDetails[3]
        var isClicked by remember {
            mutableStateOf(false)
        }
        var clickedMessage by remember {
            mutableStateOf<Message>(Message("","",false,received = false,"",""))
        }
        var expandMenu by remember {
            mutableStateOf(false)
        }
        var selectedMenu by remember {
            mutableStateOf(0)
        }
        Column(modifier = Modifier.fillMaxHeight()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {startActivity(Intent(this@ChatActivity,HomeActivity::class.java))}) {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(30.dp))
                }
                Box(
                    modifier = Modifier
                        .shadow(shape = RoundedCornerShape(25.dp), elevation = 0.dp)
                ) {
                    AsyncImage(model =receiverProfilePhoto,
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
                Column(modifier = Modifier.padding(start=10.dp)
                    .clickable {}) {
                    Text(text = receiverName,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(text = receiverUserName,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { expandMenu=true }) {
                    Icon(imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        modifier = Modifier.size(50.dp).padding(end=20.dp)
                    )
                    DropdownMenu(
                        expanded = expandMenu,
                        onDismissRequest = { expandMenu = false },
                        modifier = Modifier
                            .width(120.dp)
                            .background(TextFieldColor)
                    ) {
                        DropdownMenuItem(onClick = {
                            selectedMenu=1
                            expandMenu = false
                        }) {
                            Text(text = "View Profile")
                        }
                        DropdownMenuItem(onClick = {
                            selectedMenu = 2
                            expandMenu = false
                        }) {
                            Text(text = "Clear Chat")
                        }
                    }
                }
            }
            if(selectedMenu==2){
                val user = Firebase.auth.currentUser!!
                val myRef = FirebaseDatabase.getInstance().getReference("User").child(user.uid).child("Chats").child(receiverUserId)
                myRef.removeValue()
            }
            val chat = remember { mutableStateListOf<Message>() }
            val myUserId = Firebase.auth.currentUser?.uid.toString()
            var date=""
            val dbRef= FirebaseDatabase.getInstance().getReference("User").child(myUserId).child("Chats").child(receiverUserId)
            Column(modifier = Modifier
                .fillMaxHeight(0.83f)
                .verticalScroll(rememberScrollState())) {
                DisposableEffect(Unit){
                    val listener=object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            chat.clear()
                            for(i in snapshot.children){
                                if(i.hasChildren()){
                                    val messageId=i.child("messageId").value.toString()
                                    val message=i.child("message").value.toString()
                                    val isPhoto=i.child("photo").value.toString().toBoolean()
                                    val isReceived=i.child("received").value.toString().toBoolean()
                                    val date=i.child("date").value.toString()
                                    val time=i.child("time").value.toString()
                                    chat.add(Message(messageId,message,isPhoto,isReceived,date,time))
                                }

                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@ChatActivity, "${error.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    dbRef.addValueEventListener(listener)
                    // This block will be called when the composable is removed from the UI
                    onDispose {
                        dbRef.removeEventListener(listener) // Remove the listener to avoid leaks
                    }

                }
                for(i in chat){
                    if(date=="" || i.date !=date){
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical=2.dp),
                        horizontalArrangement = Arrangement.Center) {
                            Text(text = i.date,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(0.2f).shadow(elevation = 10.dp, shape = RoundedCornerShape(10.dp)).background(
                               Brush.verticalGradient(colors = listOf( Color.White, Color.LightGray))
                            ))
                        }
                    }
                    date=i.date
                    if(!i.photo){
                        if(i.received){
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp, top = 10.dp)
                                .combinedClickable(
                                    true,
                                    onLongClick = {
                                        isClicked = true
                                        clickedMessage = i
                                    },
                                    onClick = {}),
                                horizontalArrangement = Arrangement.Start){
                                    Column() {
                                        Text(
                                            text=i.message,
                                            textAlign = TextAlign.Start,
                                            fontSize = 18.sp,
                                            modifier = Modifier
                                                .padding(end = 10.dp, start = 10.dp)
                                                .clip(shape = RoundedCornerShape(15.dp))
                                                .background(color = TopBarColor)
                                                .padding(end = 10.dp, start = 10.dp, bottom = 10.dp)
                                        )
                                        Text(
                                            text=i.time,
                                            textAlign = TextAlign.End,
                                            fontSize = 12.sp,
                                            modifier = Modifier
                                                .padding(end = 10.dp, bottom = 10.dp)
                                                .align(Alignment.End)
                                        )
                                    }
                            }
                        }else{
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    true,
                                    onLongClick = {
                                        isClicked = true
                                        clickedMessage = i
                                    },
                                    onClick = {})
                                .padding(start = 4.dp, top = 4.dp),
                                horizontalArrangement = Arrangement.End){
                                Column() {
                                    Text(
                                        text=i.message,
                                        textAlign = TextAlign.Start,
                                        fontSize = 18.sp,
                                        modifier = Modifier
                                            .padding(end = 10.dp, start = 30.dp)
                                            .clip(shape = RoundedCornerShape(15.dp))
                                            .background(color = TopBarColor)
                                            .padding(end = 10.dp, start = 10.dp)
                                    )
                                    Text(
                                        text=i.time,
                                        textAlign = TextAlign.End,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .padding(end = 10.dp, bottom = 10.dp)
                                            .align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            var message by remember {
                mutableStateOf("")
            }
            var sendMessage by remember {
                mutableStateOf(false)
            }
            Row(modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxHeight(),
                verticalAlignment = Alignment.Bottom){
                TextField(
                    value = message,
                    onValueChange = {
                        message=it
                    },
                    leadingIcon = { IconButton(onClick = { /*TODO*/ }) {
                        Icon(painter = painterResource(id =R.drawable.ic_attach_foreground )  , contentDescription = "Attachment",
                            Modifier.size(50.dp))
                    }},
                    trailingIcon = {
                        if(message.isNotBlank()){
                            IconButton(onClick = { sendMessage=true
                                                 chat.clear()},
                                modifier = Modifier
                                    .clip(shape = CircleShape)
                                    .background(color = Color(11, 206, 240, 242))) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                            }
                        }
                    },
                    placeholder = { Text(text = "Message..") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(94, 205, 243, 37)
                    ),
                    shape = RoundedCornerShape(35.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                        .align(Alignment.Bottom)
                )
            }
            if(sendMessage){
                Send(message = message, receiverUserId = receiverUserId,receiverName=receiverName,receiverProfilePhoto=receiverProfilePhoto,receiverUserName,senderDetails)
                message=""
                sendMessage=false
            }
            if(isClicked){
               MessageClicked(clickedMessage, onClick = {isClicked=false},receiverDetails,senderDetails )
            }
        }
    }

//    fun call(receiverDetails: ArrayList<String>,isVideoCall:Boolean){
//        val targetUserID: String = receiverDetails[0]/* The ID of the user you want to call */
//        val targetUserName: String = receiverDetails[1]/* The username of the user you want to call */
//        val context: Context = applicationContext/* Android context */
//
//        val button = ZegoSendCallInvitationButton(context).apply {
//            setIsVideoCall(isVideoCall)
//            resourceID = "zego_uikit_call"
//            setInvitees(listOf(ZegoUIKitUser(targetUserID, targetUserName)))
//        }
//    }
//    private fun startService(senderDetails:kotlin.collections.ArrayList<String>) {
//        val senderUserId = Firebase.auth.currentUser?.uid!!
//        val senderUserName=senderDetails[1]
//        val application: Application =
//            applicationContext as Application/* your Application context */
//        val appID: Long =1866280
//        val appSign: String = "50325e3f3b60422360a0c21977b84587d7cec3c57d1234d8d77e046db821d72e"
//        val userID: String = senderUserId/* yourUserID */
//        val userName: String =senderUserName /* yourUserName */
//
//        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig().apply {
//            this.notifyWhenAppRunningInBackgroundOrQuit = true
//        }
//
//        val notificationConfig = ZegoNotificationConfig().apply {
//            sound = "zego_uikit_sound_call"
//            channelID = "CallInvitation"
//            channelName = "CallInvitation"
//        }
//
//        ZegoUIKitPrebuiltCallInvitationService.init(
//            application,
//            appID,
//            appSign,
//            userID,
//            userName,
//            callInvitationConfig
//        )
//    }
}
@SuppressLint("SimpleDateFormat")
@Composable
fun Send(message:String,receiverUserId:String,receiverName:String,receiverProfilePhoto: String,receiverUserName:String,senderDetails: ArrayList<String>){
    val senderUserId=auth.currentUser?.uid!!
    val currentDate = Date()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    val formattedDate = dateFormat.format(currentDate)
    val date=formattedDate.substring(0,10)
    val time=formattedDate.substring(11,16)
    val dbRef= FirebaseDatabase.getInstance().getReference("User")
    val senderSide = dbRef.child(senderUserId).child("Chats").child(receiverUserId)
    senderSide.child("receiverUserId").setValue(receiverUserId)
    senderSide.child("receiverName").setValue(receiverName)
    senderSide.child("receiverUserName").setValue(receiverUserName)
    senderSide.child("receiverProfilePhoto").setValue(receiverProfilePhoto)
    senderSide.child("lastMessage").setValue(message)
    senderSide.child("date").setValue(date)
    senderSide.child("time").setValue(time)
    senderSide.child("received").setValue(false)
    val senderMessage=senderSide.push()
    val messageId = senderMessage.key ?: ""     //finding value of push node
    val receiverSide=dbRef.child(receiverUserId).child("Chats").child(senderUserId)
    receiverSide.child("receiverUserId").setValue(senderUserId)
    receiverSide.child("lastMessage").setValue(message)
    receiverSide.child("date").setValue(date)
    receiverSide.child("time").setValue(time)
    receiverSide.child("received").setValue(true)
    receiverSide.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (!dataSnapshot.hasChild("receiverName")) {
                receiverSide.child("receiverName").setValue(senderDetails[0])
            }
            if(!dataSnapshot.hasChild("receiverUserName")){
                receiverSide.child("receiverUserName").setValue(senderDetails[1])
            }
            if(!dataSnapshot.hasChild("receiverProfilePhoto")){
                receiverSide.child("receiverProfilePhoto").setValue(senderDetails[2])
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            println("Error: ${databaseError.message}")
        }
    })
    val receiverMessage=receiverSide.child(messageId)
    senderMessage.setValue(Message(messageId,message,false,received = false,date,time))
        .addOnSuccessListener {
            receiverMessage.setValue(Message(messageId,message,false,received = true,date,time))
        }

}