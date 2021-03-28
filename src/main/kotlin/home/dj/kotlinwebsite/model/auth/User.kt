package home.dj.kotlinwebsite.model.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class User(
    private val name: String,
    private val password: String,
    private val authority: String
) : UserDetails {
    override fun getAuthorities() = listOf(GrantedAuthority { authority })

    override fun getPassword() = password

    override fun getUsername() = name

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}
