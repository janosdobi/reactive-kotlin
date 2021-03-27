package home.dj.kotlinwebsite.util

import home.dj.kotlinwebsite.model.UserDetailsImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class JwtTokenUtilTest {

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Test
    fun generateToken() {
        //given
        val userDetails = UserDetailsImpl()

        //when
        val token = jwtTokenUtil.generateToken(userDetails)
        val usernameFromToken = jwtTokenUtil.getUsernameFromToken(token)

        //then
        assertEquals("test", usernameFromToken)
    }
}