package com.vaibhav.distributedjobscheduler.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun stringRedisTemplate(connectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate(connectionFactory)
        val stringSerializer = StringRedisSerializer()

        template.keySerializer = stringSerializer
        template.valueSerializer = stringSerializer
        template.hashKeySerializer = stringSerializer
        template.hashValueSerializer = stringSerializer

        template.afterPropertiesSet()
        return template
    }
}