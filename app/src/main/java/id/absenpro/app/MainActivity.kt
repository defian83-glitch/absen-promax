package id.absenpro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AbsenProApp() }
    }
}

data class Employee(val name: String, val division: String)
data class Record(val name: String, val division: String, val date: String,
                  val status: String, val timeIn: String, val timeOut: String)

private val statuses = listOf("Hadir", "Terlambat", "Izin", "Sakit", "Alpa", "Cuti", "Libur")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenProApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("absen_pro", 0) }
    var employees by remember { mutableStateOf(loadEmployees(prefs)) }
    var records by remember { mutableStateOf(loadRecords(prefs)) }
    var name by remember { mutableStateOf("") }
    var division by remember { mutableStateOf("") }
    var selectedEmployee by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Hadir") }
    var timeIn by remember { mutableStateOf("08:00") }
    var timeOut by remember { mutableStateOf("17:00") }
    var tab by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("") }

    fun saveEmployees() {
        val arr = JSONArray()
        employees.forEach { arr.put(JSONObject().put("name", it.name).put("division", it.division)) }
        prefs.edit().putString("employees", arr.toString()).apply()
    }
    fun saveRecords() {
        val arr = JSONArray()
        records.forEach {
            arr.put(JSONObject().put("name", it.name).put("division", it.division)
                .put("date", it.date).put("status", it.status)
                .put("timeIn", it.timeIn).put("timeOut", it.timeOut))
        }
        prefs.edit().putString("records", arr.toString()).apply()
    }

    MaterialTheme {
        Scaffold(topBar = {
            TopAppBar(title = { Text("ABSEN PRO", fontWeight = FontWeight.Bold) })
        }) { padding ->
            Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = tab == 0, onClick = { tab = 0 }, label = { Text("Absensi") })
                    FilterChip(selected = tab == 1, onClick = { tab = 1 }, label = { Text("Pegawai") })
                    FilterChip(selected = tab == 2, onClick = { tab = 2 }, label = { Text("Rekap") })
                }
                Spacer(Modifier.height(16.dp))
                when (tab) {
                    0 -> {
                        Text("Jam kerja standar: 08.00–17.00", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(12.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            OutlinedTextField(value = selectedEmployee, onValueChange = {},
                                readOnly = true, label = { Text("Pilih pegawai") },
                                modifier = Modifier.menuAnchor().fillMaxWidth())
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                employees.forEach { e ->
                                    DropdownMenuItem(text = { Text("${e.name} • ${e.division}") },
                                        onClick = { selectedEmployee = e.name; expanded = false })
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        var statusExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                            OutlinedTextField(value = status, onValueChange = {}, readOnly = true,
                                label = { Text("Status") }, modifier = Modifier.menuAnchor().fillMaxWidth())
                            ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                                statuses.forEach { s -> DropdownMenuItem(text = { Text(s) },
                                    onClick = { status = s; statusExpanded = false }) }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(timeIn, { timeIn = it }, label = { Text("Jam masuk (HH:mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(timeOut, { timeOut = it }, label = { Text("Jam pulang (HH:mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            val emp = employees.firstOrNull { it.name == selectedEmployee }
                            if (emp == null) message = "Pilih pegawai terlebih dahulu."
                            else {
                                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                records = records.filterNot { it.name == emp.name && it.date == today } +
                                    Record(emp.name, emp.division, today, status, timeIn, timeOut)
                                saveRecords()
                                message = "Absensi ${emp.name} tersimpan."
                            }
                        }, modifier = Modifier.fillMaxWidth()) { Text("SIMPAN ABSENSI") }
                        if (message.isNotBlank()) Text(message, color = MaterialTheme.colorScheme.primary)
                    }
                    1 -> {
                        OutlinedTextField(name, { name = it }, label = { Text("Nama pegawai") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(division, { division = it }, label = { Text("Divisi") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            if (name.isBlank() || division.isBlank()) message = "Nama dan divisi wajib diisi."
                            else if (employees.size >= 80) message = "Maksimal 80 pegawai."
                            else {
                                employees = employees + Employee(name.trim(), division.trim())
                                saveEmployees()
                                selectedEmployee = name.trim()
                                name = ""; division = ""; message = "Pegawai berhasil ditambahkan."
                            }
                        }, modifier = Modifier.fillMaxWidth()) { Text("TAMBAH PEGAWAI") }
                        Text("Daftar pegawai (${employees.size}/80)", fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp))
                        LazyColumn {
                            items(employees) { e ->
                                ListItem(headlineContent = { Text(e.name) }, supportingContent = { Text(e.division) })
                            }
                        }
                    }
                    else -> {
                        val counts = statuses.associateWith { s -> records.count { it.status == s } }
                        Text("Rekap absensi tersimpan", fontWeight = FontWeight.Bold)
                        Text("Total catatan: ${records.size}")
                        statuses.forEach { s -> ListItem(headlineContent = { Text(s) },
                            trailingContent = { Text("${counts[s] ?: 0}", fontWeight = FontWeight.Bold) }) }
                        Divider()
                        Text("Riwayat terbaru", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                        LazyColumn {
                            items(records.sortedByDescending { it.date }.take(30)) { r ->
                                ListItem(headlineContent = { Text("${r.name} — ${r.status}") },
                                    supportingContent = { Text("${r.date} • ${r.timeIn}–${r.timeOut} • ${r.division}") })
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun loadEmployees(prefs: android.content.SharedPreferences): List<Employee> = try {
    val arr = JSONArray(prefs.getString("employees", "[]"))
    (0 until arr.length()).map { val o = arr.getJSONObject(it); Employee(o.getString("name"), o.getString("division")) }
} catch (_: Exception) { emptyList() }

private fun loadRecords(prefs: android.content.SharedPreferences): List<Record> = try {
    val arr = JSONArray(prefs.getString("records", "[]"))
    (0 until arr.length()).map {
        val o = arr.getJSONObject(it)
        Record(o.getString("name"), o.getString("division"), o.getString("date"),
            o.getString("status"), o.getString("timeIn"), o.getString("timeOut"))
    }
} catch (_: Exception) { emptyList() }
