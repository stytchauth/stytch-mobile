package com.stytch.sdk.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class BootstrapResponse(
    @SerialName("status_code")
    override val statusCode: Int,
    @SerialName("request_id")
    override val requestId: String,
    @SerialName("disable_sdk_watermark")
    public val disableSDKWatermark: Boolean = true,
    @SerialName("cname_domain")
    public val cnameDomain: String? = null,
    @SerialName("email_domains")
    public val emailDomains: List<String> = listOf("stytch.com"),
    @SerialName("captcha_settings")
    public val captchaSettings: CaptchaSettings = CaptchaSettings(),
    @SerialName("pkce_required_for_email_magic_links")
    public val pkceRequiredForEmailMagicLinks: Boolean = false,
    @SerialName("pkce_required_for_password_resets")
    public val pkceRequiredForPasswordResets: Boolean = false,
    @SerialName("pkce_required_for_oauth")
    public val pkceRequiredForOAuth: Boolean = false,
    @SerialName("pkce_required_for_sso")
    public val pkceRequiredForSso: Boolean = false,
    @SerialName("slug_pattern")
    public val slugPattern: String? = null,
    @SerialName("create_organization_enabled")
    public val createOrganizationEnabled: Boolean = false,
    @SerialName("dfp_protected_auth_enabled")
    public val dfpProtectedAuthEnabled: Boolean = false,
    @SerialName("dfp_protected_auth_mode")
    public val dfpProtectedAuthMode: DFPProtectedAuthMode? = DFPProtectedAuthMode.OBSERVATION,
    @SerialName("password_config")
    public val passwordConfig: PasswordConfig? = null,
    @SerialName("rbac_policy")
    public val rbacPolicy: RBACPolicy? = null,
    public val vertical: Vertical = Vertical.CONSUMER,
) : BasicResponse

@Serializable
public class CaptchaSettings(
    public val enabled: Boolean = false,
    public val siteKey: String = "",
)

@Serializable
public class RBACPolicy(
    public val roles: List<RBACPolicyRole>,
    public val resources: List<RBACPolicyResource>,
) {
    public val rolesByID: Map<String, RBACPolicyRole>
        get() {
            val map = mutableMapOf<String, RBACPolicyRole>()
            roles.forEach { role ->
                map[role.roleId] = role
            }
            return map
        }

    public fun callerIsAuthorized(
        memberRoles: List<String>,
        resourceId: String,
        action: String,
    ): Boolean {
        val permission =
            memberRoles
                .mapNotNull { roleId -> rolesByID[roleId] }
                .flatMap { role -> role.permissions }
                .filter { permission -> permission.resourceId == resourceId }
                .find { permission ->
                    permission.actions.contains(action) || permission.actions.contains("*")
                }
        return permission != null
    }

    public fun allPermissionsForCaller(memberRoles: List<String>): Map<String, Map<String, Boolean>> {
        val allPermissionsMap = mutableMapOf<String, Map<String, Boolean>>()
        resources.forEach { resource ->
            val actionMap = mutableMapOf<String, Boolean>()
            resource.actions.forEach { action ->
                actionMap[action] = callerIsAuthorized(memberRoles, resource.resourceId, action)
            }
            allPermissionsMap[resource.resourceId] = actionMap
        }
        return allPermissionsMap
    }
}

@Serializable
public class RBACPolicyRole(
    @SerialName("role_id")
    public val roleId: String,
    @SerialName("description")
    public val roleDescription: String,
    public val permissions: List<RBACPermission>,
)

@Serializable
public class RBACPermission(
    @SerialName("resource_id")
    public val resourceId: String,
    public val actions: List<String>,
)

@Serializable
public class RBACPolicyResource(
    @SerialName("resource_id")
    public val resourceId: String,
    @SerialName("description")
    public val resourceDescription: String,
    public val actions: List<String>,
)

@Serializable
public class PasswordConfig(
    @SerialName("luds_complexity")
    public val ludsComplexity: Int,
    @SerialName("luds_minimum_count")
    public val ludsMinimumCount: Int,
)

@Serializable
public enum class Vertical {
    B2B,
    CONSUMER,
}

@Serializable
public enum class DFPProtectedAuthMode {
    OBSERVATION,
    DECISIONING,
}
