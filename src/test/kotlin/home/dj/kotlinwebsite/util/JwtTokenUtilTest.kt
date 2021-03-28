package home.dj.kotlinwebsite.util

import home.dj.kotlinwebsite.model.UserDetailsImpl
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
        val userDetails = UserDetailsImpl("test", "test")

        //when
        val token = jwtTokenUtil.generateToken(userDetails)
        val usernameFromToken = jwtTokenUtil.getUsernameFromToken(token)

        //then
        assertEquals("test", usernameFromToken)
        assertTrue(jwtTokenUtil.validateToken(token, userDetails))
    }
}