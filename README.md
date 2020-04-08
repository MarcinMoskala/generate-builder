# Kotlin Generate Builder library

This library is experimental now! It might change a lot. 

This librery uses Kotlin annotation processor (kapt) to generate builders for Kotlin classes. Classes needs to have `@GenerateBuilder` annotation. Example:

```kotlin
@GenerateBuilder
class User(
    val userId: Id,
    val name: String,
    val surname: String,
    val money: BigDecimal?
)

class Id(val id: String)
```

Generated class:
```kotlin
class UserBuilder() {
    var userId: Id by Delegates.notNull()

    var name: String by Delegates.notNull()

    var surname: String by Delegates.notNull()

    var money: BigDecimal? = null

    constructor(instance: User) : this() {
        this.userId = instance.userId
        this.name = instance.name
        this.surname = instance.surname
        this.money = instance.money
    }

    constructor(
        userId: Id,
        name: String,
        surname: String,
        money: BigDecimal?
    ) : this() {
        this.userId = userId
        this.name = name
        this.surname = surname
        this.money = money
    }

    fun withUserId(newUserId: Id): UserBuilder = this.apply { this.userId = newUserId }

    fun withName(newName: String): UserBuilder = this.apply { this.name = newName }

    fun withSurname(newSurname: String): UserBuilder = this.apply { this.surname = newSurname }

    fun withMoney(newMoney: BigDecimal?): UserBuilder = this.apply { this.money = newMoney }

    fun build(): User = com.generatebuilder.User(userId, name, surname, money)
}
```

Thanks to that you can create this class using builder in other languages like Groovy or Java (in Kotlin there is no such need as we have default optional parameters and `copy` in data classes).

For instance, you can create a class this way: (in Groovy)
```java
User defaultUser = new UserBuilder()
        .withUserId(new Id("123"))
        .withName("Marcin")
        .withSurname("Moskała")
        .withMoney(null)
        .build()
```

Builders can be used to fill objects only partially:
```java
User makeUserBuilder() = new UserBuilder()
        .withUserId(new Id("123"))
        .withName("Marcin")
        .withSurname("Moskała")
        
// ...
User user1 = makeUserBuilder().withMoney(BigDecimal.ONE).build()
User user2 = makeUserBuilder().withMoney(BigDecimal.TEN).build()
```

One can also make a copy of an object and change only some properties:
```java
User makeUserBuilder() = new UserBuilder(defaultUser)
        .withMoney(BigDecimal.TEN)
        .build()
```

This library is not yet published but it will be once it is stable. For testing purposes, it can be found here:
https://bintray.com/marcinmoskala/MarcinMoskala/GenerateBuilder
https://dl.bintray.com/marcinmoskala/MarcinMoskala/