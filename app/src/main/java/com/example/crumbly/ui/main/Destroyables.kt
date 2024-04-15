package com.example.crumbly.ui.main

import javax.inject.Inject
import javax.security.auth.Destroyable

class Destroyables @Inject constructor() {

    private val thingsToDestroy: MutableList<Destroyable> = mutableListOf()

    fun addToDestroy(thingToDestroy: Destroyable) {
        thingsToDestroy.add(thingToDestroy)
    }

    fun destroyAll() {
        thingsToDestroy.forEach { it.destroy() }
    }

}