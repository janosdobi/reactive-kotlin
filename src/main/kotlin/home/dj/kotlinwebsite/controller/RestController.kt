package home.dj.kotlinwebsite.controller

import home.dj.kotlinwebsite.model.PersistentObject
import home.dj.kotlinwebsite.persistence.PersistentObjectRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController(
    private val persistentObjectRepo: PersistentObjectRepository
) {

    @GetMapping("/all")
    fun getAll() = persistentObjectRepo.findAll()

    @PostMapping("/new")
    fun saveNew(@RequestBody entity: PersistentObject) = persistentObjectRepo.save(entity)

    @GetMapping("/delete-all")
    fun deleteAll() = persistentObjectRepo.deleteAll()
}

