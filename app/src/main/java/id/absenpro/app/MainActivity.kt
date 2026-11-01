package id.absenpro.app

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { AbsenProApp() } }
}

data class Employee(
    val name: String, val division: String,
    val payType: String = "Bulanan", val monthlySalary: Long = 0,
    val dailyRate: Long = 0, val lateDeductionPerMinute: Long = 0,
    val absentDeductionPerDay: Long = 0, val monthlyIncentive: Long = 0,
    val overtimeRatePerHour: Long = 0
)
data class Record(val name: String, val division: String, val date: String,
                  val status: String, val timeIn: String, val timeOut: String)

private val statuses = listOf("Hadir", "Terlambat", "Izin", "Sakit", "Alpa", "Cuti", "Libur")
private const val MEAL_ALLOWANCE = 10000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenProApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("absen_pro", 0) }
    var employees by remember { mutableStateOf(loadEmployees(prefs)) }
    var records by remember { mutableStateOf(loadRecords(prefs)) }
    var name by remember { mutableStateOf("") }
    var division by remember { mutableStateOf("") }
    var payType by remember { mutableStateOf("Bulanan") }
    var monthlySalary by remember { mutableStateOf("0") }
    var dailyRate by remember { mutableStateOf("0") }
    var lateRate by remember { mutableStateOf("0") }
    var absentRate by remember { mutableStateOf("0") }
    var incentive by remember { mutableStateOf("0") }
    var overtimeRate by remember { mutableStateOf("0") }
    var selectedEmployee by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Hadir") }
    var timeIn by remember { mutableStateOf("08:00") }
    var timeOut by remember { mutableStateOf("17:00") }
    var tab by remember { mutableIntStateOf(0) }
    var appMode by remember { mutableStateOf("Admin") }
    var message by remember { mutableStateOf("") }

    fun saveEmployees() {
        val arr = JSONArray()
        employees.forEach { e -> arr.put(JSONObject().put("name", e.name).put("division", e.division)
            .put("payType", e.payType).put("monthlySalary", e.monthlySalary).put("dailyRate", e.dailyRate)
            .put("lateDeductionPerMinute", e.lateDeductionPerMinute).put("absentDeductionPerDay", e.absentDeductionPerDay)
            .put("monthlyIncentive", e.monthlyIncentive).put("overtimeRatePerHour", e.overtimeRatePerHour)) }
        prefs.edit().putString("employees", arr.toString()).apply()
    }
    fun saveRecords() {
        val arr = JSONArray()
        records.forEach { arr.put(JSONObject().put("name", it.name).put("division", it.division).put("date", it.date)
            .put("status", it.status).put("timeIn", it.timeIn).put("timeOut", it.timeOut)) }
        prefs.edit().putString("records", arr.toString()).apply()
    }
    val rupiah = remember { NumberFormat.getCurrencyInstance(Locale("in", "ID")) }
    val month = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    val blueScheme = lightColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF1769D2),
        onPrimary = androidx.compose.ui.graphics.Color.White,
        secondary = androidx.compose.ui.graphics.Color(0xFF4D83C8),
        background = androidx.compose.ui.graphics.Color(0xFFF5F7FB),
        surface = androidx.compose.ui.graphics.Color.White
    )
    MaterialTheme(colorScheme = blueScheme) {
        Scaffold(topBar = { TopAppBar(title = { Text("ABSEN PRO MAX V3", fontWeight = FontWeight.Bold) }) }) { padding ->
            Column(Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = appMode == "Admin", onClick = { appMode = "Admin" }, label = { Text("Mode Admin") })
                    FilterChip(selected = appMode == "Kiosk", onClick = { appMode = "Kiosk" }, label = { Text("Mode Kiosk") })
                }
                Spacer(Modifier.height(10.dp))
                if (appMode == "Kiosk") {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("ABSENSI WAJAH", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Mode Kiosk • Perangkat absensi kantor", color = MaterialTheme.colorScheme.primary)
                            Text("Tahap V3.1: kerangka mode kiosk sudah tersedia. Integrasi kamera dan deteksi wajah lintas perangkat menjadi tahap berikutnya.")
                            Text("Data pegawai dan riwayat V2 tetap tersimpan di penyimpanan aplikasi.", style = MaterialTheme.typography.bodyMedium)
                            Text("KAMERA KIOSK", fontWeight = FontWeight.Bold)
                            CameraPreview(modifier = Modifier.fillMaxWidth().height(300.dp))
                            Text("Catatan: tahap ini menampilkan kamera depan. Verifikasi identitas wajah belum diaktifkan.")
                            Text("PENTING: jangan gunakan pengenalan wajah untuk mengambil keputusan sebelum proses pendaftaran wajah, uji akurasi, dan perlindungan data pegawai selesai.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { appMode = "Admin" }, modifier = Modifier.fillMaxWidth()) { Text("KEMBALI KE ADMIN") }
                        }
                    }
                } else {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = tab == 0, onClick = { tab = 0 }, label = { Text("Absensi") })
                    FilterChip(selected = tab == 1, onClick = { tab = 1 }, label = { Text("Pegawai") })
                    FilterChip(selected = tab == 2, onClick = { tab = 2 }, label = { Text("Rekap") })
                    FilterChip(selected = tab == 3, onClick = { tab = 3 }, label = { Text("Gaji") })
                }
                Spacer(Modifier.height(12.dp))
                when (tab) {
                    0 -> {
                        Text("Jam kerja standar: 08.00–17.00")
                        Spacer(Modifier.height(10.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            OutlinedTextField(value = selectedEmployee, onValueChange = {}, readOnly = true, label = { Text("Pilih pegawai") }, modifier = Modifier.menuAnchor().fillMaxWidth())
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                employees.forEach { e -> DropdownMenuItem(text = { Text("${e.name} • ${e.division}") }, onClick = { selectedEmployee = e.name; expanded = false }) }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        var statusExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                            OutlinedTextField(status, {}, readOnly = true, label = { Text("Status") }, modifier = Modifier.menuAnchor().fillMaxWidth())
                            ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                                statuses.forEach { s -> DropdownMenuItem(text = { Text(s) }, onClick = { status = s; statusExpanded = false }) }
                            }
                        }
                        OutlinedTextField(timeIn, { timeIn = it }, label = { Text("Jam masuk (HH:mm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(timeOut, { timeOut = it }, label = { Text("Jam pulang (HH:mm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii), modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = {
                            val emp = employees.firstOrNull { it.name == selectedEmployee }
                            if (emp == null) message = "Pilih pegawai terlebih dahulu."
                            else if (!validTime(timeIn) || !validTime(timeOut)) message = "Format jam harus HH:mm, contoh 08:00."
                            else {
                                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                records = records.filterNot { it.name == emp.name && it.date == today } + Record(emp.name, emp.division, today, status, timeIn, timeOut)
                                saveRecords(); message = "Absensi ${emp.name} tersimpan."
                            }
                        }, modifier = Modifier.fillMaxWidth()) { Text("SIMPAN ABSENSI") }
                        if (message.isNotBlank()) Text(message, color = MaterialTheme.colorScheme.primary)
                    }
                    1 -> {
                        LazyColumn {
                            item {
                                OutlinedTextField(name, { name = it }, label = { Text("Nama pegawai") }, modifier = Modifier.fillMaxWidth())
                                OutlinedTextField(division, { division = it }, label = { Text("Divisi") }, modifier = Modifier.fillMaxWidth())
                                Text("Skema gaji", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                    listOf("Bulanan", "Harian", "Kombinasi").forEach { p -> FilterChip(selected = payType == p, onClick = { payType = p }, label = { Text(p) }) }
                                }
                                if (payType != "Harian") MoneyField("Gaji pokok bulanan (Rp)", monthlySalary) { monthlySalary = it }
                                if (payType != "Bulanan") MoneyField("Upah harian (Rp)", dailyRate) { dailyRate = it }
                                MoneyField("Potongan per menit terlambat (Rp)", lateRate) { lateRate = it }
                                MoneyField("Potongan per hari alpa (Rp)", absentRate) { absentRate = it }
                                MoneyField("Insentif bulanan (Rp)", incentive) { incentive = it }
                                MoneyField("Tarif lembur per jam (Rp)", overtimeRate) { overtimeRate = it }
                                Button(onClick = {
                                    if (name.isBlank() || division.isBlank()) message = "Nama dan divisi wajib diisi."
                                    else if (employees.size >= 80) message = "Maksimal 80 pegawai."
                                    else if (employees.any { it.name.equals(name.trim(), true) }) message = "Nama pegawai sudah terdaftar."
                                    else {
                                        employees = employees + Employee(name.trim(), division.trim(), payType, monthlySalary.toLongOrNull() ?: 0,
                                            dailyRate.toLongOrNull() ?: 0, lateRate.toLongOrNull() ?: 0, absentRate.toLongOrNull() ?: 0,
                                            incentive.toLongOrNull() ?: 0, overtimeRate.toLongOrNull() ?: 0)
                                        saveEmployees(); selectedEmployee = name.trim(); name = ""; division = ""; message = "Pegawai berhasil ditambahkan."
                                    }
                                }, modifier = Modifier.fillMaxWidth()) { Text("TAMBAH PEGAWAI") }
                                Text(message, color = MaterialTheme.colorScheme.primary)
                                Text("Daftar pegawai (${employees.size}/80)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                            }
                            items(employees) { e -> ListItem(headlineContent = { Text(e.name) }, supportingContent = { Text("${e.division} • ${e.payType} • ${rupiah.format(if (e.payType == "Harian") e.dailyRate else e.monthlySalary)}") }) }
                        }
                    }
                    2 -> {
                        val counts = statuses.associateWith { s -> records.count { it.status == s } }
                        Text("Rekap absensi tersimpan", fontWeight = FontWeight.Bold)
                        Text("Total catatan: ${records.size}")
                        statuses.forEach { s -> ListItem(headlineContent = { Text(s) }, trailingContent = { Text("${counts[s] ?: 0}", fontWeight = FontWeight.Bold) }) }
                        Divider()
                        Text("Riwayat terbaru", fontWeight = FontWeight.Bold)
                        LazyColumn { items(records.sortedByDescending { it.date }.take(30)) { r -> ListItem(headlineContent = { Text("${r.name} — ${r.status}") }, supportingContent = { Text("${r.date} • ${r.timeIn}–${r.timeOut} • ${r.division}") }) } }
                    }
                    else -> {
                        Text("ESTIMASI GAJI • $month", fontWeight = FontWeight.Bold)
                        Text("Uang makan: Rp10.000 per hari hadir", color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        LazyColumn {
                            items(employees) { e ->
                                val monthlyRecords = records.filter { it.name == e.name && it.date.startsWith(month) }
                                val present = monthlyRecords.count { it.status == "Hadir" || it.status == "Terlambat" }
                                val absent = monthlyRecords.count { it.status == "Alpa" }
                                val lateMinutes = monthlyRecords.filter { it.status == "Terlambat" }.sumOf { minutesLate(it.timeIn) }
                                val overtimeHours = monthlyRecords.sumOf { overtimeMinutes(it.timeOut) } / 60.0
                                val base = when (e.payType) { "Harian" -> e.dailyRate * present; "Kombinasi" -> e.monthlySalary + e.dailyRate * present; else -> e.monthlySalary }
                                val meal = present * MEAL_ALLOWANCE
                                val deductions = lateMinutes * e.lateDeductionPerMinute + absent * e.absentDeductionPerDay
                                val overtime = (overtimeHours * e.overtimeRatePerHour).toLong()
                                val total = (base + meal + e.monthlyIncentive + overtime - deductions).coerceAtLeast(0)
                                Card(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(e.name, fontWeight = FontWeight.Bold)
                                        Text("${e.division} • ${e.payType} • Hadir: $present hari")
                                        Text("Gaji pokok: ${rupiah.format(base)}")
                                        Text("Uang makan: ${rupiah.format(meal)}")
                                        Text("Lembur: ${rupiah.format(overtime)} • Insentif: ${rupiah.format(e.monthlyIncentive)}")
                                        Text("Potongan: ${rupiah.format(deductions)}")
                                        Divider(Modifier.padding(vertical = 5.dp))
                                        Text("ESTIMASI DITERIMA: ${rupiah.format(total)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }
        }
    }
}


@Composable
private fun CameraPreview(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasPermission) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("AbsenProCamera", "Gagal membuka kamera depan", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Izin kamera diperlukan. Aktifkan izin kamera untuk Mode Kiosk.")
        }
    }
}

@Composable
private fun MoneyField(label: String, value: String, onValue: (String) -> Unit) {
    OutlinedTextField(value, { v -> onValue(v.filter { it.isDigit() }) }, label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
}
private fun validTime(s: String): Boolean = try { val p = s.split(":"); p.size == 2 && p[0].toInt() in 0..23 && p[1].toInt() in 0..59 } catch (_: Exception) { false }
private fun minutesLate(s: String): Long = try { val p = s.split(":"); ((p[0].toInt() * 60 + p[1].toInt()) - 480).coerceAtLeast(0).toLong() } catch (_: Exception) { 0 }
private fun overtimeMinutes(s: String): Int = try { val p = s.split(":"); ((p[0].toInt() * 60 + p[1].toInt()) - 1020).coerceAtLeast(0) } catch (_: Exception) { 0 }

private fun loadEmployees(prefs: android.content.SharedPreferences): List<Employee> = try {
    val arr = JSONArray(prefs.getString("employees", "[]"))
    (0 until arr.length()).map { val o = arr.getJSONObject(it); Employee(o.getString("name"), o.getString("division"),
        o.optString("payType", "Bulanan"), o.optLong("monthlySalary", 0), o.optLong("dailyRate", 0),
        o.optLong("lateDeductionPerMinute", 0), o.optLong("absentDeductionPerDay", 0),
        o.optLong("monthlyIncentive", 0), o.optLong("overtimeRatePerHour", 0)) }
} catch (_: Exception) { emptyList() }
private fun loadRecords(prefs: android.content.SharedPreferences): List<Record> = try {
    val arr = JSONArray(prefs.getString("records", "[]"))
    (0 until arr.length()).map { val o = arr.getJSONObject(it); Record(o.getString("name"), o.getString("division"), o.getString("date"), o.getString("status"), o.getString("timeIn"), o.getString("timeOut")) }
} catch (_: Exception) { emptyList() }
