package     com.rubdashen.finsimple.shared.api.user.request

import      org.json.JSONObject



public final data class UserUpdateRequest(
    public val userId: Int,
    public val companyName: String,
    public val email: String,
    public val ruc: String
) {
    public fun toJsonString(): String {
        return JSONObject().apply {
            put("userId", userId)
            put("companyName", companyName)
            put("email", email)
            put("ruc", ruc)
        }.toString()
    }
}