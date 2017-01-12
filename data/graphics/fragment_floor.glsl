#version 330

%include partial_fragment_base.glsl

uniform sampler2D texture0;  // Tiles
uniform sampler2D texture1;  // Heatmap colors
uniform sampler2D texture2;  // Heatmap grayscale

in vec2 theUv;

out vec4 outColor;

void main() {
    float value = mix(texture(texture2, theUv).r, texture(texture2, floor(theUv*128)/128).r, 0.5);
    vec4 tileColor = texture(texture0, theUv*128.0);
    vec4 heatColor = texture(texture1, vec2(value,0));
    vec4 color = tileColor * heatColor;
    
    outColor = mixFog(color);
}
