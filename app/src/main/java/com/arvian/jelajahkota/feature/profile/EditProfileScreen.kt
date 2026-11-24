package com.arvian.jelajahkota.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaPrimaryButton
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTextField
import com.arvian.jelajahkota.core.designsystem.component.JelajahKotaTopAppBar
import com.arvian.jelajahkota.core.designsystem.theme.JelajahKotaTheme
import com.arvian.jelajahkota.core.designsystem.theme.Lato
import com.arvian.jelajahkota.core.domain.model.UserGender
import com.arvian.jelajahkota.core.domain.validation.ProfileValidator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun EditProfileRoute(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onSaved()
        }
    }

    EditProfileScreen(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onBioChange = viewModel::onBioChange,
        onCityChange = viewModel::onCityChange,
        onBirthDateChange = viewModel::onBirthDateChange,
        onGenderChange = viewModel::onGenderChange,
        onSaveClick = viewModel::saveProfile,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    uiState: ProfileUiState,
    onFullNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var showBirthDateDialog by remember { mutableStateOf(false) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        JelajahKotaTopAppBar(
            title = "Ubah Profil",
            onNavigateBack = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Lengkapi data profil Anda. Selain nama lengkap, semua kolom bersifat opsional dan dapat diperbarui kapan saja.",
                fontFamily = Lato,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Read-only account email (managed by Supabase Auth)
            val accountEmail = uiState.user?.email.orEmpty()
            if (accountEmail.isNotBlank()) {
                JelajahKotaTextField(
                    value = accountEmail,
                    onValueChange = {},
                    label = "Email Akun",
                    enabled = false,
                    readOnly = true,
                    supportingText = "Dikelola otomatis oleh akun",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            JelajahKotaTextField(
                value = uiState.draftFullName,
                onValueChange = onFullNameChange,
                label = "Nama Lengkap *",
                placeholder = "Masukkan nama lengkap",
                isError = uiState.fieldErrors.fullNameError != null,
                errorMessage = uiState.fieldErrors.fullNameError,
                supportingText = "${uiState.draftFullName.length}/${ProfileValidator.MAX_NAME_LENGTH}",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            JelajahKotaTextField(
                value = uiState.draftPhone,
                onValueChange = onPhoneChange,
                label = "Nomor Telepon",
                placeholder = "Contoh: 081234567890 atau +6281234567890",
                isError = uiState.fieldErrors.phoneError != null,
                errorMessage = uiState.fieldErrors.phoneError,
                supportingText = "Opsional • 9–15 digit",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Structured City Dropdown Picker (optional)
            ExposedDropdownMenuBox(
                expanded = cityDropdownExpanded,
                onExpandedChange = { cityDropdownExpanded = !cityDropdownExpanded }
            ) {
                JelajahKotaTextField(
                    value = uiState.draftCity,
                    onValueChange = onCityChange,
                    label = "Kota Domisili",
                    placeholder = "Pilih atau ketik kota domisili (opsional)",
                    isError = uiState.fieldErrors.cityError != null,
                    errorMessage = uiState.fieldErrors.cityError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.draftCity.isNotBlank()) {
                                IconButton(
                                    onClick = { onCityChange("") },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Hapus kota",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Pilih kota",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true)
                )

                val filteredCities = remember(uiState.draftCity) {
                    val q = uiState.draftCity.trim()
                    if (q.isEmpty()) ProfileValidator.CURATED_CITIES
                    else ProfileValidator.CURATED_CITIES.filter { it.contains(q, ignoreCase = true) }
                }

                if (filteredCities.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = cityDropdownExpanded,
                        onDismissRequest = { cityDropdownExpanded = false },
                        containerColor = colorScheme.surface
                    ) {
                        filteredCities.forEach { cityOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = cityOption,
                                        fontFamily = Lato,
                                        fontSize = 14.sp,
                                        color = colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    onCityChange(cityOption)
                                    cityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Structured Birth Date Picker Field (opens Material 3 DatePickerDialog -> ISO DATE in DB)
            val displayBirthDate = remember(uiState.draftBirthDate) {
                ProfileValidator.formatBirthDateForDisplay(uiState.draftBirthDate)
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tanggal Lahir",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    onClick = { showBirthDateDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    color = colorScheme.surfaceContainerLow,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (uiState.fieldErrors.birthDateError != null) {
                            colorScheme.error
                        } else {
                            colorScheme.outlineVariant
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cake,
                            contentDescription = null,
                            tint = if (uiState.fieldErrors.birthDateError != null) {
                                colorScheme.error
                            } else {
                                colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = displayBirthDate.ifBlank { "Pilih tanggal lahir melalui kalender" },
                            fontFamily = Lato,
                            fontSize = 15.sp,
                            color = if (displayBirthDate.isNotBlank()) {
                                colorScheme.onSurface
                            } else {
                                colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.weight(1f)
                        )
                        if (uiState.draftBirthDate.isNotBlank()) {
                            IconButton(
                                onClick = { onBirthDateChange("") },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus tanggal lahir",
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                if (uiState.fieldErrors.birthDateError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.fieldErrors.birthDateError,
                        fontFamily = Lato,
                        fontSize = 12.sp,
                        color = colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Structured Gender Chip Selector (enum-backed: male / female / prefer_not_to_say)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Jenis Kelamin",
                    fontFamily = Lato,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                val activeGender = UserGender.fromRaw(uiState.draftGender)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserGender.entries.forEach { option ->
                        val isSelected = activeGender == option
                        FilterChip(
                            selected = isSelected,
                            onClick = { onGenderChange(option.code) },
                            label = {
                                Text(
                                    text = option.label,
                                    fontFamily = Lato,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colorScheme.primaryContainer,
                                selectedLabelColor = colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                if (uiState.fieldErrors.genderError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.fieldErrors.genderError,
                        fontFamily = Lato,
                        fontSize = 12.sp,
                        color = colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            JelajahKotaTextField(
                value = uiState.draftBio,
                onValueChange = onBioChange,
                label = "Bio",
                placeholder = "Ceritakan sedikit tentang gaya perjalananmu...",
                isError = uiState.fieldErrors.bioError != null,
                errorMessage = uiState.fieldErrors.bioError,
                supportingText = "${uiState.draftBio.length}/${ProfileValidator.MAX_BIO_LENGTH}",
                singleLine = false
            )

            if (uiState.errorMessage != null && !uiState.fieldErrors.hasErrors) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = uiState.errorMessage,
                    fontFamily = Lato,
                    fontSize = 12.sp,
                    color = colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isSaving) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                JelajahKotaPrimaryButton(
                    text = "Simpan Perubahan",
                    onClick = onSaveClick
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showBirthDateDialog) {
        val today = remember { LocalDate.now() }
        val todayEndMillis = remember(today) {
            today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val minMillis = remember {
            ProfileValidator.MIN_BIRTH_DATE.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val initialSelectedMillis = remember(uiState.draftBirthDate) {
            val (iso, _) = ProfileValidator.validateBirthDateIso(uiState.draftBirthDate, today)
            iso?.let {
                runCatching { LocalDate.parse(it).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() }.getOrNull()
            }
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedMillis,
            yearRange = 1900..today.year,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis in minMillis..todayEndMillis

                override fun isSelectableYear(year: Int): Boolean =
                    year in 1900..today.year
            }
        )

        DatePickerDialog(
            onDismissRequest = { showBirthDateDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selected = datePickerState.selectedDateMillis
                        if (selected != null) {
                            val pickedDate = Instant.ofEpochMilli(selected)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            onBirthDateChange(pickedDate.toString())
                        }
                        showBirthDateDialog = false
                    }
                ) {
                    Text(
                        text = "Pilih",
                        fontFamily = Lato,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showBirthDateDialog = false }) {
                    Text(
                        text = "Batal",
                        fontFamily = Lato
                    )
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    JelajahKotaTheme {
        EditProfileScreen(
            uiState = ProfileUiState(draftFullName = "Arvian Diva"),
            onFullNameChange = {},
            onPhoneChange = {},
            onBioChange = {},
            onCityChange = {},
            onBirthDateChange = {},
            onGenderChange = {},
            onSaveClick = {},
            onNavigateBack = {}
        )
    }
}
