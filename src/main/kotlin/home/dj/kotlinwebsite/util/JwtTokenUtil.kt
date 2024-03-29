package home.dj.kotlinwebsite.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

private const val JWT_TOKEN_VALIDITY = (2 * 60 * 60).toLong()
private const val UID = "uid"
private const val ROLE = "role"

@Component
class JwtTokenUtil {

    private val publicKey: PublicKey
    private val privateKey: PrivateKey

    init {
        val keyGenerator = KeyPairGenerator.getInstance("RSA")
        keyGenerator.initialize(2048)

        val kp = keyGenerator.genKeyPair()
        publicKey = kp.public as PublicKey
        privateKey = kp.private as PrivateKey
    }

    fun getUidFromToken(token: String) = getClaimFromToken(token) { claims -> claims[UID] as String }

    fun getUsernameFromToken(token: String): String = getClaimFromToken(token, Claims::getSubject)

    fun getRoleFromToken(token: String): String = getClaimFromToken(token) { claims -> claims[ROLE] as String }

    fun getExpirationDateFromToken(token: String): Date = getClaimFromToken(token, Claims::getExpiration)

    fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claimsResolver.invoke(claims)
    }

    fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(privateKey)
            .build()
            .parseClaimsJws(token).body
    }

    fun isTokenExpired(token: String): Boolean {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = mapOf(
            ROLE to userDetails.authorities.first().authority,
            UID to UUID.randomUUID()
        )
        return doGenerateToken(claims, userDetails.username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .compact()
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}