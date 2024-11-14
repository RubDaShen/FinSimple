package     com.rubdashen.finsimple.shared.api.user.request

import      org.json.JSONObject



public final data class UserLoginRequest(
    public val password: String,
    public val email: String
) {
    public fun toJsonString(): String {
        return JSONObject().apply {
            put("password", password)
            put("email", email)
        }.toString()
    }
}