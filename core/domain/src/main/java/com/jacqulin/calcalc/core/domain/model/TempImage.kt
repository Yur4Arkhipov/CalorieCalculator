package com.jacqulin.calcalc.core.domain.model

import android.net.Uri
import java.io.File

data class TempImage(
    val uri: Uri,
    val file: File
)