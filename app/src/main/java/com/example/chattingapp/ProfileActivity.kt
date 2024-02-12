package com.example.chattingapp

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chattingapp.model.Message
import com.example.chattingapp.ui.theme.AppColor
import com.example.chattingapp.ui.theme.ChattingAppTheme
import com.example.chattingapp.ui.theme.TopBarColor
import com.example.chattingapp.ui.theme.TopBarColor2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val intent = intent
                    val receiverDetails:ArrayList<String> = intent.getStringArrayListExtra("receiverDetails") as ArrayList<String>
                    val senderDetails:ArrayList<String> = intent.getStringArrayListExtra("senderDetails") as ArrayList<String>
                    Profile(receiverDetails = receiverDetails,senderDetails)
                }
            }
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Profile(receiverDetails:kotlin.collections.ArrayList<String>,senderDetails:kotlin.collections.ArrayList<String>){
        var showPhoto by remember {
            mutableStateOf<String?>(null)
        }
        var selectedTabIndex by remember {
            mutableStateOf(0)
        }
        val tabItems = listOf<String>(
            "All", "Sent", "Received"
        )
        val receiverUserId = receiverDetails[0]
        val receiverUserName=receiverDetails[1]
        val receiverName=receiverDetails[2]
        val receiverProfilePhoto=receiverDetails[3]
        val chat = remember { mutableStateListOf<Message>() }
        val myUserId = Firebase.auth.currentUser?.uid.toString()
        val dbRef= FirebaseDatabase.getInstance().getReference("User").child(myUserId).child("Chats").child(receiverUserId)
        DisposableEffect(Unit){
            val listener=object: ValueEventListener {
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
                            if(isPhoto){
                                chat.add(Message(messageId,message,true,isReceived,date,time))
                            }
                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            dbRef.addValueEventListener(listener)
            // This block will be called when the composable is removed from the UI
            onDispose {
                dbRef.removeEventListener(listener) // Remove the listener to avoid leaks
            }

        }
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)){
            IconButton(onClick = {
                val intent=Intent(this@ProfileActivity,ChatActivity::class.java)
                intent.putStringArrayListExtra("receiverDetails",receiverDetails)
                intent.putStringArrayListExtra("senderDetails",senderDetails)
                startActivity(intent)
            }) {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .padding(top = 30.dp, start = 15.dp)
                        .size(30.dp))
            }
            AsyncImage(model = receiverProfilePhoto,
                contentDescription = "photo",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(75.dp))
                    .border(
                        5.dp,
                        color = Color(22, 139, 179, 37),
                        shape = RoundedCornerShape(75.dp)
                    )
                    .clip(shape = RoundedCornerShape(75.dp))
                    .size(150.dp)
                    .clickable { showPhoto = receiverProfilePhoto },
                contentScale = ContentScale.Crop
            )
            Text(
                text = receiverName,
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = receiverUserName,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                fontFamily = FontFamily.Monospace,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = "Shared Photos",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp, start = 5.dp, end = 5.dp)
                    .border(2.dp, color = Color.White, shape = RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .background(TopBarColor2),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            val pagerState = rememberPagerState(tabItems.size)
            LaunchedEffect(selectedTabIndex ){
                pagerState.animateScrollToPage(selectedTabIndex)
            }
            LaunchedEffect(pagerState.currentPage,pagerState.isScrollInProgress ){
                if(!pagerState.isScrollInProgress){
                    selectedTabIndex=pagerState.currentPage
                }
            }
            TabRow(selectedTabIndex = selectedTabIndex,
            modifier = Modifier.background(Color.White)) {
                tabItems.forEachIndexed { index, item ->
                    Tab(selected = selectedTabIndex==index,
                        modifier = Modifier
                            .border(2.dp, color = Color.White, shape = RoundedCornerShape(9.dp))
                            .background(
                                if (selectedTabIndex == index) AppColor
                                else Color.LightGray
                            ),
                        onClick = { selectedTabIndex=index },
                        text = {
                            Text(
                                text=item,
                                color = if(selectedTabIndex==index) Color.White
                            else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }
            val photos =chat.filter { selectedTabIndex==0 || (selectedTabIndex==1 && !it.received) || (selectedTabIndex==2 && it.received)}
            HorizontalPager(
                state=pagerState,
                modifier= Modifier
                    .fillMaxWidth()
                    .weight(1f),
                pageCount = tabItems.size
            ) { index->
                Column(modifier= Modifier
                    .fillMaxWidth()
                    .weight(1f)) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(100.dp),
                        content ={
                            items(photos){
                                AsyncImage(
                                    model = it.message,
                                    contentDescription = "photo",
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .border(
                                            5.dp,
                                            color = TopBarColor,
                                            //shape = RoundedCornerShape(75.dp)
                                        )
                                        .shadow(
                                            elevation = 10.dp,
                                            //  shape = RoundedCornerShape(75.dp)
                                        )
                                        .width(100.dp)
                                        .height(130.dp)

                                        .clickable { showPhoto = it.message },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    )
                }

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

}

