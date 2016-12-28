#version 330

in vec3 theNormal;
in vec3 theColor;
in vec3 theUv;

out vec4 outColor;

void main() {
    outColor = vec4(theNormal, 1.0);
}
