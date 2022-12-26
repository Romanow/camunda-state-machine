package ru.romanow.camunda.utils

import camundajar.impl.com.google.gson.Gson
import camundajar.impl.com.google.gson.GsonBuilder
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

val gson: Gson = GsonBuilder().create()

fun toJson(obj: Any?): String? = gson.toJson(obj)

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> fromJson(json: String?): T {
    val typeClassifier = typeOf<T>().javaType
    return gson.fromJson(json, typeClassifier)
}
