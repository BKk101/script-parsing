package com.finches.script.repository

import com.finches.script.model.Script
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface ScriptRepository: PagingAndSortingRepository<Script, Long> {


    override fun findAll(pageable: Pageable): Page<Script>

    override fun findById(id: Long): Optional<Script>

    fun findByRegisterNoAndProjectNoAndFileName(registerNo: Long, projectNo: Long, fileName: String): Script?

    fun findByProjectNoAndEpisode(projectNo: Long, episode: String): List<Script>

    fun findAllByScriptNoIn(scriptNo: List<Long>): List<Script>

    fun findByProjectNo(projectNo: Long): List<Script>

    fun findByProjectNoOrderByScriptNoAsc(projectNo: Long): List<Script>

    fun findByProjectNoOrderByScriptNoDesc(projectNo: Long): List<Script>


}
