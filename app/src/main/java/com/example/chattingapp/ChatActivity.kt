package com.example.chattingapp

import android.annotation.SuppressLint
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import androidx.compose.foundation.background
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.text.util.Linkify
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chattingapp.model.Message
import com.example.chattingapp.model.User
import com.example.chattingapp.model.forwardUsers
import com.example.chattingapp.notifications.Constants.Companion.SERVER_KEY
import com.example.chattingapp.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import org.json.JSONObject
import java.net.URI
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
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun Chat(receiverDetails:kotlin.collections.ArrayList<String>,senderDetails:kotlin.collections.ArrayList<String>){
        val receiverUserId = receiverDetails[0]
        val receiverUserName=receiverDetails[1]
        val receiverName=receiverDetails[2]
        val receiverProfilePhoto=receiverDetails[3]
        val receiverToken=receiverDetails[4]
        val senderName=senderDetails[0]
        val senderUserName=senderDetails[1]
        val senderProfilePhoto=senderDetails[2]
        var openProfile by remember {
            mutableStateOf(false)
        }
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
        var showPhoto by remember {
            mutableStateOf<String?>(null)
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
                            .clickable {
                                val intent = Intent(this@ChatActivity, ProfileActivity::class.java)
                                intent.putStringArrayListExtra("receiverDetails", receiverDetails)
                                intent.putStringArrayListExtra("senderDetails", senderDetails)
                                startActivity(intent)
                            },
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        val intent = Intent(this@ChatActivity, ProfileActivity::class.java)
                        intent.putStringArrayListExtra("receiverDetails", receiverDetails)
                        intent.putStringArrayListExtra("senderDetails", senderDetails)
                        startActivity(intent)
                    }
                ) {
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
                        modifier = Modifier
                            .size(50.dp)
                            .padding(end = 20.dp)
                    )
                    DropdownMenu(
                        expanded = expandMenu,
                        onDismissRequest = { expandMenu = false },
                        modifier = Modifier
                            .width(120.dp)
                            .background(TextFieldColor)
                    ) {
                        DropdownMenuItem(onClick = {
                            val intent=Intent(this@ChatActivity,ProfileActivity::class.java)
                            intent.putStringArrayListExtra("receiverDetails",receiverDetails)
                            intent.putStringArrayListExtra("senderDetails",senderDetails)
                            startActivity(intent)
//                            selectedMenu=1
//                            expandMenu = false
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
            var message by remember {
                mutableStateOf("")
            }
            val scrollState = rememberScrollState(0)
            var date=""
            val chat = remember { mutableStateListOf<Message>() }
            val myUserId = Firebase.auth.currentUser?.uid.toString()
            val dbRef= FirebaseDatabase.getInstance().getReference("User").child(myUserId).child("Chats").child(receiverUserId)
            Column(modifier = Modifier
                .fillMaxHeight(if (message.length > 80) 0.83f else 0.86f)
                .verticalScroll(scrollState)) {
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
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.Center) {
                            Text(text = i.date,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.2f)
                                .shadow(elevation = 10.dp, shape = RoundedCornerShape(10.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color.LightGray
                                        )
                                    )
                                ))
                        }
                    }

                    date=i.date
                    if(i.photo){
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
                                horizontalArrangement = Arrangement.Start) {
                                Column() {
                                    AsyncImage(
                                        model = i.message,
                                        contentDescription = "photo",
                                        modifier = Modifier
                                            .padding(end = 40.dp)
                                            .width(200.dp)
                                            .height(200.dp)
                                            .shadow(
                                                elevation = 10.dp,
                                                //  shape = RoundedCornerShape(75.dp)
                                            )
                                            .border(
                                                5.dp,
                                                color = TopBarColor,
                                                //shape = RoundedCornerShape(75.dp)
                                            )
                                            .clickable { showPhoto = i.message },
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = i.time,
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
                                    AsyncImage(
                                        model = i.message,
                                        contentDescription = "photo",
                                        modifier = Modifier
                                            .padding(start = 40.dp)
                                            .width(200.dp)
                                            .height(200.dp)
                                            .shadow(
                                                elevation = 10.dp,
                                                //  shape = RoundedCornerShape(75.dp)
                                            )
                                            .border(
                                                5.dp,
                                                color = TopBarColor,
                                                //shape = RoundedCornerShape(75.dp)
                                            )
                                            .clickable { showPhoto = i.message },
                                        contentScale = ContentScale.Crop
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
                    if(!i.photo){
                        val annotatedString = buildAnnotatedString {
                            append(i.message)
                            val regex = Regex("""(https?://\S+\.(\w{2,}|co|org|net|edu|gov)(\S+)?[\w\-\.,@?^=%&amp;:/\*\(\)]*)""") // Matches valid URLs
                            val matches = regex.findAll(i.message)
                            for (match in matches) {
                                val start = match.range.first
                                val end = match.range.last + 1
                                val url = i.message.substring(start, end)
                                addStyle(
                                    style = SpanStyle(
                                        color = Color.Blue, // Use the color you want for the link
                                        textDecoration = TextDecoration.Underline,
                                        fontSize = 18.sp,
                                    ),
                                    start = start,
                                    end = end
                                )
                                addStringAnnotation(
                                    "url",
                                    url,
                                    start,
                                    end
                                )
                            }
                        }

                        val uriHandler = LocalUriHandler.current
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
                                        ClickableText(
                                            modifier = Modifier
                                                .padding(end = 30.dp, start = 10.dp)
                                                .clip(shape = RoundedCornerShape(15.dp))
                                                .background(color = TopBarColor)
                                                .padding(end = 10.dp, start = 10.dp),
                                            text = annotatedString,
                                            style = TextStyle(fontSize = 18.sp),
                                            onClick = { offset ->
                                                val uri=annotatedString.getStringAnnotations("url", offset, offset)
                                                    .firstOrNull()?.item
                                                if(uri!=null) uriHandler?.openUri(uri)
                                            },
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
//                                    Text(
//                                        text=i.message,
//                                        textAlign = TextAlign.Start,
//                                        fontSize = 18.sp,
//                                        modifier = Modifier
//                                            .padding(end = 10.dp, start = 30.dp)
//                                            .clip(shape = RoundedCornerShape(15.dp))
//                                            .background(color = TopBarColor)
//                                            .padding(end = 10.dp, start = 10.dp)
//                                    )
                                    ClickableText(
                                        modifier = Modifier
                                            .padding(end = 10.dp, start = 30.dp)
                                            .clip(shape = RoundedCornerShape(15.dp))
                                            .background(color = TopBarColor)
                                            .padding(end = 10.dp, start = 10.dp),
                                        text = annotatedString,
                                        style = TextStyle(fontSize = 18.sp),
                                        onClick = { offset ->
                                            val uri=annotatedString.getStringAnnotations("url", offset, offset)
                                                .firstOrNull()?.item
                                            if(uri!=null) uriHandler?.openUri(uri)
                                        },
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
                LaunchedEffect(Unit) {          //to reach latest chat
                    scrollState.animateScrollTo(Int.MAX_VALUE)
                }

            }

            var sendMessage by remember {
                mutableStateOf(false)
            }
            var sendPhoto by remember {
                mutableStateOf(false)
            }
            var imageUris by remember {
                mutableStateOf<List<Uri>>(emptyList())
            }
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(),
                onResult = {
                    imageUris=it
                }
            )
            Row(modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxHeight(),
                verticalAlignment = Alignment.Bottom){
                TextField(
                    value = message,
                    onValueChange = {
                        message=it
                    },
                    leadingIcon = { IconButton(onClick = {launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        sendPhoto=true}) {
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
                        .padding(top = 2.dp, bottom = 10.dp)
                        .align(Alignment.Bottom)
                )
            }
            if(sendPhoto){
                val links = remember {
                    mutableStateListOf<String>()
                }
                var uploadedToStorage by remember {
                    mutableStateOf(false)
                }
                val totalImages = imageUris.size
                var successfulUploads = 0
                if(imageUris.isEmpty()) println("Nishant Empty")
                for(i in imageUris){
                    val storage = FirebaseStorage.getInstance()
                    println("Nishant $i")
                    val storageRef = storage.reference.child("users/${auth.currentUser?.uid!!}/$receiverUserId/${i.toString().replace("/","")}") //
                    val uploadTask = storageRef.putFile(i)
                    uploadTask.addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            val downloadUrl = downloadUri.toString()
                            println("Nishant success $downloadUrl")
                            //  Send(message = downloadUrl, receiverUserId = receiverUserId,receiverName=receiverName,receiverProfilePhoto=receiverProfilePhoto,receiverUserName,senderDetails,true)
                            links.add(downloadUrl)
                            successfulUploads++
                            if (successfulUploads == totalImages) {
                                // All images are uploaded, now send messages
                                uploadedToStorage=true

                            }
                            // dbRef.child("profilePhoto").setValue(downloadUrl)
                            // TODO: Save the downloadUrl to the user's profile or database
                        }
                    }.addOnFailureListener {
                        //callback("Failed to upload profile Photo")
                        println("Nishant Fail")
                    }
                    //var count=links.size
                    //println("nishant $count")
                    if(uploadedToStorage){
                        for (link in links) {
                            Send(link, receiverUserId, receiverName, receiverProfilePhoto, receiverUserName, senderName,senderUserName,senderProfilePhoto, true,receiverToken)
                            println("NIshant 9")
                        }
                        sendPhoto=false
                    }
                }

            }
            if(sendMessage){
                Send(message = message, receiverUserId = receiverUserId,receiverName=receiverName,receiverProfilePhoto=receiverProfilePhoto,receiverUserName,senderName,senderUserName,senderProfilePhoto,false,receiverToken)
                message=""
                sendMessage=false
            }
            if(isClicked){
               MessageClicked(clickedMessage, onClick = {isClicked=false},receiverDetails,senderDetails )
            }
        }
        if(showPhoto!=null){
            Column(modifier = Modifier.fillMaxSize(1f)) {
                AsyncImage(model = showPhoto,
                    contentDescription = "photo",
                    modifier = Modifier
                        .fillMaxHeight(0.93f)
                        .fillMaxWidth(1f)
                        .background(Color.White)
                        //.shadow(elevation = 10.dp, shape = RoundedCornerShape(75.dp))
                        .border(
                            5.dp,
                            color = Color(22, 139, 179, 37),
                            // shape = RoundedCornerShape(150.dp)
                        )
                        // .clip(shape =RoundedCornerShape(150.dp))
                        .size(250.dp)
                        .clickable { },
                    contentScale = ContentScale.Crop
                )
                Row(modifier = Modifier
                    .fillMaxWidth(1f)
                    .background(Color.White)
                    .padding(bottom = 0.dp),verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceAround){
                    Button(onClick = {showPhoto = null},
                        modifier = Modifier
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(15.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = "Back"
                            , fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = { downloadPhotoToDownloads(showPhoto,"ChatOnImage.jpg") },
                        modifier = Modifier
                            .width(130.dp)
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(15.dp)),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Download"
                            , fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    fun downloadPhotoToDownloads( uri: String?, fileName: String) {
        // Show "Downloading..." toast
        Toast.makeText(this,"Downloading..",Toast.LENGTH_SHORT).show()

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(uri))
            .setTitle(fileName)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        try {
            downloadManager.enqueue(request)
            // Show "Downloaded" toast
            Toast.makeText(this,"Downloaded",Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            // Show "Not downloaded" toast in case of failure
            Toast.makeText(this,"Can't Download,Try again",Toast.LENGTH_SHORT).show()
        }
    }
    fun sendNotifications(name:String,message:String,token:String){
        try {
            val queue:RequestQueue= Volley.newRequestQueue(this)
            val url:String="https://fcm.googleapis.com/fcm/send"
            val data:JSONObject=JSONObject()
            data.put("title",name)
            data.put("body",message)
            val notificationData:JSONObject= JSONObject()
            notificationData.put("notification",data)
            notificationData.put("to",token)
            val request = object : JsonObjectRequest(Request.Method.POST, url, notificationData, Response.Listener { _ ->
                // Handle successful response
                Toast.makeText(this@ChatActivity, "success", Toast.LENGTH_SHORT).show();
            }, Response.ErrorListener { error ->
                // Handle error response
                Toast.makeText(this@ChatActivity, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }) {

                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    val key = "Key=$SERVER_KEY"
                    headers["Content-Type"] = "application/json"
                    headers["Authorization"] = key
                    return headers
                }
            }
            queue.add(request)
        }catch (e:Exception){
            //Toast.makeText(this@ChatActivity,"$e",Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("SimpleDateFormat", "RememberReturnType")
    @Composable
    fun Send(message:String,receiverUserId:String,receiverName:String,receiverProfilePhoto: String,receiverUserName:String,senderName:String,senderUserName:String,senderProfilePhoto:String,isPhoto:Boolean,token:String){
        println("Nishant send")
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
        senderSide.child("receiverToken").setValue(token)
        if(isPhoto){
            senderSide.child("lastMessage").setValue("Image")
        }else {
            if(message.length>15)  senderSide.child("lastMessage").setValue(message.substring(0,15))
            else senderSide.child("lastMessage").setValue(message)
        }
        senderSide.child("date").setValue(date)
        senderSide.child("time").setValue(time)
        senderSide.child("received").setValue(false)
        val senderMessage=senderSide.push()
        val messageId = senderMessage.key ?: ""     //finding value of push node
        val receiverSide=dbRef.child(receiverUserId).child("Chats").child(senderUserId)
        receiverSide.child("receiverUserId").setValue(senderUserId)
        if(isPhoto){
            receiverSide.child("lastMessage").setValue("Image")
        }else{
            if(message.length>15){
                receiverSide.child("lastMessage").setValue(message.substring(0,15))
            }else{
                receiverSide.child("lastMessage").setValue(message)
            }

        }
        receiverSide.child("date").setValue(date)
        receiverSide.child("time").setValue(time)
        receiverSide.child("received").setValue(true)
               Toast.makeText(this@ChatActivity,"Name: $receiverName and $senderUserName",Toast.LENGTH_SHORT).show()
        FirebaseMessaging.getInstance().token.addOnSuccessListener{
            val senderToken=it
            Toast.makeText(this@ChatActivity,senderToken,Toast.LENGTH_SHORT).show()
            receiverSide.child("receiverToken").setValue(senderToken)
        }
        //var senderName=senderDetails[0]
        receiverSide.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild("receiverName")) {
                    receiverSide.child("receiverName").setValue(senderName)
                   // senderName=senderDetails[0]
                }
                if(!dataSnapshot.hasChild("receiverUserName")){
                    receiverSide.child("receiverUserName").setValue(senderUserName)
                }
                if(!dataSnapshot.hasChild("receiverProfilePhoto")){
                    receiverSide.child("receiverProfilePhoto").setValue(senderProfilePhoto)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })
        var name by remember {
            mutableStateOf<String>("")
        }
        val db = FirebaseDatabase.getInstance().getReference("User").child(senderUserId)
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child("name").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
        val receiverMessage=receiverSide.child(messageId)
        senderMessage.setValue(Message(messageId,message,isPhoto,received = false,date,time))
            .addOnSuccessListener {
                sendNotifications(name,if(isPhoto) "Sent you an image" else message,token)
                receiverMessage.setValue(Message(messageId,message,isPhoto,received = true,date,time))
            }
    }
}
