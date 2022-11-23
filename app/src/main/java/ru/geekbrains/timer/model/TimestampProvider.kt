package ru.geekbrains.timer.model

interface TimestampProvider {
    fun getMilliseconds(): Long
}