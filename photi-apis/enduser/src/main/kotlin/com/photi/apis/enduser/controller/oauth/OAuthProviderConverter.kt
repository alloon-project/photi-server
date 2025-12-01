package com.photi.apis.enduser.controller.oauth

import com.photi.core.domain.user.exception.UserException
import com.photi.core.domain.user.model.OAuthProviderType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class OAuthProviderConverter : Converter<String, OAuthProviderType> {

    override fun convert(source: String): OAuthProviderType? {
        return try {
            OAuthProviderType.valueOf(source.lowercase())
        } catch (e: IllegalArgumentException) {
            throw UserException.InvalidOAuthProviderException()
        }
    }
}
