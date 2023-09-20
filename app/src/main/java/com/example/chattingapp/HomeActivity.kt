package com.example.chattingapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chattingapp.model.ChatUsers
import com.example.chattingapp.model.MenuItem
import com.example.chattingapp.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : ComponentActivity() {
    lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContent {
            val scaffoldState = rememberScaffoldState()
            val scope = rememberCoroutineScope()
            var selected by remember {
                mutableStateOf(0)
            }
            var name by remember {
                mutableStateOf("")
            }
            var userName by remember {
                mutableStateOf("")
            }
            var profilePhoto by remember {
                mutableStateOf("")
            }
            val userId = Firebase.auth.currentUser?.uid.toString()
            val db = FirebaseDatabase.getInstance().getReference("User").child(userId)
            db.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    name = snapshot.child("name").value.toString()
                    userName = snapshot.child("userName").value.toString()
                    profilePhoto=snapshot.child("profilePhoto").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            })
            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    AppBar(selected = selected,
                        onNavigationIconClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        },
                        onMoreClicked = {}
                    )
                },
                drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                drawerContent = {
                    DrawerHeader(name, userName,profilePhoto)
                    DrawerBody(
                        items = listOf(
                            MenuItem(
                                id = 0,
                                title = "Home",
                                contentDescription = "Go to home screen",
                                icon = Icons.Default.Home
                            ),
                            MenuItem(
                                id = 1,
                                title = "Explore",
                                contentDescription = "Explore",
                                icon = Icons.Default.Search
                            ),
                            MenuItem(
                                id = 2,
                                title = "My Profile",
                                contentDescription = "My Profile",
                                icon = Icons.Default.Person
                            ),
                            MenuItem(
                                id = 3,
                                title = "Settings",
                                contentDescription = "Go to settings screen",
                                icon = Icons.Default.Settings
                            ),
                            MenuItem(
                                id = 4,
                                title = "Help",
                                contentDescription = "Get help",
                                icon = Icons.Default.Info
                            ),
                            MenuItem(
                                id = 5,
                                title = "Log Out",
                                contentDescription = "Log Out",
                                icon = Icons.Default.ExitToApp
                            )
                        ),
                        onItemClick = {
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                            when (it.id) {
                                0 -> selected = 0
                                1 -> selected = 1
                                2 -> selected = 2
                                3 -> selected = 3
                                4 -> selected = 4
                                5 -> selected = 5
                            }
                        },
                        selected = selected
                    )
                }) {
                when (selected) {
                    0 -> Home()
                    1 -> Explore()
                    2 -> ShowProfile()
                    3 -> Settings()
                    4 -> Toast.makeText(this@HomeActivity, "Clicked $selected", Toast.LENGTH_SHORT)
                        .show()
                    5 -> if (!logout()) selected = 0
                }
            }
        }
    }
    @Composable
    fun ShowProfile() {
        var name by remember {
            mutableStateOf("")
        }
        var userName by remember {
            mutableStateOf("")
        }
        var phoneNumber by remember {
            mutableStateOf("")
        }
        var email by remember {
            mutableStateOf("")
        }
        var profilePhoto by remember {
            mutableStateOf("")
        }
        val userId = Firebase.auth.currentUser?.uid.toString()
        val db = FirebaseDatabase.getInstance().getReference("User").child(userId)
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child("name").value.toString()
                userName = snapshot.child("userName").value.toString()
                phoneNumber = snapshot.child("phoneNumber").value.toString()
                email = snapshot.child("email").value.toString()
                profilePhoto=snapshot.child("profilePhoto").value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        profile(name = name, userName = userName, phoneNumber = phoneNumber, email = email, profilePhoto = profilePhoto)
    }
    @Composable
    fun logout(): Boolean {
        var isDialog by remember {
            mutableStateOf(true)
        }
        if (isDialog) {
            AlertDialog(onDismissRequest = { /*TODO*/ },
                title = {
                    Text(
                        text = "Are you sure?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                shape = RoundedCornerShape(15.dp),
                text = {
                    Text(
                        text = "Log out of you account",
                        fontSize = 16.sp
                    )
                },
                buttons = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(bottom = 15.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White
                            ),
                            onClick = { isDialog = false },
                            modifier = Modifier
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                            onClick = {
                                sharedPreferences.edit().clear().apply()
                                Firebase.auth.signOut()
                                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            },
                            modifier = Modifier
                                .shadow(elevation = 2.dp, RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "Log Out",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        } else {
            return false
        }
        return true
    }
    @Composable
    fun Explore(){
        var user by remember {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
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
            val senderDetails = remember {
                mutableStateListOf<User>()
            }
            val allUsers = remember { mutableStateListOf<User>() }
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
                            if (userId != Firebase.auth.currentUser?.uid.toString() && (userName.startsWith(user) || name.startsWith(user))) {
                                allUsers.add(
                                    User(
                                        userId = userId,
                                        userName = userName,
                                        name = name,
                                        email = email,
                                        phoneNumber = phoneNumber ,
                                        profilePhoto = profilePhoto
                                    )
                                )
                            }else{
                                senderDetails.add(User(userId=userId, userName = userName, profilePhoto = profilePhoto,name=name,email= email, phoneNumber = phoneNumber))
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@HomeActivity, error.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                dRef.addValueEventListener(listener)
                // This block will be called when the composable is removed from the UI
                onDispose {
                    dRef.removeEventListener(listener) // Remove the listener to avoid leaks
                }
            }
            val filteredUsers = allUsers.filter { it.name.startsWith(user, ignoreCase = true) || it.userName.startsWith(user,true)}
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())) {
                for(i in filteredUsers){
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(this@HomeActivity, ChatActivity::class.java)
                                intent.putStringArrayListExtra(
                                    "receiverDetails",
                                    arrayListOf(i.userId, i.userName, i.name, i.profilePhoto)
                                )
                                intent.putStringArrayListExtra("senderDetails", arrayListOf(senderDetails[0].name,senderDetails[0].userName,senderDetails[0].profilePhoto))
                                startActivity(intent)
                            }
                            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                        backgroundColor = Color.White,
                        elevation = 5.dp,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row() {
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
                                    .padding(start = 20.dp)
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
                        }
                    }

                }
            }
        }
    }
    @Composable
    fun Settings(){
        var isDialog by remember {
            mutableStateOf(false)
        }
        Column(modifier = Modifier
            .fillMaxSize()
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
            ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(onClick = {},
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 30.dp)
                    .clip(
                        CircleShape
                    ),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.White
                )) {
                androidx.compose.material.Text(text = "Blocked Users", color = Color.Red)
            }
            OutlinedButton(onClick = {},
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 30.dp)
                    .clip(
                        CircleShape
                    ),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.White
                )) {
                androidx.compose.material.Text(text = "Clear All Chats", color = Color.Red)
            }
            OutlinedButton(onClick = {isDialog=true},
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 30.dp)
                    .clip(
                        CircleShape
                    ),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.Red
                )) {
                Text(text = "Delete Account", color = Color.White)
            }
        }
        if (isDialog) {
            AlertDialog(onDismissRequest = { /*TODO*/ },
                title = {
                    Text(
                        text = "Are you sure?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                shape = RoundedCornerShape(15.dp),
                text = {
                    Text(
                        text = "Want to delete account?",
                        fontSize = 16.sp
                    )
                },
                buttons = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(bottom = 15.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White
                            ),
                            onClick = { isDialog = false },
                            modifier = Modifier
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                            onClick = {
                                val user = Firebase.auth.currentUser!!
                                val myRef = FirebaseDatabase.getInstance().getReference("User")
                                myRef.child(user.uid).removeValue()     //removing from database
                                user.delete()           //removing from authentication
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this@HomeActivity,"Account Deleted",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                val storage = FirebaseStorage.getInstance()
                                val storageRef = storage.getReference("profilePhoto/${user.uid}")
                                storageRef.delete()
                                sharedPreferences.edit().clear().apply()
                                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            },
                            modifier = Modifier
                                .shadow(elevation = 2.dp, RoundedCornerShape(15.dp))
                        ) {
                            Text(
                                text = "Delete Account",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    }
@Composable
fun Home(){
    var user by remember {
        mutableStateOf("")
    }
    val senderDetails = remember {
        mutableStateListOf<User>()
    }
    Column(
        modifier = Modifier,
        Arrangement.Center,
        Alignment.CenterHorizontally
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
                            contentDescription = "Clear",
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
        val allUsers = remember { mutableStateListOf<ChatUsers>() }
        DisposableEffect(Unit) {                            //so that one item won't appear multiple times
            val dRef = FirebaseDatabase.getInstance().getReference("User").child(auth.currentUser?.uid!!).child("Chats")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    allUsers.clear() // Clear the list before adding new data
                    for (postSnapshot in snapshot.children) {
                        val userId = postSnapshot.child("receiverUserId").value.toString()
                        val userName = postSnapshot.child("receiverUserName").value.toString()
                        val name = postSnapshot.child("receiverName").value.toString()
                        val profilePhoto=postSnapshot.child("receiverProfilePhoto").value.toString()
                        val lastMessage=postSnapshot.child("lastMessage").value.toString()
                        val date=postSnapshot.child("date").value.toString()
                        val time=postSnapshot.child("time").value.toString()
                        val isReceived=postSnapshot.child("received").value.toString().toBoolean()
                        if (userName.startsWith(user) || name.startsWith(user)) {
                            allUsers.add(
                                ChatUsers(
                                    userId = userId,
                                    userName = userName,
                                    name = name,
                                    profilePhoto = profilePhoto,
                                    isReceived = isReceived,
                                    lastMessage = lastMessage,
                                    date = date,
                                    time = time
                                )
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            dRef.addValueEventListener(listener)
            // This block will be called when the composable is removed from the UI
            onDispose {
                dRef.removeEventListener(listener) // Remove the listener to avoid leaks
            }
        }
        DisposableEffect(Unit) {                            //so that one item won't appear multiple times
            val dRef = FirebaseDatabase.getInstance().getReference("User").child(auth.currentUser?.uid!!)
            val listener = object : ValueEventListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot) {
                    senderDetails.clear() // Clear the list before adding new data
                    for (postSnapshot in snapshot.children) {
                        val userId = postSnapshot.child("userId").value.toString()
                        val userName = postSnapshot.child("userName").value.toString()
                        val name = postSnapshot.child("name").value.toString()
                        val profilePhoto=postSnapshot.child("profilePhoto").value.toString()
                        val email=postSnapshot.child("email").value.toString()
                        val phoneNumber= postSnapshot.child("phoneNumber").value.toString()
                            senderDetails.add(
                                User(
                                    userId = userId,
                                    userName = userName,
                                    name = name,
                                    profilePhoto = profilePhoto,
                                    email = email,
                                    phoneNumber = phoneNumber
                                )
                            )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            dRef.addValueEventListener(listener)
            // This block will be called when the composable is removed from the UI
            onDispose {
                dRef.removeEventListener(listener) // Remove the listener to avoid leaks
            }
        }
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val sortedUsers = allUsers.sortedWith(compareByDescending<ChatUsers> { user ->
            val dateTimeString = "${user.date} ${user.time}:00"
            try {
                val parsedDate = sdf.parse(dateTimeString)
                parsedDate
            } catch (e: ParseException) {
                // Handle the exception or log an error message
                null
            }
        }.thenByDescending { it.time })
        val filteredUsers = sortedUsers.filter { it.name.startsWith(user, ignoreCase = true) || it.userName.startsWith(user,true)}
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f)
            .verticalScroll(rememberScrollState())) {
            for(i in filteredUsers){
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(this@HomeActivity, ChatActivity::class.java)
                            intent.putStringArrayListExtra(
                                "receiverDetails",
                                arrayListOf(i.userId, i.userName, i.name, i.profilePhoto)
                            )
                            intent.putStringArrayListExtra("senderDetails", arrayListOf(senderDetails[0].name,senderDetails[0].userName,senderDetails[0].profilePhoto))
                            startActivity(intent)
                        }
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                    backgroundColor = Color.White,
                    elevation = 5.dp,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row() {
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
                                .padding(start = 20.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Text(
                                text = i.userName,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier
                                    .padding(top = 4.dp),
                                fontFamily = FontFamily.Monospace,
                                fontStyle = FontStyle.Italic
                            )
                            Row(modifier = Modifier
                                .padding(top = 4.dp),){
                                Text(
                                    text =
                                          if(i.isReceived) "${i.name}: "
                                        else "You: ",
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Start,

                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = i.lastMessage,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Start,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier.padding(end=10.dp).fillMaxHeight(1f).align(CenterVertically)
                        ){
                            Text(
                                text = i.date,
                                fontSize = 12.sp,
                                textAlign = TextAlign.End,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(
                                text = i.time,
                                fontSize = 12.sp,
                                textAlign = TextAlign.End,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

            }
        }
    }
}
}