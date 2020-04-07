package com.generatebuilder

import com.marcinmoskala.generatebuilder.annotation.GenerateBuilder
import java.math.BigDecimal

@GenerateBuilder
class User(
    val userId: Id,
    val name: String,
    val surname: String,
    val money: BigDecimal?
)

class Id(val id: String)

fun main() {
    val user = UserBuilder()
        .withUserId(Id("123"))
        .withName("Marcin")
        .withSurname("Moskała")
        .withMoney(null)
        .build()
    print(user)
}