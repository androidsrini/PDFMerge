package com.tamil.pdfmergeapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamil.pdfmergeapp.ui.theme.PDFWritterAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

private const val TAG = "MainActivity"

enum class MenuSelection {
    ADD_ITEM,
    SETTINGS,
    ABOUT,
    NONE
}

enum class ShowNotification {
    Dialog,
    Snackbar,
    Toast,
    NONE
}


class MainActivity : ComponentActivity() {

    val finishActivity = {finish()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PDFWritterAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MediumTopAppBarExample(onCloseClick = finishActivity)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun ActionBtn(btnName: String, click:()->Unit) {
    Row {
        Button(onClick = click, enabled = true) {
            Text(text = btnName)
        }
        Spacer(modifier = Modifier.width(5.dp))
        Button(onClick = click, enabled = true) {
            Text(text = btnName)
        }
    }
}

@Composable
fun MainMenu(menuSelection: MutableState<MenuSelection>,
             expandedMain: MutableState<Boolean>,
             addClick: () -> Unit, settingClick:() -> Unit,
             aboutClick:() -> Unit) {
    DropdownMenu(expanded = expandedMain.value,
        onDismissRequest = { expandedMain.value = false }) {
        DropdownMenuItem(text = { Text(text = "Add Item") },
            onClick = {
                expandedMain.value = false
                menuSelection.value = MenuSelection.ADD_ITEM
                addClick.invoke()
            })
        Divider()
        DropdownMenuItem(text = { Text(text = "Setting") },
            onClick = {
                expandedMain.value = false
                menuSelection.value = MenuSelection.SETTINGS
                settingClick()
            })
        Divider()
        DropdownMenuItem(text = { Text(text = "About") },
            onClick = {
                expandedMain.value = false
                menuSelection.value = MenuSelection.ABOUT
                aboutClick()
            })
    }
}

@Composable
fun TopAppBarDropdownMenu(menuSelection: MutableState<MenuSelection>,
                          addClick: () -> Unit, settingClick: () -> Unit,
                          aboutClick: () -> Unit) {
    val expandedMain = remember { mutableStateOf(false) }

    Box(Modifier.wrapContentSize(Alignment.TopEnd)) {
       IconButton(onClick = {
           expandedMain.value = true
       }) {
           Icon(imageVector = Icons.Filled.MoreVert, contentDescription ="More menu")
       } 
    }
    MainMenu(menuSelection = menuSelection, expandedMain = expandedMain, addClick,
        settingClick, aboutClick)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopActionBar(title: String, icon: ImageVector, onCloseClick: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = { Text(text = title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onCloseClick.invoke()
                    }) {
                        Icon(imageVector = icon, contentDescription ="Exit app")
                    }
                },
                scrollBehavior = scrollBehavior)
        }){
        it.calculateTopPadding()
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()) {
            Text(text = stringResource(id = R.string.under_construction_msg))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediumTopAppBarExample(onCloseClick: () -> Unit) {
    val context = LocalContext.current
    val menuSelection = remember { mutableStateOf(MenuSelection.NONE) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var files = remember {
        mutableStateListOf<Uri>()
    }
    val pickPDFFile = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { imageUri ->
        imageUri.let {
            it.map {
                if (!files.contains(it)) {
                    files.add(it)
                } else {
                    Toast.makeText(context, "Duplicate ${it.getName(context)}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val scope = rememberCoroutineScope() // Create a coroutine scope
    val scaffoldState = rememberBottomSheetScaffoldState()
    var currentProgress by remember { mutableStateOf(0f) }
    var loadingProgress by remember {
        mutableStateOf(false)
    }
    var showSuccessDialog by remember {
        mutableIntStateOf(ShowNotification.NONE.ordinal)
    }
    val snackBarHostState = remember { SnackbarHostState() }
    var fileName: String? = null

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            MediumTopAppBar(title = { Text(text = "PDF Merge") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onCloseClick.invoke()
                    }) {
                        Icon(imageVector =Icons.Filled.Close, contentDescription ="Exit app")
                    }
                },
                actions = {
                    TopAppBarDropdownMenu(menuSelection = menuSelection, addClick =  {
                        // Add menu click
                        pickPDFFile.launch("application/pdf")
                    }, settingClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }, aboutClick = {
                        context.startActivity(Intent(context, AboutActivity::class.java))
                    })
                },
                scrollBehavior = scrollBehavior)
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.Transparent,
                actions = {}, floatingActionButton = {
                Row() {
                    if (loadingProgress) {
                        Column {
                            LinearProgressIndicator(
                                progress = currentProgress/100,
                                modifier = Modifier.fillMaxWidth()
                            )
                            //19.23077
                            Text(text = "Merging...${DecimalFormat("#.##").format(currentProgress)}%")
                        }
                    } else {
                        if (files.size > 1) {
                            //val width = context.resources.getDisplayMetrics().widthPixels / 10
                                FloatingActionButton(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .offset(x = -100.dp),
                                    onClick = {
                                        Toast.makeText(context, "Merge started", Toast.LENGTH_SHORT)
                                            .show()
                                        // Merge button click
                                        loadingProgress = true
                                        scope.launch {
                                            fileName = getFileName()
                                            val outputPath = "${getOutputPath()}/${fileName}"
                                            mergePDFFile(context, files.toList(), outputPath) {
                                                currentProgress = it
                                                Log.d(TAG, "currentProgress:${currentProgress}")
                                            }
                                            Log.d(TAG, "mergePDFFile done ")
                                            loadingProgress = false
                                            files.clear()
                                            showSuccessDialog = ShowNotification.Dialog.ordinal
                                        }
                                    },
                                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(5.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_insert_drive_file_24),
                                            contentDescription = "Merge files"
                                        )
                                        Text(text = "Merge")
                                    }
                                }
                        }
                        FloatingActionButton(
                            modifier = Modifier.padding(5.dp),
                            onClick = {
                                pickPDFFile.launch("application/pdf")
                            },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center) {
                                Icon(Icons.Filled.Add, "Add File")
                                Text(text = "Add")
                            }
                        }
                    }
                } })
        }) {
        //innerPadding.calculateTopPadding()
        if (files.size > 0) {
            homeScreen(fileNames = files, it) { deleteItemIndex ->
                files.removeAt(deleteItemIndex)
            }
        } else {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()) {
                Text(text = "No files selected")
            }
        }
        if (showSuccessDialog == ShowNotification.Dialog.ordinal) {
            showSuccessDialog(filename = fileName ?: "") {
                showSuccessDialog = ShowNotification.Snackbar.ordinal
                scope.launch {
                    showSnackbar(fileName?:"", snackBarHostState){
                        openDocument(context, fileName?:"")
                    }
                }
            }
        }
        //ScrollCon

    }
}

suspend fun showSnackbar(msg: String, snackBarHostState: SnackbarHostState,
                         onClick:()->Unit) {
    val snackbarResult = snackBarHostState.showSnackbar(msg, actionLabel = "Open",
        duration = SnackbarDuration.Long)
    when (snackbarResult) {
        SnackbarResult.Dismissed -> {
            Log.d(TAG, "Dismissed snackbar")
        }
        SnackbarResult.ActionPerformed -> {
            //openDocument(context)
            onClick.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showSuccessDialog(filename: String, onOkClick:()->Unit) {
    AlertDialog(
        icon = {
            Icon(painterResource(id = R.drawable.baseline_check_circle_24),
                contentDescription = "Success", tint = Color.Green,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp))
        },
        title = {
            Text(text = "Merged")
        },
        text = {
            Text(text = filename)
        },
        onDismissRequest = {
            //onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = onOkClick
            ) {
                Text("OK")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PDFWritterAppTheme {
        MediumTopAppBarExample(){

        }
    }
}

fun Uri.getName(context: Context): String? {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    cursor?.moveToFirst()
    val index = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: -1
    return if (index > 0) {cursor?.getString(index)} else null
}

suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(100)
    }
}

/**
 * This method will return internal storage Document path
 */
fun getOutputPath(): String {
    val dir = "${Environment.getExternalStorageDirectory()}/Documents${File.separator}PDFMergeApp"
    val outFile = File(dir)
    if (!outFile.exists()) {
        outFile.mkdir()
    }
    //val sharedPref = context.getSharedPreferences(context.getString(R.string.preference),Context.MODE_PRIVATE) ?: return
    return dir//sharedPref.getString(context.getString(R.string.output_path), null)
}

fun getFileName(): String {
    val dateTime = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    return "Merge_$dateTime.pdf"
}

fun openDocument(context: Context, filename: String) {
    val uri = Uri.parse(getOutputPath() + "/" + filename)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
    }
    context.startActivity(intent)
}

suspend fun mergePDFFile(
    context: Context, pdfFile: List<Uri>, outputName: String,
    updateProgress: (Float) -> Unit) {
    val pdfDocument = PdfDocument()

    //Load exiting pdf file
    var totalFileToMerge = pdfFile.size
    var progress = 0f
    pdfFile.mapIndexed {index, uri ->
        var pdfReader: PdfRenderer? = null
        var page: PdfDocument.Page? = null
        var canvas: Canvas? = null

        try {
            pdfReader = context.contentResolver.openFileDescriptor(uri, "r")
                ?.let { PdfRenderer(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(context, "Can't open protected ${uri.getName(context)}", Toast.LENGTH_SHORT).show()
        }

        pdfReader?.let {
            //Render each page of the existing PDF file onto the canvas
            Log.d(TAG, "pageCount:${it.pageCount}")
            val totalPageCount = it.pageCount -1
            for (i in 0..totalPageCount) {
                Log.d(TAG, "index:${i}")
                val pdfPage = it.openPage(i)
                //Log.d(TAG, "pdf page width:${pdfPage.width} height:${pdfPage.height}")
                //Create pdf page based on original pdf result
                val pageInfo = PdfDocument.PageInfo.Builder(pdfPage.width, pdfPage.height, 1).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page?.canvas
                //context.resources.getDisplayMetrics().densityDpi * pdfPage.width / 72
                val bitmap = Bitmap.createBitmap(pdfPage.width, pdfPage.height, Bitmap.Config.ARGB_8888)
                pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                canvas?.drawBitmap(bitmap, 0f, 0f, null)
                pdfPage.close()
                pdfDocument.finishPage(page)
                // Update progress
                //page count = 52
                // index = 0
                progress = ((i.toFloat() / it.pageCount.toFloat()) * 100)
                Log.d(TAG, "progress:${progress}")
                updateProgress(progress)
                delay(10)
            }
        }
    }

    //Save the document
    Log.d(TAG, "outputName:${outputName}")
    val file = File(outputName)
    try {
        withContext(Dispatchers.IO) {
            pdfDocument.writeTo(FileOutputStream(file))
        }
        Toast.makeText(context, "PDF merged successfully", Toast.LENGTH_LONG).show()
        updateProgress(100f)
        Log.d(TAG, "Merge success to:${outputName}")
    } catch (e: IOException){
        e.printStackTrace()
        Toast.makeText(context, "Error: " + e.message, Toast.LENGTH_LONG).show();
    }

    pdfDocument.close()
}