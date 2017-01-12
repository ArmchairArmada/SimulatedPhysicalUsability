#define FOG_DISTANCE 500.0
#define FOG_POWER 1.5
#define FOG_COLOR vec4(1,1,1,1)

vec4 mixFog(vec4 color) {
    float fragDistance = gl_FragCoord.z / gl_FragCoord.w;
    float strength = pow(fragDistance / FOG_DISTANCE, FOG_POWER);
    float fogAmount = clamp(strength, 0.0, 1.0);
    return mix(color, FOG_COLOR, fogAmount);
}
