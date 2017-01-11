#version 330

uniform sampler2D texture0;

in vec2 theUv;

out vec4 outColor;

void main() {
    vec4 color = texture(texture0, theUv);
    float fog = clamp(pow(gl_FragCoord.z/gl_FragCoord.w/240.0, 1.5), 0.0, 1.0);
    
    outColor = mix(color, vec4(1,1,1,1), fog);
}
