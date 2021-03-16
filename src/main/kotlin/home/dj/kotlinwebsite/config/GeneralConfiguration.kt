package home.dj.kotlinwebsite.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class GeneralConfiguration {

    @Primary
    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
    }
}