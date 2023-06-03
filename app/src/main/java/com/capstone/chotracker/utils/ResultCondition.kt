package com.capstone.chotracker.utils

sealed class ResultCondition<out R> private constructor() {
    data class SuccessState<out T>(val data: T) : ResultCondition<T>()
    data class ErrorState(val data: Int) : ResultCondition<Nothing>()
    object LoadingState : ResultCondition<Nothing>()
}