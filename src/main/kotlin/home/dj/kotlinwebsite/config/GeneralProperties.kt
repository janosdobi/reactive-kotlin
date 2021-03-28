package home.dj.kotlinwebsite.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("home.dj")
class GeneralProperties {
    lateinit var password: String
    lateinit var username: String
    lateinit var secret: String
    lateinit var iteration: Integer
    lateinit var keylength: Integer
    lateinit var admin: String
    lateinit var adminPass: String
}