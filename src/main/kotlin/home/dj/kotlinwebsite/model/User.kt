package home.dj.kotlinwebsite.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class User(
    private val name: String,
    private val password: String
) : UserDetails {
    override fun getAuthorities() = listOf(GrantedAuthority { "USER" })

    override fun getPassword() = password

    override fun getUsername() = name

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}
