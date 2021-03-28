package home.dj.kotlinwebsite.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("home.dj")
class GeneralProperties {
    lateinit var password: String
    lateinit var username: String
}