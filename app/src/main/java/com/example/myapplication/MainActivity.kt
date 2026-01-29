package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

// --- Color Palette ---
val DarkBackground = Color(0xFF020610)
val SurfaceDark = Color(0xFF0F1528)
val PrimaryBlue = Color(0xFF2E65F3)
val TextWhite = Color(0xFFFFFFFF)
val TextGrey = Color(0xFF9098A8)
val InputBackground = Color(0xFF0B101E)
val BorderColor = Color(0xFF1F2937)
val DangerRed = Color(0xFFEF4444)
val WarningOrange = Color(0xFFF59E0B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MaterialTheme(
                    colorScheme = darkColorScheme(
                        background = DarkBackground,
                        surface = SurfaceDark,
                        primary = PrimaryBlue,
                        onBackground = TextWhite,
                        onSurface = TextWhite
                    )
                ) {
                    AppContent()
                }
            }
        }
    }
}

// --- Navigation State ---
enum class Screen {
    Onboarding, SignIn, SignUp, Dashboard
}

@Composable
fun AppContent() {
    var currentScreen by remember { mutableStateOf(Screen.Onboarding) }

    // Wrap everything in a Surface to ensure the background is always Dark
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        when (currentScreen) {
            Screen.Onboarding -> OnboardingScreen(
                onGetStarted = { currentScreen = Screen.SignUp },
                onSignIn = { currentScreen = Screen.SignIn }
            )
            Screen.SignIn -> SignInScreen(
                onBack = { currentScreen = Screen.Onboarding },
                onSignUp = { currentScreen = Screen.SignUp },
                onLoginSuccess = { currentScreen = Screen.Dashboard }
            )
            Screen.SignUp -> SignUpScreen(
                onBack = { currentScreen = Screen.Onboarding },
                onSignIn = { currentScreen = Screen.SignIn }
            )
            Screen.Dashboard -> DashboardScreen(
                onSignOut = { currentScreen = Screen.SignIn }
            )
        }
    }
}

// --- Screens: Onboarding (Restored) ---

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit, onSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OrbdynLogo()
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Orbdyn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1528))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "NEW STANDARD",
                color = PrimaryBlue,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        val titleText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = TextWhite)) {
                append("Your Personal ")
            }
            withStyle(style = SpanStyle(color = PrimaryBlue)) {
                append("Resource\nManager")
            }
        }
        Text(
            text = titleText,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            lineHeight = 44.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "A private sanctuary for your bookmarks, notes, and ideas. Organized, accessible, and always secure.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGrey
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Features
        FeatureCard(
            icon = Icons.Default.Layers,
            title = "Smart Categories",
            desc = "Automatically organize your bookmarks and notes into intuitive categories."
        )
        Spacer(modifier = Modifier.height(16.dp))
        FeatureCard(
            icon = Icons.Default.Search,
            title = "Powerful Search",
            desc = "Find anything instantly with our advanced search algorithms."
        )
        Spacer(modifier = Modifier.height(16.dp))
        FeatureCard(
            icon = Icons.Default.Security,
            title = "Private & Secure",
            desc = "Your data is encrypted and accessible only by you."
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(text = "Get Started", onClick = onGetStarted, showArrow = true)

        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ", color = TextGrey)
            Text(
                text = "Sign In",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignIn() }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FeatureCard(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(16.dp))
            .background(Color(0xFF070C18), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF0F1C30), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, color = TextWhite, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, color = TextGrey, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
        }
    }
}

@Composable
fun SignInScreen(onBack: () -> Unit, onSignUp: () -> Unit, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(SurfaceDark, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        OrbdynLogo()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sign in to access your resources and manage your digital life.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email
        InputLabel("Email Address")
        AuthInput(
            value = email,
            onValueChange = { email = it },
            hint = "you@example.com",
            icon = Icons.Outlined.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PASSWORD",
                style = MaterialTheme.typography.labelSmall,
                color = TextGrey,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Forgot Password?",
                color = PrimaryBlue,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { /* Forgot Password */ }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AuthInput(
            value = password,
            onValueChange = { password = it },
            hint = "••••••••",
            icon = Icons.Outlined.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(text = "Sign In", onClick = onLoginSuccess, showArrow = false)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don't have an account? ", color = TextGrey)
            Text(
                text = "Sign up",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignUp() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SignUpScreen(onBack: () -> Unit, onSignIn: () -> Unit) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(SurfaceDark, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        OrbdynLogo()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Display Name
        InputLabel("Display Name")
        AuthInput(
            value = displayName,
            onValueChange = { displayName = it },
            hint = "Your name",
            icon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        InputLabel("Email Address")
        AuthInput(
            value = email,
            onValueChange = { email = it },
            hint = "you@example.com",
            icon = Icons.Outlined.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        InputLabel("Password")
        AuthInput(
            value = password,
            onValueChange = { password = it },
            hint = "••••••••",
            icon = Icons.Outlined.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton(text = "Sign Up", onClick = { /* Handle Sign Up */ }, showArrow = false)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ", color = TextGrey)
            Text(
                text = "Sign In",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignIn() }
            )
        }
    }
}

@Composable
fun AuthInput(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = Color(0xFF4B5563)) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TextGrey) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            focusedBorderColor = BorderColor,
            unfocusedBorderColor = BorderColor,
            cursorColor = PrimaryBlue,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        )
    )
}

// --- Dashboard & Sidebar & Modal (Preserved) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onSignOut: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddResourceModal by remember { mutableStateOf(false) }
    var selectedDashboardPage by remember { mutableStateOf("All Resources") }
    var modalInitialType by remember { mutableStateOf(ResourceType.Link) }

    if (showAddResourceModal) {
        AddResourceModal(
            onDismiss = { showAddResourceModal = false },
            initialType = modalInitialType
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarContent(
                onSignOut = onSignOut,
                selectedItem = selectedDashboardPage,
                onItemSelected = {
                    selectedDashboardPage = it
                    scope.launch { drawerState.close() }
                }
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Scaffold(
            topBar = {
                DashboardTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onAddClick = {
                        modalInitialType = when (selectedDashboardPage) {
                            "Notes" -> ResourceType.Note
                            "To Do" -> ResourceType.Todo
                            else -> ResourceType.Link
                        }
                        showAddResourceModal = true
                    }
                )
            },
            containerColor = DarkBackground
        ) { innerPadding ->
            DashboardContent(
                modifier = Modifier.padding(innerPadding),
                selectedPage = selectedDashboardPage,
                onAddResource = { type ->
                    modalInitialType = type
                    showAddResourceModal = true
                }
            )
        }
    }
}

@Composable
fun SidebarContent(
    onSignOut: () -> Unit,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = DarkBackground,
        drawerContentColor = TextWhite,
        modifier = Modifier.width(300.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrbdynLogo(size = 32.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Orbdyn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Menu Items
        SidebarItem(Icons.Default.GridView, "All Resources", selectedItem == "All Resources") { onItemSelected("All Resources") }
        SidebarItem(Icons.Default.Link, "Links", selectedItem == "Links") { onItemSelected("Links") }
        SidebarItem(Icons.Default.Description, "Notes", selectedItem == "Notes") { onItemSelected("Notes") }
        SidebarItem(Icons.Default.CheckBox, "To Do", selectedItem == "To Do") { onItemSelected("To Do") }
        SidebarItem(Icons.Default.StarBorder, "Favorites", selectedItem == "Favorites") { onItemSelected("Favorites") }
        SidebarItem(Icons.Default.Inventory2, "Archive", selectedItem == "Archive") { onItemSelected("Archive") }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SidebarItem(
            icon = Icons.Default.DeleteOutline,
            label = "Recycle Bin",
            isSelected = selectedItem == "Recycle Bin",
            iconColor = DangerRed,
            textColor = DangerRed,
            onClick = { onItemSelected("Recycle Bin") }
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        // Categories Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("CATEGORIES", style = MaterialTheme.typography.labelSmall, color = TextGrey, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Add, contentDescription = "Add Category", tint = TextGrey, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // User Profile Section
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(SurfaceDark, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).background(Color(0xFF374151), CircleShape), contentAlignment = Alignment.Center) {
                    Text("GC", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Girish Chandwani", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("girish@meensou.com", style = MaterialTheme.typography.bodySmall, color = TextGrey)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                Icon(Icons.Outlined.DarkMode, contentDescription = null, tint = TextGrey)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Dark Mode", color = TextGrey)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSignOut() }) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = DangerRed)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Sign Out", color = DangerRed)
            }
        }
    }
}

@Composable
fun SidebarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    iconColor: Color = TextGrey,
    textColor: Color = TextGrey,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null, tint = if (isSelected) PrimaryBlue else iconColor) },
        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Color(0xFF111827),
            selectedTextColor = PrimaryBlue,
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = textColor
        ),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(onMenuClick: () -> Unit, onAddClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OrbdynLogo(size = 28.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Orbdyn", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(SurfaceDark.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextWhite)
            }
        },
        actions = {
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp).padding(end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontSize = 14.sp)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground,
            titleContentColor = TextWhite,
            actionIconContentColor = TextWhite
        )
    )
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    selectedPage: String,
    onAddResource: (ResourceType) -> Unit
) {
    // Determine content based on selectedPage
    val (title, emptyText, addButtonText, defaultParams) = when (selectedPage) {
        "Links" -> Quadruple("Links", "No links found", "+ Add Link", ResourceType.Link)
        "Notes" -> Quadruple("Notes", "No notes found", "+ Add Note", ResourceType.Note)
        "To Do" -> Quadruple("To Do", "No tasks found", "+ Add Task", ResourceType.Todo)
        "Favorites" -> Quadruple("Favorites", "No favorites yet", "+ Add Resource", ResourceType.Link)
        "Archive" -> Quadruple("Archive", "No archived items", "", ResourceType.Link)
        "Recycle Bin" -> Quadruple("Recycle Bin", "Bin is empty", "", ResourceType.Link)
        else -> Quadruple("All Resources", "No resources yet", "+ Add Resource", ResourceType.Link)
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search $title...", color = Color(0xFF6B7280)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6B7280)) },
            trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF6B7280)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedBorderColor = BorderColor,
                unfocusedBorderColor = BorderColor,
                cursorColor = PrimaryBlue
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("0 items", style = MaterialTheme.typography.bodySmall, color = TextGrey)
        }
        Spacer(modifier = Modifier.height(64.dp))
        Column(modifier = Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(80.dp).background(SurfaceDark, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color(0xFF4B5563))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(emptyText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Start building your journal by adding links, notes, and professional resources.", textAlign = TextAlign.Center, color = TextGrey, modifier = Modifier.padding(horizontal = 32.dp))
            Spacer(modifier = Modifier.height(32.dp))
            
            if (addButtonText.isNotEmpty()) {
                PrimaryButton(text = addButtonText, onClick = { onAddResource(defaultParams) })
            }
        }
    }
}

// Simple data holder for the when expression
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// --- Add Resource Modal (Preserved) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddResourceModal(onDismiss: () -> Unit, initialType: ResourceType = ResourceType.Link) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        contentColor = TextWhite,
        modifier = Modifier.fillMaxHeight(0.95f)
    ) {
        AddResourceContent(onDismiss, initialType)
    }
}

enum class ResourceType { Link, Note, Todo }

@Composable
fun AddResourceContent(onDismiss: () -> Unit, initialType: ResourceType) {
    var selectedType by remember { mutableStateOf(initialType) }
    
    // Simplify slightly for length, assuming user has previous full version or uses this one
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("New Resource", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Capture and organize your vault.", style = MaterialTheme.typography.bodySmall, color = TextGrey)
            }
            IconButton(onClick = onDismiss, modifier = Modifier.background(SurfaceDark, CircleShape).size(32.dp)) {
                Icon(Icons.Default.Close, null, tint = TextGrey, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Reuse previous logic for tabs, inputs etc.
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
           ResourceType.values().forEach { type ->
                val isSelected = selectedType == type
                Button(
                    onClick = { selectedType = type },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) PrimaryBlue else SurfaceDark,
                        contentColor = if (isSelected) TextWhite else TextGrey
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = when(type) {
                            ResourceType.Link -> Icons.Default.Link
                            ResourceType.Note -> Icons.Default.Description
                            ResourceType.Todo -> Icons.Default.Check
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(type.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        if (selectedType == ResourceType.Link) {
            InputLabel("URL / Link")
            CustomInput(value = "", onValueChange = {}, icon = Icons.Default.Link, hint = "https://...")
            Spacer(modifier = Modifier.height(24.dp))
        }

        InputLabel("Title")
        CustomInput(value = "", onValueChange = {}, hint = "What is this about?", trailingIcon = Icons.Default.Mic)
        Spacer(modifier = Modifier.height(24.dp))

        InputLabel("Description (Optional)")
        CustomInput(value = "", onValueChange = {}, hint = "Brief summary...", singleLine = false, modifier = Modifier.height(80.dp))
        Spacer(modifier = Modifier.height(24.dp))
        
        PrimaryButton("Create Resource", onClick = onDismiss)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- Shared Components ---
@Composable
fun OrbdynLogo(size: androidx.compose.ui.unit.Dp = 48.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape((size.value * 0.25).dp))
            .background(Brush.linearGradient(listOf(Color(0xFF2E65F3), Color(0xFF153060)))),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Layers, "Logo", tint = Color.White, modifier = Modifier.size(size * 0.6f))
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    showArrow: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun InputLabel(text: String) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextGrey, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun AssetButton(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = TextWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
             Box(modifier = Modifier.size(32.dp).background(InputBackground, CircleShape), contentAlignment = Alignment.Center) {
                 Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
             }
             Spacer(modifier = Modifier.height(8.dp))
             Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CustomInput(
    value: String, 
    onValueChange: (String) -> Unit, 
    hint: String, 
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = Color(0xFF4B5563)) },
        leadingIcon = if (icon != null) { { Icon(icon, null, tint = TextGrey) } } else null,
        trailingIcon = if (trailingIcon != null) { { Icon(trailingIcon, null, tint = TextGrey) } } else null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            focusedBorderColor = BorderColor,
            unfocusedBorderColor = BorderColor,
            cursorColor = PrimaryBlue,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        )
    )
}