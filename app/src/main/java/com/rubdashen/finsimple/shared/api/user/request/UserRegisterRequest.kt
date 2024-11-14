package     com.rubdashen.finsimple.shared.api.user.request

import      org.json.JSONObject



public final data class UserRegisterRequest(
    public val company: String,
    public val email: String,
    public val ruc: String,
    public val password: String
) {
    public fun toJsonString(): String {
        return JSONObject().apply {
            put("company", company)
            put("email", email)
            put("ruc", ruc)
            put("password", password)
        }.toString()
    }
}