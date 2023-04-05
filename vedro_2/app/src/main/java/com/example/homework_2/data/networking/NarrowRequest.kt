package com.example.homework_2.data.networking

class NarrowRequest (
    val operator: String,
    val operand: String
) {

    override fun toString(): String {
        return "{\"operator\": \"$operator\", \"operand\": \"$operand\"}"
    }
}
