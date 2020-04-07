import com.generatebuilder.Id
import com.generatebuilder.UserBuilder

class ExampleGroovyTest {

    def defaultUser = new UserBuilder()
            .withUserId(new Id("123"))
            .withName("Marcin")
            .withSurname("Moskała")
            .withMoney(null)
            .build()

    def "some method"() {
        def copy = new UserBuilder(defaultUser)
            .withMoney(BigDecimal.valueOf(10))
            .build()
    }
}
