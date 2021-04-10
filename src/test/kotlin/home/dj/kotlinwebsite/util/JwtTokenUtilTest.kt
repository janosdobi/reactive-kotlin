package home.dj.kotlinwebsite.util

import home.dj.kotlinwebsite.model.auth.Role
import home.dj.kotlinwebsite.model.auth.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [JwtTokenUtil::class])
internal class JwtTokenUtilTest {

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Test
    fun generateToken() {
        //given
        val userDetails = User("test", "test", Role.ROLE_USER.name)

        //when
        val token = jwtTokenUtil.generateToken(userDetails)
        val usernameFromToken = jwtTokenUtil.getUsernameFromToken(token)

        //then
        assertEquals("test", usernameFromToken)
        assertTrue(jwtTokenUtil.validateToken(token, userDetails))
    }
}