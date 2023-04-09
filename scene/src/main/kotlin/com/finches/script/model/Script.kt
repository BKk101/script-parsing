package com.finches.script.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "script")
data class Script(
    @Id var scriptNo: Long? = null,
    val fileName: String,
    val registerNo: Long? = null,
    val episode: String = "",
    val projectNo: Long,
)
