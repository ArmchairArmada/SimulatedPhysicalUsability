#version 330

uniform sampler2D texture0;  // Tiles
uniform sampler2D texture1;  // Heatmap colors
uniform sampler2D texture2;  // Heatmap grayscale

in vec2 theUv;

out vec4 outColor;

void main() {
    float value = texture2D(texture2, theUv);
    vec4 tileColor = texture2D(texture0, theUv*128.0);
    vec4 heatColor = texture2D(texture1, vec2(value,0));
    vec4 color = tileColor * heatColor;
    
    float fog = clamp(pow(gl_FragCoord.z/gl_FragCoord.w/240.0, 1.5), 0.0, 1.0);
    
    outColor = mix(color, vec4(1,1,1,1), fog);
}
