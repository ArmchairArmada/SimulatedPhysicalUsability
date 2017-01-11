#version 330

uniform sampler2D texture0;

in vec3 theNormal;
in vec3 theColor;
in vec2 theUv;

out vec4 outColor;

void main() {
    outColor = vec4(texture(texture0, theUv).rgb, 1.0);
}
