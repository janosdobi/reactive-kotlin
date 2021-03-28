package home.dj.kotlinwebsite.config

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.spec.InvalidKeySpecException

import java.security.NoSuchAlgorithmException
import java.util.*

import javax.crypto.spec.PBEKeySpec

import javax.crypto.SecretKeyFactory

@Component
class SimplePasswordEncoder(
    private val props: GeneralProperties
) : PasswordEncoder {

    override fun encode(rawPassword: CharSequence?): String {
        return try {
            val result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                .generateSecret(PBEKeySpec(
                    rawPassword.toString().toCharArray(),
                    props.secret.toByteArray(),
                    props.iteration.toInt(),
                    props.keylength.toInt()))
                .encoded
            Base64.getEncoder().encodeToString(result)
        } catch (ex: NoSuchAlgorithmException) {
            throw RuntimeException(ex)
        } catch (ex: InvalidKeySpecException) {
            throw RuntimeException(ex)
        }
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        return encode(rawPassword) == encodedPassword;
    }
}
