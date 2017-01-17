#define FOG_DISTANCE 500.0
#define FOG_POWER 1.5
#define FOG_COLOR vec4(1,1,1,1)

vec4 mixFog(vec4 position, vec4 color) {
    float theDistance = max(position.z, 0);// / position.w;
    float strength = pow(theDistance / FOG_DISTANCE, FOG_POWER);
    float fogAmount = clamp(strength, 0.0, 1.0);
    return mix(color, FOG_COLOR, fogAmount);
}
