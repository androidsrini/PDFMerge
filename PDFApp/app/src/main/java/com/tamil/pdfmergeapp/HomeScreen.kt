package com.tamil.pdfmergeapp

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


val column1Weight = .1f // 10%
val column2Weight = .7f // 70%
val column3Weight = .2f // 20%

@Composable
fun homeScreen(fileNames: SnapshotStateList<Uri>, paddingValue: PaddingValues,
               deleteOnClick: (Int) -> Unit) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(paddingValue),
        contentPadding = PaddingValues(16.dp)
    ) {
        /*item {
            Row(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Gray),) {
                TableCell(text = "#", column1Weight)
                TableCell(text = "Name", column2Weight)
                TableCell(text = "Action", column3Weight)
            }
        }*/
        itemsIndexed(fileNames) {index, names->
            itemsDisplay(index = index, name = names, deleteOnClick )
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float) {
    Text(text = text, modifier = Modifier
        .weight(weight)
        .padding(8.dp))
}

@Composable
fun RowScope.TableCellImage(index: Int, weight: Float, deleteOnClick: (Int) -> Unit) {
    IconButton(onClick = {
        deleteOnClick.invoke(index) }, modifier = Modifier
        .weight(weight)
        .padding(5.dp)) {
        Icon(imageVector = Icons.Filled.Delete, contentDescription ="Delete Item")
    }
}

@Composable
fun itemsDisplay(index: Int, name: Uri, deleteOnClick: (Int) -> Unit) {
    Card(modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()
        .wrapContentHeight(),
        shape = RoundedCornerShape(2.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TableCell(text = index.toString(), column1Weight)
            name.path?.let{name.getName(LocalContext.current)}?.let { TableCell(text = it, column2Weight) }
            TableCellImage(index, weight = column3Weight, deleteOnClick)
        }
    }
}

@Preview
@Composable
fun display() {
    val DUMMY_LIST = remember {
       mutableStateListOf<String>("Test1.pdf", "Test2.pdf", "Test3.pdf")
    }//arrayListOf("Test1.pdf", "Test2.pdf", "Test3.pdf")
    //homeScreen(fileNames = DUMMY_LIST)
}